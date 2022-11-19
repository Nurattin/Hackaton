package com.smartdev.hackaton

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("3b6f1d7d-a008-4207-8a48-672210c9773d")
        MapKitFactory.initialize(this)
        DirectionsFactory.initialize(this)
    }
}