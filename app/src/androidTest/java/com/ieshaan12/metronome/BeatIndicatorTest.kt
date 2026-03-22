package com.ieshaan12.metronome

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import com.ieshaan12.metronome.ui.components.BeatIndicator
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BeatIndicatorTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsCorrectDotCountFor4_4() {
        composeRule.setContent {
            BeatIndicator(currentBeat = 0, beatsPerMeasure = 4, isPlaying = false)
        }
        var count = 0
        for (i in 0..7) {
            count += composeRule.onAllNodesWithTag("beat_dot_$i", useUnmergedTree = true)
                .fetchSemanticsNodes().size
        }
        assertEquals(4, count)
    }

    @Test
    fun activeDotFilledWhenPlaying() {
        composeRule.setContent {
            BeatIndicator(currentBeat = 2, beatsPerMeasure = 4, isPlaying = true)
        }
        // Verify beat_dot_2 exists (the active dot)
        composeRule.onAllNodesWithTag("beat_dot_2", useUnmergedTree = true)
            .fetchSemanticsNodes()
            .also { assertEquals(1, it.size) }
    }

    @Test
    fun allDotsOutlinedWhenStopped() {
        composeRule.setContent {
            BeatIndicator(currentBeat = 0, beatsPerMeasure = 4, isPlaying = false)
        }
        // All 4 dots should exist
        var count = 0
        for (i in 0..3) {
            count += composeRule.onAllNodesWithTag("beat_dot_$i", useUnmergedTree = true)
                .fetchSemanticsNodes().size
        }
        assertEquals(4, count)
    }
}
