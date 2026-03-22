package com.ieshaan12.metronome.ui.components

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ieshaan12.metronome.ui.theme.MetronomeTheme
import kotlin.math.roundToInt

val MAELZEL_STOPS = intArrayOf(
    20, 24, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52, 54, 56, 58, 60,
    63, 66, 69, 72, 76, 80, 84, 88, 92, 96, 100, 104, 108, 112, 116, 120,
    126, 132, 138, 144, 152, 160, 168, 176, 184, 192, 200, 208,
    216, 224, 232, 240, 252, 264, 276, 288, 300,
)

fun bpmToSliderIndex(bpm: Int): Float {
    val idx = MAELZEL_STOPS.indexOfFirst { it >= bpm }
    return when {
        idx == -1 -> (MAELZEL_STOPS.size - 1).toFloat()
        idx == 0 -> 0f
        else -> {
            val lower = MAELZEL_STOPS[idx - 1]
            val upper = MAELZEL_STOPS[idx]
            if (bpm - lower <= upper - bpm) (idx - 1).toFloat() else idx.toFloat()
        }
    }
}

fun sliderIndexToBpm(index: Float): Int =
    MAELZEL_STOPS[index.roundToInt().coerceIn(0, MAELZEL_STOPS.lastIndex)]

@Composable
fun BpmSlider(
    bpm: Int,
    onBpmChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Slider(
        value = bpmToSliderIndex(bpm),
        onValueChange = { idx -> onBpmChange(sliderIndexToBpm(idx)) },
        valueRange = 0f..(MAELZEL_STOPS.size - 1).toFloat(),
        steps = MAELZEL_STOPS.size - 2,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun BpmSlider120Preview() {
    MetronomeTheme {
        BpmSlider(bpm = 120, onBpmChange = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BpmSlider200Preview() {
    MetronomeTheme {
        BpmSlider(bpm = 200, onBpmChange = {})
    }
}
