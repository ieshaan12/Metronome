package com.ieshaan12.metronome.audio

import kotlinx.coroutines.flow.StateFlow

fun interface TickCallback {
    fun onTick(beat: Int, beatsPerMeasure: Int)
}

interface MetronomeEngine {
    val currentBeat: StateFlow<Int>
    fun start(bpm: Int, beatsPerMeasure: Int)
    fun stop()
    fun updateBpm(bpm: Int)
    fun updateBeatsPerMeasure(beatsPerMeasure: Int)
}
