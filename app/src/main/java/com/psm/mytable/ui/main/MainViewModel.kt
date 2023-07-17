package com.psm.mytable.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.utils.HangulUtils

/**
 * 인트로 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - 클라이언트 ID 정보가 없는 경우 받오온다.
 * - FCM 토큰 등록이 안되 있으면 등록 한다.
 * - 로그인 사용자, 비 로그인 사용자에 해당되는 앱 초기화 완료 이벤트를 전달한다.
 * - 버전 체크 (강제 또는 선택 업데이트 알럿 노출)
 */
class MainViewModel(
    private val repository: AppRepository
) : ViewModel(){


    private val _items = MutableLiveData<List<RecipeItemData>>()
    val items: LiveData<List<RecipeItemData>>
        get() = _items

    private val _searchResultItems = MutableLiveData<List<RecipeItemData>>()
    val searchResultItems: LiveData<List<RecipeItemData>>
        get() = _searchResultItems

    private val _recipeListVisibility = MutableLiveData(false)
    val recipeListVisibility: LiveData<Boolean>
        get() = _recipeListVisibility

    // 검색결과 없음 레이아웃 노출
    private val _emptyLayoutVisible = MutableLiveData(false)
    val emptyLayoutVisible: LiveData<Boolean>
        get() = _emptyLayoutVisible

    // 검색 결과 레이아웃 노출
    private val _searchResultVisible = MutableLiveData(false)
    val searchResultVisible: LiveData<Boolean>
        get() = _searchResultVisible


    private val _emptyRecipeListVisibility = MutableLiveData(false)
    val emptyRecipeListVisibility: LiveData<Boolean>
        get() = _emptyRecipeListVisibility

    private val _openRecipeDetailEvent = MutableLiveData<Event<RecipeItemData>>()
    val openRecipeDetailEvent: LiveData<Event<RecipeItemData>>
        get() = _openRecipeDetailEvent

    private val _goRecipeWriteEvent = MutableLiveData<Event<Unit>>()
    val goRecipeWriteEvent: LiveData<Event<Unit>>
        get() = _goRecipeWriteEvent

    private var database: RoomDB? = null

    val searchWord = MutableLiveData("")


    fun init(context: Context){
        database = RoomDB.getInstance(context)
        getRecipeList()
    }

    fun getRecipeList(){
        val mRecipeList = database?.recipeDao()?.getAllRecipe() ?: listOf()
        if(mRecipeList.isNotEmpty()){
            _items.value = mRecipeList.map{recipe ->
                RecipeItemData(
                    id = recipe.id.toLong(),
                    recipeImage = recipe.recipeImagePath,
                    recipeName = recipe.recipeName,
                    ingredients = recipe.ingredients,
                    howToMake = recipe.howToMake,
                    reg_date = recipe.reg_date,
                    type = recipe.recipeType,
                    typeId = recipe.recipeTypeId
                )
            }

            _recipeListVisibility.value = true
            _emptyRecipeListVisibility.value = false
        }else{
            _recipeListVisibility.value = false
            _emptyRecipeListVisibility.value = true
        }

    }

   /* fun test() : Flow<PagingData<Recipe>>{
        return Pager(PagingConfig(pageSize = 10)){
                repository.recipeDao
        }.flow.cachedIn(viewModelScope)
    }*/

    fun filterSearchWordRecipe(mSearchWord: String) {
        if(mSearchWord.isNotEmpty()){
            searchWord.value = mSearchWord
            _searchResultItems.value = _items.value?.filter {
                val result = HangulUtils.getHangulInitialSound(it.recipeName, searchWord.value)
                result.indexOf(searchWord.value!!) >= 0
            }
        }else{
            searchWord.value = ""
        }

        updateLayoutVisible()
    }

    private fun updateLayoutVisible() {
        when {

            // 검색어가 있고, 검색결과가 없다면, 데이터 없음 레이아웃 표출
            !searchWord.value.isNullOrEmpty() && searchResultItems.value.isNullOrEmpty() -> {
                _emptyRecipeListVisibility.value = true
                _recipeListVisibility.value = false
                _searchResultVisible.value = false
            }

            // 검색어가 있고, 검색결과가 있다면, 검색결과 레이아웃 표출
           !searchWord.value.isNullOrEmpty() && !searchResultItems.value.isNullOrEmpty() -> {
               _emptyRecipeListVisibility.value = false
               _recipeListVisibility.value = false
               _searchResultVisible.value = true
           }

            // 검색어가 없고, 검색결과도 없고, 기존 레시피목록 데이터가 있다면 레시피목록 표출
           searchWord.value.isNullOrEmpty() && searchResultItems.value.isNullOrEmpty() && !_items.value.isNullOrEmpty() ->{
               _emptyRecipeListVisibility.value = false
               _recipeListVisibility.value = true
               _searchResultVisible.value = false
           }
            // 검색어가 없고, 검색결과도 없고, 기존 레시피목록 데이터도 없다면 데이터 없음 레이아웃 표출
           searchWord.value.isNullOrEmpty() && searchResultItems.value.isNullOrEmpty() && _items.value.isNullOrEmpty() ->{
                _emptyRecipeListVisibility.value = true
                _recipeListVisibility.value = false
                _searchResultVisible.value = false
           }
           else -> {
                _emptyRecipeListVisibility.value = false
                _recipeListVisibility.value = true
                _searchResultVisible.value = false
           }
        }
    }

    fun clickAddRecipe(){
        _goRecipeWriteEvent.value = Event(Unit)
    }

    fun clickRecipeDetail(item: RecipeItemData){
        _openRecipeDetailEvent.value = Event(item)
    }


}