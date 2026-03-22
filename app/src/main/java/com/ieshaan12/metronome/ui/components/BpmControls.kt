package com.ieshaan12.metronome.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ieshaan12.metronome.R
import com.ieshaan12.metronome.ui.theme.MetronomeTheme

@Composable
fun BpmControls(
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onDecrementFive: () -> Unit,
    onIncrementFive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val decreaseDesc = stringResource(R.string.decrease_bpm)
    val increaseDesc = stringResource(R.string.increase_bpm)
    val decreaseFiveDesc = stringResource(R.string.decrease_bpm_five)
    val increaseFiveDesc = stringResource(R.string.increase_bpm_five)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilledTonalButton(
            onClick = onDecrementFive,
            modifier = Modifier.semantics { contentDescription = decreaseFiveDesc },
        ) {
            Text("−5")
        }
        FilledTonalButton(
            onClick = onDecrement,
            modifier = Modifier.semantics { contentDescription = decreaseDesc },
        ) {
            Text("−1")
        }
        FilledTonalButton(
            onClick = onIncrement,
            modifier = Modifier.semantics { contentDescription = increaseDesc },
        ) {
            Text("+1")
        }
        FilledTonalButton(
            onClick = onIncrementFive,
            modifier = Modifier.semantics { contentDescription = increaseFiveDesc },
        ) {
            Text("+5")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BpmControlsPreview() {
    MetronomeTheme {
        BpmControls(
            onDecrement = {},
            onIncrement = {},
            onDecrementFive = {},
            onIncrementFive = {},
        )
    }
}
