package com.psm.mytable.ui.ingredients.frozen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.psm.mytable.data.repository.AppRepository
import com.psm.mytable.data.room.RoomDB

/**
 * 설정 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - database 초기화
 * - 현재 버전정보 체크
 */
class FrozenStorageViewModel(
    private val repository: AppRepository
) : ViewModel(){

    private var database: RoomDB? = null

    fun appInit(context: Context){
        database = RoomDB.getInstance(context)
    }
}