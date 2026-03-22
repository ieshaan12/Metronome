package com.ieshaan12.metronome.model

fun tempoMarking(bpm: Int): String = when {
    bpm < 40 -> "Grave"
    bpm < 66 -> "Largo"
    bpm < 76 -> "Larghetto"
    bpm < 108 -> "Andante"
    bpm < 120 -> "Moderato"
    bpm < 156 -> "Allegro"
    bpm < 176 -> "Vivace"
    bpm < 200 -> "Presto"
    else -> "Prestissimo"
}
