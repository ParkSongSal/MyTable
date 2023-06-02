package com.psm.mytable

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.MyTableRepository

class App: Application(), CameraXConfig.Provider {

    val repository: MyTableRepository = AppRepository()


    override fun onCreate() {
        super.onCreate()
        instance = this

        //MobileAds.initialize(this) {}

    }

    companion object {
        lateinit var instance: App
    }

    override fun getCameraXConfig(): CameraXConfig = Camera2Config.defaultConfig()
}