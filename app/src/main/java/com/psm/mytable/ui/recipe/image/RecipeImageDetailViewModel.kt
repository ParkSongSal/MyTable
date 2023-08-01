package com.psm.mytable.ui.recipe.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.room.AppRepository
import com.psm.mytable.ui.recipe.RecipeItemData

/**
 * 인트로 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - 클라이언트 ID 정보가 없는 경우 받오온다.
 * - FCM 토큰 등록이 안되 있으면 등록 한다.
 * - 로그인 사용자, 비 로그인 사용자에 해당되는 앱 초기화 완료 이벤트를 전달한다.
 * - 버전 체크 (강제 또는 선택 업데이트 알럿 노출)
 */
class RecipeImageDetailViewModel(
    private val repository: AppRepository
) : ViewModel(){

    private val _appInitCompleteEvent = MutableLiveData<Event<Unit>>()
    val appInitCompleteEvent: LiveData<Event<Unit>>
        get() = _appInitCompleteEvent

    private val _recipeImage = MutableLiveData<String>()
    val recipeImage: LiveData<String> = _recipeImage

    private val _setTitleEvent = MutableLiveData<Event<String>>()
    val setTitleEvent: LiveData<Event<String>>
        get() = _setTitleEvent

    fun appInit(){
        _appInitCompleteEvent.value = Event(Unit)
    }

    fun getRecipeDetailData(recipeItemData: RecipeItemData){
        _recipeImage.value = recipeItemData.recipeImage
        _setTitleEvent.value = Event(recipeItemData.recipeName)
    }
}