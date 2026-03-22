package com.ieshaan12.metronome.audio

import kotlin.math.PI
import kotlin.math.sin

object ClickSynthesizer {

    fun generate(
        sampleRate: Int = 44100,
        frequencyHz: Double = 800.0,
        durationMs: Int = 10,
    ): ShortArray {
        val numSamples = sampleRate * durationMs / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val sine = sin(2.0 * PI * frequencyHz * t)
            val envelope = 1.0 - (i.toDouble() / numSamples) // linear fade-out
            buffer[i] = (sine * envelope * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
