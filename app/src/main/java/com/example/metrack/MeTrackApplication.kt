package com.example.metrack

import android.app.Application
import io.scanbot.sap.IScanbotSDKLicenseErrorHandler
import io.scanbot.sap.SdkFeature
import io.scanbot.sdk.ScanbotSDKInitializer
import io.scanbot.sdk.util.log.LoggerProvider

class MeTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val LICENSE_KEY = "P/zFTbxw8gEM0uFpxT4btOm1VdYwSq\"\n" +
                "\"BBUjIp6Pw5w2OrigOgViOWU3ZMvCOO\"\n" +
                "\"jfmEVVfUy2+PIOMXa2kvu4R25YH3H+\"\n" +
                "\"QKakHrxX+aaDyWi851QYXNvrJODufo\"\n" +
                "\"jwJ6Rh488iniNGoK0/3EljPGEwoTH6\"\n" +
                "\"bjcYCbbDqdGdJa7QIinMYIFM0PlfTv\"\n" +
                "\"NhGFFEZLYonXpEAwIb0Ogb2oYed3vL\"\n" +
                "\"jUsjMuM7RgraJyNmRiBInRyz8kLCmh\"\n" +
                "\"8SSR5jTQSUVEUvOg/jaqhf/1U8OR/F\"\n" +
                "\"5utlkiY3HHD+VZn8Fa/jMDtCYvCq7D\"\n" +
                "\"Ht6AXeb8K3qMbyjIFCtZR+8EWzEbfI\"\n" +
                "\"k5aHxJLJXZnw==\\nU2NhbmJvdFNESw\"\n" +
                "\"pjb20uZXhhbXBsZS5tZXRyYWNrCjE3\"\n" +
                "\"MDgzODcxOTkKNTkwCjE=\\n"
        ScanbotSDKInitializer()
            .license(this, LICENSE_KEY) // see below
            .licenceErrorHandler(IScanbotSDKLicenseErrorHandler { status, feature, message ->
                // Handle license errors here:
                LoggerProvider.logger.d("ExampleApplication", "license status:${status.name}, message: $message")
                if (feature != SdkFeature.NoSdkFeature) {
                    LoggerProvider.logger.d("ExampleApplication", "Missing SDK feature in license: ${feature.name}")
                }
            })
            .initialize(this)
    }
}