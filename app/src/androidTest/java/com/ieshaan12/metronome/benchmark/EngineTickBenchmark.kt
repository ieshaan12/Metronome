package com.ieshaan12.metronome.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ieshaan12.metronome.audio.CoroutineMetronomeEngine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EngineTickBenchmark {

    @Test
    fun engineStartStopTiming() {
        val engine = CoroutineMetronomeEngine()
        val start = System.nanoTime()

        engine.start(120, 4)
        // Wait for first non-zero beat emission
        runBlocking {
            engine.currentBeat.drop(1).first()
        }
        engine.stop()

        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        // At 120 BPM, first beat after beat 0 should arrive within ~600ms (500ms + tolerance)
        assertTrue("Engine tick took ${elapsedMs}ms, expected < 700ms", elapsedMs < 700)
    }
}
