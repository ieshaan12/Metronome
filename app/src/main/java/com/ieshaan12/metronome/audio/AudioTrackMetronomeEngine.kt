package com.ieshaan12.metronome.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicReference

class AudioTrackMetronomeEngine(
    private val tickCallback: TickCallback? = null,
    private val sampleRate: Int = 44100,
    private val chunkSize: Int = 1024,
) : MetronomeEngine {

    private data class TickConfig(val bpm: Int, val beatsPerMeasure: Int)

    private val _currentBeat = MutableStateFlow(0)
    override val currentBeat: StateFlow<Int> = _currentBeat.asStateFlow()

    private val config = AtomicReference(TickConfig(120, 4))
    private val lock = Any()

    @Volatile
    private var running = false
    private var audioThread: Thread? = null
    private var callbackThread: HandlerThread? = null
    private var callbackHandler: Handler? = null

    private val normalClick = ClickSynthesizer.generate(
        sampleRate = sampleRate,
        frequencyHz = 800.0,
        durationMs = 10,
    )
    private val accentClick = ClickSynthesizer.generate(
        sampleRate = sampleRate,
        frequencyHz = 1200.0,
        durationMs = 15,
    )

    override fun start(bpm: Int, beatsPerMeasure: Int) {
        synchronized(lock) {
            stopInternal()
            config.set(TickConfig(bpm, beatsPerMeasure))
            running = true

            // Dedicated handler thread for callbacks — keeps audio thread pristine
            val ht = HandlerThread("MetronomeCallback").apply { start() }
            callbackThread = ht
            val handler = Handler(ht.looper)
            callbackHandler = handler

            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
            )

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioThread = Thread({
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
                audioTrack.play()

                val buffer = ShortArray(chunkSize)
                var sampleCounter = 0L
                var beat = 0
                var clickOffset = -1
                var currentClick = accentClick

                var snap = config.get()
                var intervalSamples = (sampleRate.toLong() * 60) / snap.bpm

                _currentBeat.value = 0
                handler.post { tickCallback?.onTick(0, snap.beatsPerMeasure) }

                while (running) {
                    snap = config.get()
                    intervalSamples = (sampleRate.toLong() * 60) / snap.bpm

                    var pendingBeat = -1
                    var pendingBeatSamplePos = 0L
                    val pendingBeatsPerMeasure = snap.beatsPerMeasure

                    for (i in 0 until chunkSize) {
                        if (sampleCounter != 0L && sampleCounter % intervalSamples == 0L) {
                            beat = ((sampleCounter / intervalSamples) % snap.beatsPerMeasure).toInt()
                            currentClick = if (beat == 0) accentClick else normalClick
                            clickOffset = 0
                            pendingBeat = beat
                            pendingBeatSamplePos = sampleCounter
                        }

                        buffer[i] = if (clickOffset in 0 until currentClick.size) {
                            currentClick[clickOffset++]
                        } else {
                            clickOffset = -1
                            0
                        }

                        sampleCounter++
                    }

                    // Blocking write — this IS the clock. Nothing else on this thread.
                    audioTrack.write(buffer, 0, chunkSize)

                    if (pendingBeat >= 0) {
                        _currentBeat.value = pendingBeat
                        val b = pendingBeat
                        handler.post { tickCallback?.onTick(b, pendingBeatsPerMeasure) }
                    }
                }

                audioTrack.stop()
                audioTrack.release()
            }, "MetronomeAudio").apply { start() }
        }
    }

    override fun stop() {
        synchronized(lock) {
            stopInternal()
        }
    }

    private fun stopInternal() {
        running = false
        audioThread?.join(500)
        audioThread = null
        callbackThread?.quitSafely()
        callbackThread = null
        callbackHandler = null
        _currentBeat.value = 0
    }

    override fun updateBpm(bpm: Int) {
        config.set(config.get().copy(bpm = bpm))
    }

    override fun updateBeatsPerMeasure(beatsPerMeasure: Int) {
        config.set(config.get().copy(beatsPerMeasure = beatsPerMeasure))
        _currentBeat.value = 0
    }
}
