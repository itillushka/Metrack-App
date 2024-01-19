package com.example.metrack

import android.app.Application
import io.scanbot.sdk.ScanbotSDKInitializer

class MeTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val LICENSE_KEY = "no-strings-attached"
        ScanbotSDKInitializer()
            .license(this, LICENSE_KEY) // see below
            .initialize(this)
    }
}