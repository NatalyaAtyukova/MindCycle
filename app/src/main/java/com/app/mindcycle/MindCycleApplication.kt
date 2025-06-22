package com.app.mindcycle

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yandex.mobile.ads.common.MobileAds

class MindCycleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        
        // Initialize Yandex Mobile Ads SDK
        MobileAds.initialize(this) {
            // SDK initialization completed
        }
    }
} 