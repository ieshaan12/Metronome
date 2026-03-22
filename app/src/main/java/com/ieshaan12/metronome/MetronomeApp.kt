package com.ieshaan12.metronome

import android.app.Application
import com.ieshaan12.metronome.debug.DebugTools

class MetronomeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DebugTools.init(this)
    }
}
