package com.ieshaan12.metronome.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ieshaan12.metronome.R
import com.ieshaan12.metronome.model.TimeSignature
import com.ieshaan12.metronome.ui.theme.MetronomeTheme

@Composable
fun TimeSignaturePicker(
    selected: TimeSignature,
    onSelect: (TimeSignature) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TimeSignature.entries.forEach { entry ->
            val desc = stringResource(R.string.time_signature_format, entry.label)
            FilterChip(
                selected = entry == selected,
                onClick = { onSelect(entry) },
                label = { Text(entry.label) },
                modifier = Modifier.semantics { contentDescription = desc },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeSignaturePickerPreview() {
    MetronomeTheme {
        TimeSignaturePicker(selected = TimeSignature.FOUR_FOUR, onSelect = {})
    }
}
