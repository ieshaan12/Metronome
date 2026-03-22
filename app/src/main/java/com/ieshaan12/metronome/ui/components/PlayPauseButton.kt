package com.ieshaan12.metronome.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ieshaan12.metronome.R
import com.ieshaan12.metronome.ui.theme.MetronomeTheme
import androidx.compose.ui.res.painterResource

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LargeFloatingActionButton(
        onClick = onToggle,
        modifier = modifier.size(96.dp),
        elevation = FloatingActionButtonDefaults.elevation(),
    ) {
        Crossfade(targetState = isPlaying, label = "playPause") { playing ->
            if (playing) {
                Icon(
                    painter = painterResource(R.drawable.ic_pause),
                    contentDescription = stringResource(R.string.pause),
                    modifier = Modifier.size(48.dp),
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.play),
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayPauseButtonStoppedPreview() {
    MetronomeTheme {
        PlayPauseButton(isPlaying = false, onToggle = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayPauseButtonPlayingPreview() {
    MetronomeTheme {
        PlayPauseButton(isPlaying = true, onToggle = {})
    }
}
