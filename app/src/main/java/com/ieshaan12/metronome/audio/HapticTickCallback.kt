package com.ieshaan12.metronome.audio

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

class HapticTickCallback(context: Context) : TickCallback {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun onTick(beat: Int, beatsPerMeasure: Int) {
        vibrator.vibrate(effect)
    }
}
