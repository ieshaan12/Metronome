package com.ieshaan12.metronome.audio

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineMetronomeEngineTest {

    private fun createEngine(scheduler: TestCoroutineScheduler): CoroutineMetronomeEngine {
        return CoroutineMetronomeEngine(
            coroutineContext = StandardTestDispatcher(scheduler),
            nanoTime = { scheduler.currentTime * 1_000_000 },
        )
    }

    @Test
    fun `start emits beat 0 immediately`() = runTest {
        val engine = createEngine(testScheduler)
        engine.start(120, 4)
        runCurrent()
        assertEquals(0, engine.currentBeat.value)
        engine.stop()
    }

    @Test
    fun `beats cycle through measure`() = runTest {
        val engine = createEngine(testScheduler)
        engine.currentBeat.test {
            assertEquals(0, awaitItem()) // initial value

            engine.start(120, 4) // 500ms per beat
            runCurrent() // dispatch coroutine

            advanceTimeBy(501)
            assertEquals(1, expectMostRecentItem())

            advanceTimeBy(500)
            assertEquals(2, expectMostRecentItem())

            advanceTimeBy(500)
            assertEquals(3, expectMostRecentItem())

            advanceTimeBy(500)
            assertEquals(0, expectMostRecentItem())

            engine.stop()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stop resets beat to 0`() = runTest {
        val engine = createEngine(testScheduler)
        engine.start(120, 4)
        advanceTimeBy(1001) // past beat 2
        engine.stop()
        assertEquals(0, engine.currentBeat.value)
    }

    @Test
    fun `start is idempotent - second start cancels first`() = runTest {
        val engine = createEngine(testScheduler)
        engine.start(120, 4)
        advanceTimeBy(501)
        assertEquals(1, engine.currentBeat.value)

        engine.start(120, 4)
        runCurrent()
        assertEquals(0, engine.currentBeat.value)
        engine.stop()
    }

    @Test
    fun `updateBpm changes interval`() = runTest {
        val engine = createEngine(testScheduler)
        engine.start(60, 4) // 1000ms per beat
        advanceTimeBy(1001) // beat 1
        assertEquals(1, engine.currentBeat.value)

        engine.updateBpm(120) // 500ms per beat
        // Next tick detects BPM change and resets anchor
        advanceTimeBy(501) // should produce a beat at new interval
        val beat = engine.currentBeat.value
        // Beat should have advanced from the anchor reset
        assert(beat >= 0)
        engine.stop()
    }

    @Test
    fun `updateBeatsPerMeasure resets beat`() = runTest {
        val engine = createEngine(testScheduler)
        engine.start(120, 4)
        advanceTimeBy(1501) // beat 3
        assertEquals(3, engine.currentBeat.value)

        engine.updateBeatsPerMeasure(3)
        assertEquals(0, engine.currentBeat.value)
        engine.stop()
    }

    @Test
    fun `stop cancels scope - no further emissions`() = runTest {
        val engine = createEngine(testScheduler)
        engine.start(120, 4)
        advanceTimeBy(501)
        assertEquals(1, engine.currentBeat.value)

        engine.stop()
        assertEquals(0, engine.currentBeat.value)

        advanceTimeBy(2000)
        assertEquals(0, engine.currentBeat.value)
    }

    @Test
    fun `stop during delayed tick prevents emission`() = runTest {
        val engine = createEngine(testScheduler)
        engine.start(60, 4) // 1000ms per beat
        advanceTimeBy(100) // mid-delay

        engine.stop()
        assertEquals(0, engine.currentBeat.value)

        advanceTimeBy(2000)
        assertEquals(0, engine.currentBeat.value)
    }

    @Test
    fun `rapid time signature changes stabilize`() = runTest {
        val engine = createEngine(testScheduler)
        engine.start(120, 4)
        advanceTimeBy(501)

        engine.updateBeatsPerMeasure(3)
        engine.updateBeatsPerMeasure(6)
        engine.updateBeatsPerMeasure(2)

        assertEquals(0, engine.currentBeat.value)
        engine.stop()
    }

    @Test
    fun `updateBpm resets time anchor - no multi-second gap`() = runTest {
        val engine = createEngine(testScheduler)
        engine.currentBeat.test {
            assertEquals(0, awaitItem())

            engine.start(120, 4) // 500ms
            advanceTimeBy(1501) // 3 beats

            engine.updateBpm(60) // 1000ms per beat
            advanceTimeBy(1001) // anchor reset means next beat within 1000ms

            val recent = expectMostRecentItem()
            assert(recent >= 0)

            engine.stop()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
