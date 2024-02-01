package com.example.metrack

import android.app.Application
import io.scanbot.sap.IScanbotSDKLicenseErrorHandler
import io.scanbot.sap.SdkFeature
import io.scanbot.sdk.ScanbotSDKInitializer
import io.scanbot.sdk.util.log.LoggerProvider

class MeTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val LICENSE_KEY =
                "P6I04uUrEf7sM2QwB1yOwe0tNupchH" +
                "ksqIHG20xFI/0+pg7i7mUWu7ONNjgz" +
                "RuvIr+RaFNjW8IIDp7YX+7SPukvXn3" +
                "q2UWclvrph3EkIQ/4lWdl6xXsCJiEj" +
                "WfEnSl7WIo5fwFHew2JBqg7kRHc3sm" +
                "HekWu8+mbxj8f5fnGnfGtNhQMGmW1A" +
                "g/FbYWKSx7HBDRSOTJtOroPi+8BHsi" +
                "Liy9SV+BagdP0w+xbYbNLdJ1spDfjf" +
                "nqfi9Gf1vfCW8/prqRt4YJPqCCfUxC" +
                "5E2MwzIwERNU+2mqwYNn0dKiD+p90q" +
                "+bkJof2qERBuCKgwY77Z4TAGSQE71y" +
                "1bSgoaKbdZtQ==\nU2NhbmJvdFNESw" +
                "pjb20uZXhhbXBsZS5tZXRyYWNrCjE3" +
                "MDgzODcxOTkKNTkwCjI=\n";

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