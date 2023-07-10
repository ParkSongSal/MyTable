package com.psm.mytable

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.MyTableRepository
import com.psm.mytable.room.RoomDB
import timber.log.Timber

class App: Application(), CameraXConfig.Provider {

    val repository: MyTableRepository = AppRepository()
    lateinit var adRequest : AdRequest


    override fun onCreate() {
        super.onCreate()
        instance = this

        MobileAds.initialize(instance)
        adRequest = AdRequest.Builder().build()

        //MobileAds.initialize(this) {}
        Timber.plant(Timber.DebugTree())

    }

    companion object {
        lateinit var instance: App
    }

    override fun getCameraXConfig(): CameraXConfig = Camera2Config.defaultConfig()
}