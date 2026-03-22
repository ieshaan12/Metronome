package com.ieshaan12.metronome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class MetronomeScreenTest {

    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(DetectLeaksAfterTestSuccess())
        .around(composeRule)

    @Test
    fun playButtonShowsPlayInitially() {
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed()
    }

    @Test
    fun tappingPlayChangesToPause() {
        composeRule.onNodeWithContentDescription("Play").performClick()
        composeRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
    }

    @Test
    fun plusOneIncrementsBpmDisplay() {
        composeRule.onNodeWithContentDescription("Increase BPM").performClick()
        composeRule.onNodeWithTag("bpm_display").assertIsDisplayed()
        composeRule.onNodeWithText("121").assertIsDisplayed()
    }

    @Test
    fun minusOneDecrementsBpmDisplay() {
        composeRule.onNodeWithContentDescription("Decrease BPM").performClick()
        composeRule.onNodeWithTag("bpm_display").assertIsDisplayed()
        composeRule.onNodeWithText("119").assertIsDisplayed()
    }

    @Test
    fun selecting3_4Shows3Dots() {
        composeRule.onNodeWithContentDescription("Time signature 3/4").performClick()
        composeRule.waitForIdle()
        val dots = composeRule.onAllNodesWithTag("beat_dot_0", useUnmergedTree = true)
            .fetchSemanticsNodes()
        // Count all beat_dot_ nodes
        var count = 0
        for (i in 0..7) {
            val nodes = composeRule.onAllNodesWithTag("beat_dot_$i", useUnmergedTree = true)
                .fetchSemanticsNodes()
            count += nodes.size
        }
        assert(count == 3) { "Expected 3 dots but found $count" }
    }

    @Test
    fun selecting6_8Shows6Dots() {
        composeRule.onNodeWithContentDescription("Time signature 6/8").performClick()
        composeRule.waitForIdle()
        var count = 0
        for (i in 0..7) {
            val nodes = composeRule.onAllNodesWithTag("beat_dot_$i", useUnmergedTree = true)
                .fetchSemanticsNodes()
            count += nodes.size
        }
        assert(count == 6) { "Expected 6 dots but found $count" }
    }

    @Test
    fun playAndPauseCycle() {
        // Play
        composeRule.onNodeWithContentDescription("Play").performClick()
        composeRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
        // Pause
        composeRule.onNodeWithContentDescription("Pause").performClick()
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed()
    }

    @Test
    fun bpmChangeWhilePlayingStopsMetronome() {
        composeRule.onNodeWithContentDescription("Play").performClick()
        composeRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
        // Change BPM via +1
        composeRule.onNodeWithContentDescription("Increase BPM").performClick()
        composeRule.waitForIdle()
        // Should have stopped — play button shows Play again
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed()
        composeRule.onNodeWithText("121").assertIsDisplayed()
    }

    @Test
    fun bpmDecrementWhilePlayingStopsMetronome() {
        composeRule.onNodeWithContentDescription("Play").performClick()
        composeRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Decrease BPM").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed()
        composeRule.onNodeWithText("119").assertIsDisplayed()
    }

    @Test
    fun timeSignatureChangeWhilePlayingStopsMetronome() {
        composeRule.onNodeWithContentDescription("Play").performClick()
        composeRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
        // Change time signature to 3/4
        composeRule.onNodeWithContentDescription("Time signature 3/4").performClick()
        composeRule.waitForIdle()
        // Should have stopped
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed()
        // Should show 3 dots
        var count = 0
        for (i in 0..7) {
            count += composeRule.onAllNodesWithTag("beat_dot_$i", useUnmergedTree = true)
                .fetchSemanticsNodes().size
        }
        assert(count == 3) { "Expected 3 dots but found $count" }
    }

    @Test
    fun bpmChangeWhileStoppedDoesNotAutoPlay() {
        // Not playing — change BPM
        composeRule.onNodeWithContentDescription("Increase BPM").performClick()
        composeRule.waitForIdle()
        // Should still show Play (not Pause)
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed()
        composeRule.onNodeWithText("121").assertIsDisplayed()
    }
}
