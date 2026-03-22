package com.ieshaan12.metronome.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ieshaan12.metronome.R
import com.ieshaan12.metronome.model.tempoMarking
import com.ieshaan12.metronome.ui.components.AboutDialog
import com.ieshaan12.metronome.ui.components.LicensesScreen
import com.ieshaan12.metronome.ui.components.BeatIndicator
import com.ieshaan12.metronome.ui.components.BpmControls
import com.ieshaan12.metronome.ui.components.BpmDisplay
import com.ieshaan12.metronome.ui.components.BpmSlider
import com.ieshaan12.metronome.ui.components.PlayPauseButton
import com.ieshaan12.metronome.ui.components.TimeSignaturePicker
import com.ieshaan12.metronome.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetronomeScreen(
    viewModel: MetronomeViewModel,
    themeMode: ThemeMode,
    onThemeToggle: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAbout by remember { mutableStateOf(false) }
    var showLicenses by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    KeepScreenOn(uiState.isPlaying)
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { viewModel.pause() }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
    if (showLicenses) {
        LicensesScreen(onBack = { showLicenses = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        val iconRes = when (themeMode) {
                            ThemeMode.Dark -> R.drawable.ic_theme_dark
                            ThemeMode.Light -> R.drawable.ic_theme_light
                        }
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = stringResource(R.string.toggle_theme),
                        )
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_options),
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.widthIn(min = 180.dp),
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.about)) },
                            onClick = {
                                showMenu = false
                                showAbout = true
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.licenses)) },
                            onClick = {
                                showMenu = false
                                showLicenses = true
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
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
                onDecrementFive = { viewModel.setBpm(uiState.bpm - 5) },
                onIncrementFive = { viewModel.setBpm(uiState.bpm + 5) },
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


