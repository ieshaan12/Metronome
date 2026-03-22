package com.ieshaan12.metronome.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ieshaan12.metronome.model.tempoMarking
import com.ieshaan12.metronome.ui.components.BeatIndicator
import com.ieshaan12.metronome.ui.components.BpmControls
import com.ieshaan12.metronome.ui.components.BpmDisplay
import com.ieshaan12.metronome.ui.components.BpmSlider
import com.ieshaan12.metronome.ui.components.PlayPauseButton
import com.ieshaan12.metronome.ui.components.TimeSignaturePicker

@Composable
fun MetronomeScreen(viewModel: MetronomeViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KeepScreenOn(uiState.isPlaying)
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { viewModel.pause() }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TimeSignaturePicker(
                selected = uiState.timeSignature,
                onSelect = { viewModel.setTimeSignature(it) },
            )
            BeatIndicator(
                currentBeat = uiState.currentBeat,
                beatsPerMeasure = uiState.timeSignature.beatsPerMeasure,
                isPlaying = uiState.isPlaying,
            )
            BpmDisplay(
                bpm = uiState.bpm,
                tempoMarking = tempoMarking(uiState.bpm),
                onBpmInput = { viewModel.setBpm(it) },
            )
            BpmSlider(
                bpm = uiState.bpm,
                onBpmChange = { viewModel.setBpm(it) },
            )
            BpmControls(
                onDecrement = { viewModel.setBpm(uiState.bpm - 1) },
                onIncrement = { viewModel.setBpm(uiState.bpm + 1) },
            )
            PlayPauseButton(
                isPlaying = uiState.isPlaying,
                onToggle = { if (uiState.isPlaying) viewModel.pause() else viewModel.play() },
            )
        }
    }
}

@Composable
private fun KeepScreenOn(enabled: Boolean) {
    val activity = LocalContext.current as? Activity ?: return
    DisposableEffect(enabled) {
        if (enabled) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}


