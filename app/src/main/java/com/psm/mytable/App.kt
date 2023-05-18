package com.psm.mytable

import android.app.Application
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.MyTableRepository

class App: Application() {

    val repository: MyTableRepository = AppRepository()


    override fun onCreate() {
        super.onCreate()
        instance = this

        //MobileAds.initialize(this) {}

    }

    companion object {
        lateinit var instance: App
    }
}