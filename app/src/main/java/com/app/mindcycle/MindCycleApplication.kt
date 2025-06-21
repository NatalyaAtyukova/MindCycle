package com.app.mindcycle

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MindCycleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
} 