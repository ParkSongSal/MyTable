package com.psm.mytable.ui.recipe.write

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.R
import com.psm.mytable.room.MyTableRepository
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.recipe.RecipeItemData

/**
 * 레시피 작성(등록, 수정)
 * [앱 초기화]
 * - 클라이언트 ID 정보가 없는 경우 받오온다.
 * - FCM 토큰 등록이 안되 있으면 등록 한다.
 * - 로그인 사용자, 비 로그인 사용자에 해당되는 앱 초기화 완료 이벤트를 전달한다.
 * - 버전 체크 (강제 또는 선택 업데이트 알럿 노출)
 */
class RecipeWriteViewModel(
    private val repository: MyTableRepository
) : ViewModel(){


    private var _openPhotoDialogEvent = MutableLiveData<Event<Unit>>()
    val openPhotoDialogEvent: LiveData<Event<Unit>>
        get() = _openPhotoDialogEvent

    // 음식 종류 선택 Dialog 호출
    private var _openFoodTypeDialogEvent = MutableLiveData<Event<Unit>>()
    val openFoodTypeDialogEvent: LiveData<Event<Unit>>
        get() = _openFoodTypeDialogEvent

    private var _recipeType = MutableLiveData<String>("")
    val recipeType: LiveData<String>
        get() = _recipeType



    fun init(){

    }

    fun setRecipeType(type: RecipeType){
        _recipeType.value = type.recipeName
    }

    fun clickPhotoDialog(){
        _openPhotoDialogEvent.value = Event(Unit)
    }
    fun clickSelectType(){
        _openFoodTypeDialogEvent.value = Event(Unit)
    }
}