package com.ieshaan12.metronome

import android.app.Application
import android.util.Log
import com.ieshaan12.metronome.debug.DebugTools

class MetronomeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Metronome ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) — track: ${BuildConfig.TRACK}, build: ${BuildConfig.BUILD_TYPE}")
        DebugTools.init(this)
    }

    private companion object {
        const val TAG = "MetronomeApp"
    }
}
