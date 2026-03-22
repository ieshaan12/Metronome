package com.ieshaan12.metronome.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ieshaan12.metronome.R
import com.ieshaan12.metronome.ui.theme.MetronomeTheme

@Composable
fun BeatIndicator(
    currentBeat: Int,
    beatsPerMeasure: Int,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.beat_indicator_format, currentBeat + 1, beatsPerMeasure)
    Row(
        modifier = modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(beatsPerMeasure) { index ->
            val isActive = isPlaying && index == currentBeat
            val color by animateColorAsState(
                targetValue = if (isActive) MaterialTheme.colorScheme.primary
                else Color.Transparent,
                label = "dotColor",
            )
            val scale by animateFloatAsState(
                targetValue = if (isActive) 1.2f else 1f,
                label = "dotScale",
            )
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .scale(scale)
                    .testTag("beat_dot_$index")
                    .then(
                        if (isActive) Modifier.background(color, CircleShape)
                        else Modifier
                            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            .background(color, CircleShape)
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BeatIndicatorPlayingPreview() {
    MetronomeTheme {
        BeatIndicator(currentBeat = 2, beatsPerMeasure = 4, isPlaying = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun BeatIndicatorStoppedPreview() {
    MetronomeTheme {
        BeatIndicator(currentBeat = 0, beatsPerMeasure = 4, isPlaying = false)
    }
}
