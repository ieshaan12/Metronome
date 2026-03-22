package com.ieshaan12.metronome

import com.ieshaan12.metronome.debug.DebugTools
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DebugToolsTraceTest {

    @Test
    fun traceSectionCompletesWithoutCrashInDebug() {
        // In debug builds, Trace.beginSection/endSection should not throw
        var executed = false
        DebugTools.trace("test-section") {
            executed = true
        }
        assertTrue(executed)
    }

    @Test
    fun traceWrapsBlockCorrectly() {
        val result = DebugTools.trace("label") { 42 }
        assertEquals(42, result)
    }
}
