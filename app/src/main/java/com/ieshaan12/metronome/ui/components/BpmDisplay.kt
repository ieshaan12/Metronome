package com.ieshaan12.metronome.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ieshaan12.metronome.R
import com.ieshaan12.metronome.ui.theme.MetronomeTheme

@Composable
fun BpmDisplay(
    bpm: Int,
    tempoMarking: String,
    onBpmInput: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEditing by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf(TextFieldValue()) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = tempoMarking,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )

        if (isEditing) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            BasicTextField(
                value = editValue,
                onValueChange = { newValue ->
                    editValue = newValue.copy(
                        text = newValue.text.filter { c -> c.isDigit() },
                    )
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .testTag("bpm_input"),
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.displayLarge.fontSize,
                    fontWeight = MaterialTheme.typography.displayLarge.fontWeight,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val parsed = editValue.text.toIntOrNull()
                        if (parsed != null) onBpmInput(parsed)
                        isEditing = false
                        focusManager.clearFocus()
                    },
                ),
                singleLine = true,
            )
        } else {
            Text(
                text = bpm.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .testTag("bpm_display")
                    .clickable {
                        val text = bpm.toString()
                        editValue = TextFieldValue(
                            text = text,
                            selection = TextRange(text.length),
                        )
                        isEditing = true
                    },
            )
        }

        Text(
            text = stringResource(R.string.bpm_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BpmDisplay120Preview() {
    MetronomeTheme {
        BpmDisplay(bpm = 120, tempoMarking = "Allegro", onBpmInput = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BpmDisplay60Preview() {
    MetronomeTheme {
        BpmDisplay(bpm = 60, tempoMarking = "Largo", onBpmInput = {})
    }
}
