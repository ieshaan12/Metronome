package com.ieshaan12.metronome.ui

import com.ieshaan12.metronome.audio.MetronomeEngine
import com.ieshaan12.metronome.model.TimeSignature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MetronomeViewModelTest {

    private lateinit var fakeEngine: FakeMetronomeEngine
    private lateinit var viewModel: MetronomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeEngine = FakeMetronomeEngine()
        viewModel = MetronomeViewModel(fakeEngine)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is defaults`() {
        val state = viewModel.uiState.value
        assertEquals(DEFAULT_BPM, state.bpm)
        assertEquals(TimeSignature.FOUR_FOUR, state.timeSignature)
        assertFalse(state.isPlaying)
        assertEquals(0, state.currentBeat)
    }

    @Test
    fun `play sets isPlaying`() {
        viewModel.play()
        assertTrue(viewModel.uiState.value.isPlaying)
        assertTrue(fakeEngine.startCalled)
    }

    @Test
    fun `pause sets isPlaying false`() {
        viewModel.play()
        viewModel.pause()
        assertFalse(viewModel.uiState.value.isPlaying)
        assertTrue(fakeEngine.stopCalled)
    }

    @Test
    fun `setBpm clamps low`() {
        viewModel.setBpm(5)
        assertEquals(BPM_MIN, viewModel.uiState.value.bpm)
    }

    @Test
    fun `setBpm clamps high`() {
        viewModel.setBpm(999)
        assertEquals(BPM_MAX, viewModel.uiState.value.bpm)
    }

    @Test
    fun `setBpm while playing stops engine`() {
        viewModel.play()
        fakeEngine.stopCalled = false
        viewModel.setBpm(144)
        assertTrue(fakeEngine.stopCalled)
        assertFalse(viewModel.uiState.value.isPlaying)
        assertEquals(144, viewModel.uiState.value.bpm)
    }

    @Test
    fun `setTimeSignature while playing stops engine`() {
        viewModel.play()
        fakeEngine.stopCalled = false
        viewModel.setTimeSignature(TimeSignature.THREE_FOUR)
        assertTrue(fakeEngine.stopCalled)
        assertFalse(viewModel.uiState.value.isPlaying)
        assertEquals(TimeSignature.THREE_FOUR, viewModel.uiState.value.timeSignature)
    }

    @Test
    fun `onCleared stops engine`() = runTest {
        viewModel.play()
        fakeEngine.stopCalled = false
        // Access the protected onCleared via reflection
        val method = viewModel.javaClass.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)
        assertTrue(fakeEngine.stopCalled)
    }
}

private class FakeMetronomeEngine : MetronomeEngine {
    private val _currentBeat = MutableStateFlow(0)
    override val currentBeat: StateFlow<Int> = _currentBeat.asStateFlow()

    var startCalled = false
    var stopCalled = false
    var lastUpdatedBpm: Int? = null
    var lastUpdatedBeatsPerMeasure: Int? = null

    override fun start(bpm: Int, beatsPerMeasure: Int) {
        startCalled = true
    }

    override fun stop() {
        stopCalled = true
        _currentBeat.value = 0
    }

    override fun updateBpm(bpm: Int) {
        lastUpdatedBpm = bpm
    }

    override fun updateBeatsPerMeasure(beatsPerMeasure: Int) {
        lastUpdatedBeatsPerMeasure = beatsPerMeasure
        _currentBeat.value = 0
    }
}
