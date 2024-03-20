package com.psm.mytable

import AmplifyManager
import android.app.Application
import android.content.Context
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.psm.mytable.data.repository.AppRepository
import com.psm.mytable.data.room.RoomDB
import timber.log.Timber

class App: Application(), CameraXConfig.Provider {

    companion object {
        lateinit var instance: App
        private set
        var database: RoomDB? = null
        private set

        var transferUtility: TransferUtility? = null
        private set
    }

    lateinit var repository: AppRepository
    lateinit var adRequest : AdRequest

    private lateinit var s3Provider: AmplifyManager


    // 식재료 변화(수정, 삭제) 상태 변경 Flag
    var isIngredientChange = false

    val s3Client
        get() = s3Provider.s3Client

    override fun onCreate() {
        super.onCreate()
        instance = this
        initS3()

        database = RoomDB.getInstance(instance)
        transferUtility = getTransferUtility(instance)
        repository = AppRepository()

        MobileAds.initialize(instance)
        adRequest = AdRequest.Builder().build()

        Timber.plant(Timber.DebugTree())
    }

    fun transferLossHandler() {
        TransferNetworkLossHandler.getInstance(this)
    }

    fun getTransferUtility(context: Context): TransferUtility = TransferUtility.builder()
        .context(context)
        .s3Client(s3Client)
        .build()

    private fun initS3(){
        s3Provider = AmplifyManager(this)
    }

    override fun getCameraXConfig(): CameraXConfig = Camera2Config.defaultConfig()
}