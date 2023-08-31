package com.psm.mytable.ui.ingredients.cold

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.psm.mytable.App
import com.psm.mytable.BuildConfig
import com.psm.mytable.Event
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.room.recipe.Recipe
import com.psm.mytable.type.AppEvent
import com.psm.mytable.ui.basket.ShoppingBasketItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

/**
 * 설정 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - database 초기화
 * - 현재 버전정보 체크
 */
class ColdStorageViewModel(
    private val repository: AppRepository
) : ViewModel(){

    private var database: RoomDB? = null

    fun appInit(context: Context){
        database = RoomDB.getInstance(context)

    }

}