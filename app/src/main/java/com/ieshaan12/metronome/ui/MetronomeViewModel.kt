package com.ieshaan12.metronome.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ieshaan12.metronome.audio.MetronomeEngine
import com.ieshaan12.metronome.model.TimeSignature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val BPM_MIN = 20
const val BPM_MAX = 300
const val DEFAULT_BPM = 120

data class MetronomeUiState(
    val bpm: Int = DEFAULT_BPM,
    val timeSignature: TimeSignature = TimeSignature.FOUR_FOUR,
    val isPlaying: Boolean = false,
    val currentBeat: Int = 0,
)

class MetronomeViewModel(
    private val engine: MetronomeEngine,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetronomeUiState())
    val uiState: StateFlow<MetronomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            engine.currentBeat.collect { beat ->
                _uiState.update { it.copy(currentBeat = beat) }
            }
        }
    }

    fun play() {
        val state = _uiState.value
        engine.start(state.bpm, state.timeSignature.beatsPerMeasure)
        _uiState.update { it.copy(isPlaying = true) }
    }

    fun pause() {
        engine.stop()
        _uiState.update { it.copy(isPlaying = false) }
    }

    fun setBpm(value: Int) {
        val clamped = value.coerceIn(BPM_MIN, BPM_MAX)
        if (_uiState.value.isPlaying) {
            engine.stop()
            _uiState.update { it.copy(bpm = clamped, isPlaying = false) }
        } else {
            _uiState.update { it.copy(bpm = clamped) }
        }
    }

    fun setTimeSignature(ts: TimeSignature) {
        if (_uiState.value.isPlaying) {
            engine.stop()
            _uiState.update { it.copy(timeSignature = ts, isPlaying = false) }
        } else {
            _uiState.update { it.copy(timeSignature = ts) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        engine.stop()
    }
}

class MetronomeViewModelFactory(
    private val engine: MetronomeEngine,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MetronomeViewModel(engine) as T
    }
}
