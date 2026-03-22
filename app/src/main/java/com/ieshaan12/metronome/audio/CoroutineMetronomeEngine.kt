package com.ieshaan12.metronome.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

class CoroutineMetronomeEngine(
    private val coroutineContext: CoroutineContext = Dispatchers.Default,
    private val nanoTime: () -> Long = System::nanoTime,
    private val tickCallback: TickCallback? = null,
) : MetronomeEngine {

    private data class TickConfig(val bpm: Int, val beatsPerMeasure: Int)

    private val _currentBeat = MutableStateFlow(0)
    override val currentBeat: StateFlow<Int> = _currentBeat.asStateFlow()

    private val config = AtomicReference(TickConfig(120, 4))
    private val lock = Any()
    private var scope: CoroutineScope? = null
    private var tickJob: Job? = null

    override fun start(bpm: Int, beatsPerMeasure: Int) {
        synchronized(lock) {
            scope?.let {
                it.coroutineContext[Job]?.cancel()
            }
            config.set(TickConfig(bpm, beatsPerMeasure))
            val newScope = CoroutineScope(SupervisorJob() + coroutineContext)
            scope = newScope
            tickJob = newScope.launch {
                var snap = config.get()
                var intervalNs = 60_000_000_000L / snap.bpm
                var startNs = nanoTime()
                _currentBeat.value = 0
                tickCallback?.onTick(0, snap.beatsPerMeasure)
                var n = 1L
                var lastBpm = snap.bpm

                while (isActive) {
                    val nextTickNs = startNs + n * intervalNs
                    val delayMs = ((nextTickNs - nanoTime()).coerceAtLeast(0)) / 1_000_000
                    delay(delayMs)

                    if (!isActive) break

                    snap = config.get()
                    val beat = (n % snap.beatsPerMeasure).toInt()
                    _currentBeat.value = beat
                    tickCallback?.onTick(beat, snap.beatsPerMeasure)

                    if (snap.bpm != lastBpm) {
                        startNs = nanoTime()
                        n = 0
                        intervalNs = 60_000_000_000L / snap.bpm
                        lastBpm = snap.bpm
                    }

                    n++
                }
            }
        }
    }

    override fun stop() {
        synchronized(lock) {
            scope?.coroutineContext?.get(Job)?.cancel()
            _currentBeat.value = 0
            tickJob = null
            scope = null
        }
    }

    override fun updateBpm(bpm: Int) {
        config.set(config.get().copy(bpm = bpm))
    }

    override fun updateBeatsPerMeasure(beatsPerMeasure: Int) {
        config.set(config.get().copy(beatsPerMeasure = beatsPerMeasure))
        _currentBeat.value = 0
    }
}
