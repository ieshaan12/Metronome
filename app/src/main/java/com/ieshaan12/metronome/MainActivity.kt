package com.ieshaan12.metronome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.ieshaan12.metronome.audio.AudioTrackMetronomeEngine
import com.ieshaan12.metronome.audio.HapticTickCallback
import com.ieshaan12.metronome.ui.MetronomeScreen
import com.ieshaan12.metronome.ui.MetronomeViewModel
import com.ieshaan12.metronome.ui.MetronomeViewModelFactory
import com.ieshaan12.metronome.ui.theme.MetronomeTheme

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
            MetronomeTheme {
                MetronomeScreen(viewModel)
            }
        }
    }
}