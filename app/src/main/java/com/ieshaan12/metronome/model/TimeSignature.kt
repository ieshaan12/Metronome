package com.ieshaan12.metronome.model

enum class TimeSignature(
    val beatsPerMeasure: Int,
    val noteValue: Int,
    val label: String,
) {
    TWO_FOUR(2, 4, "2/4"),
    THREE_FOUR(3, 4, "3/4"),
    FOUR_FOUR(4, 4, "4/4"),
    SIX_EIGHT(6, 8, "6/8"),
    EIGHT_EIGHT(8, 8, "8/8"),
}
