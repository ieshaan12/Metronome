package com.ieshaan12.metronome.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeSignatureTest {

    @Test
    fun `FOUR_FOUR has 4 beats`() {
        assertEquals(4, TimeSignature.FOUR_FOUR.beatsPerMeasure)
    }

    @Test
    fun `SIX_EIGHT has noteValue 8`() {
        assertEquals(8, TimeSignature.SIX_EIGHT.noteValue)
    }

    @Test
    fun `labels are correct`() {
        assertEquals("2/4", TimeSignature.TWO_FOUR.label)
        assertEquals("3/4", TimeSignature.THREE_FOUR.label)
        assertEquals("4/4", TimeSignature.FOUR_FOUR.label)
        assertEquals("6/8", TimeSignature.SIX_EIGHT.label)
        assertEquals("8/8", TimeSignature.EIGHT_EIGHT.label)
    }

    @Test
    fun `entries count is 5`() {
        assertEquals(5, TimeSignature.entries.size)
    }
}
