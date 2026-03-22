package com.ieshaan12.metronome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.ieshaan12.metronome.ui.components.BpmControls
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BpmControlsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun incrementButtonExists() {
        composeRule.setContent {
            BpmControls(onDecrement = {}, onIncrement = {})
        }
        composeRule.onNodeWithContentDescription("Increase BPM").assertIsDisplayed()
    }

    @Test
    fun decrementButtonExists() {
        composeRule.setContent {
            BpmControls(onDecrement = {}, onIncrement = {})
        }
        composeRule.onNodeWithContentDescription("Decrease BPM").assertIsDisplayed()
    }

    @Test
    fun callbackFiresOnClick() {
        var incrementCalled = false
        composeRule.setContent {
            BpmControls(onDecrement = {}, onIncrement = { incrementCalled = true })
        }
        composeRule.onNodeWithContentDescription("Increase BPM").performClick()
        assertTrue(incrementCalled)
    }
}
