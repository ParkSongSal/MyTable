package com.psm.mytable.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psm.mytable.App
import com.psm.mytable.Event
import com.psm.mytable.data.repository.AppRepository
import com.psm.mytable.data.room.RoomDB
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.recipe.RecipeItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 인트로 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - 클라이언트 ID 정보가 없는 경우 받오온다.
 * - FCM 토큰 등록이 안되 있으면 등록 한다.
 * - 로그인 사용자, 비 로그인 사용자에 해당되는 앱 초기화 완료 이벤트를 전달한다.
 * - 버전 체크 (강제 또는 선택 업데이트 알럿 노출)
 */
class MainViewModel(
    private val appRepository: AppRepository
) : ViewModel() {

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

    private var _recipeListState =
        MutableLiveData<Event<RecipeListState>>(Event(RecipeListState.UnInitialized))
    val recipeListState: LiveData<Event<RecipeListState>> = _recipeListState

    private var database: RoomDB? = null

    val searchWord = MutableLiveData("")
    var checkedCategory = MutableLiveData(0)
    val tempItems = mutableListOf<RecipeItemData>()

    fun init(context: Context) {
        database = RoomDB.getInstance(context)
    }

    fun getRecipeList() {
        try {
            _recipeListState.value = Event(RecipeListState.Loading)
            val mRecipeList = App.instance.database?.recipeDao()?.getAllRecipe() ?: listOf()
            if (mRecipeList.isNotEmpty()) {
                _items.value = mRecipeList.map { recipe ->
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
                tempItems.addAll(_items.value!!)
                updateRecipeVisibleState(true)
            } else {
                updateRecipeVisibleState(false)
            }
            _recipeListState.value = Event(RecipeListState.Complete)
        } catch (e: IllegalStateException) {
            updateRecipeVisibleState(false)
        } catch (e: Exception) {
            updateRecipeVisibleState(false)
        }
    }

    private fun updateRecipeVisibleState(boolean: Boolean) {
        if (boolean) {
            _recipeListVisibility.value = true
            _emptyRecipeListVisibility.value = false
        } else {
            _recipeListVisibility.value = false
            _emptyRecipeListVisibility.value = true
        }
    }

    fun filterSearchWordRecipe(mSearchWord: String) {
        if (mSearchWord.isNotEmpty()) {
            searchWord.value = mSearchWord
            viewModelScope.launch {
                _recipeListState.value = Event(RecipeListState.Loading)
                delay(1000)
                val mSearchList = database?.recipeDao()?.getSearchAllRecipeList("%${mSearchWord}%")
                withContext(Dispatchers.Main) {
                    if(mSearchList?.isNotEmpty() == true) {
                        _items.value = mSearchList.map { recipe ->
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
                        updateRecipeVisibleState(true)
                    } else {
                        updateRecipeVisibleState(false)
                    }
                    _recipeListState.value = Event(RecipeListState.Complete)
                }
            }

        } else {
            searchWord.value = ""
        }
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
            searchWord.value.isNullOrEmpty() && searchResultItems.value.isNullOrEmpty() && !_items.value.isNullOrEmpty() -> {
                _emptyRecipeListVisibility.value = false
                _recipeListVisibility.value = true
                _searchResultVisible.value = false
            }
            // 검색어가 없고, 검색결과도 없고, 기존 레시피목록 데이터도 없다면 데이터 없음 레이아웃 표출
            searchWord.value.isNullOrEmpty() && searchResultItems.value.isNullOrEmpty() && _items.value.isNullOrEmpty() -> {
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

    fun clickAddRecipe() {
        _goRecipeWriteEvent.value = Event(Unit)
    }

    fun clickRecipeDetail(item: RecipeItemData) {
        _openRecipeDetailEvent.value = Event(item)
    }
    fun getRecipeCategory(type: RecipeType) {
        try {
            checkedCategory.value = type.typeId
            viewModelScope.launch {
                _recipeListState.value = Event(RecipeListState.Loading)
                delay(1000)
                withContext(Dispatchers.Main) {
                    val mCategoryRecipeList =  if (type.typeId == 0) {
                        database?.recipeDao()?.getAllRecipe() ?: listOf()
                    } else {
                        database?.recipeDao()?.getCategoryRecipe(type.typeId) ?: listOf()
                    }
                    if (mCategoryRecipeList.isNotEmpty()) {
                        _items.value = mCategoryRecipeList.map { recipe ->
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
                        tempItems.addAll(_items.value!!)
                        updateRecipeVisibleState(true)
                    } else {
                        updateRecipeVisibleState(false)
                    }
                    //updateLayoutVisible()
                    _recipeListState.value = Event(RecipeListState.Complete)
                }
            }
        } catch (e: Exception) {
            updateRecipeVisibleState(false)
            _recipeListState.value = Event(RecipeListState.Error)
        }
    }
}