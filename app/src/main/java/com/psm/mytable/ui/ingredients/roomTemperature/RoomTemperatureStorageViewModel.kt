package com.psm.mytable.ui.ingredients.roomTemperature

import android.content.Context
import androidx.lifecycle.ViewModel
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB

/**
 * 설정 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - database 초기화
 * - 현재 버전정보 체크
 */
class RoomTemperatureStorageViewModel(
    private val repository: AppRepository
) : ViewModel(){

    private var database: RoomDB? = null

    fun appInit(context: Context){
        database = RoomDB.getInstance(context)
    }
}