package com.psm.mytable

import AmplifyManager
import android.app.Application
import android.content.Context
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import timber.log.Timber

class App: Application(), CameraXConfig.Provider {

    private var database: RoomDB? = null
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


        database = RoomDB.getInstance(instance)
        repository = AppRepository(database?.recipeDao()!!)


        initS3()
        MobileAds.initialize(instance)
        adRequest = AdRequest.Builder().build()

        Timber.plant(Timber.DebugTree())

    }

    fun getTransferUtility(context: Context) = TransferUtility.builder()
        .context(context)
        .s3Client(s3Client)
        .build()

    fun initS3(){
        s3Provider = AmplifyManager(this)
    }

    companion object {
        lateinit var instance: App
    }

    override fun getCameraXConfig(): CameraXConfig = Camera2Config.defaultConfig()
}