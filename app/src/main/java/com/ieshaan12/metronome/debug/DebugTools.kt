package com.ieshaan12.metronome.debug

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.os.Trace
import com.ieshaan12.metronome.BuildConfig

object DebugTools {

    inline fun <T> trace(label: String, block: () -> T): T {
        if (BuildConfig.DEBUG) Trace.beginSection(label)
        try {
            return block()
        } finally {
            if (BuildConfig.DEBUG) Trace.endSection()
        }
    }

    inline fun traceAsync(label: String, cookie: Int) {
        if (BuildConfig.DEBUG) Trace.beginAsyncSection(label, cookie)
    }

    inline fun endTraceAsync(label: String, cookie: Int) {
        if (BuildConfig.DEBUG) Trace.endAsyncSection(label, cookie)
    }

    fun init(application: Application) {
        if (!BuildConfig.DEBUG) return
        enableStrictMode()
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects()
                .detectActivityLeaks()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        detectUnsafeIntentLaunch()
                    }
                }
                .penaltyLog()
                .build()
        )
    }
}
