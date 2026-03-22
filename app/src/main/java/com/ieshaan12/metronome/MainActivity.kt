package com.ieshaan12.metronome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ieshaan12.metronome.audio.AudioTrackMetronomeEngine
import com.ieshaan12.metronome.audio.HapticTickCallback
import com.ieshaan12.metronome.ui.MetronomeScreen
import com.ieshaan12.metronome.ui.MetronomeViewModel
import com.ieshaan12.metronome.ui.MetronomeViewModelFactory
import com.ieshaan12.metronome.ui.theme.MetronomeTheme
import com.ieshaan12.metronome.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {

    private val viewModel: MetronomeViewModel by viewModels {
        MetronomeViewModelFactory(AudioTrackMetronomeEngine(
            tickCallback = HapticTickCallback(this@MainActivity),
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeMode by remember {
                mutableStateOf(
                    if (resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                        android.content.res.Configuration.UI_MODE_NIGHT_YES
                    ) ThemeMode.Dark else ThemeMode.Light
                )
            }
            MetronomeTheme(themeMode = themeMode) {
                MetronomeScreen(
                    viewModel = viewModel,
                    themeMode = themeMode,
                    onThemeToggle = {
                        themeMode = when (themeMode) {
                            ThemeMode.Dark -> ThemeMode.Light
                            ThemeMode.Light -> ThemeMode.Dark
                        }
                    },
                )
            }
        }
    }
}