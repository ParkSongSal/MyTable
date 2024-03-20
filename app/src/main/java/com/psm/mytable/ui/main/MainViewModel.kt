package com.psm.mytable.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.domain.recipe.GetAllRecipeUseCase
import com.psm.mytable.domain.recipe.GetCategoryRecipeUseCase
import com.psm.mytable.domain.recipe.GetSearchRecipeUseCase
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.utils.extension.onIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 레시피 목록
 * 필요한 UseCase
 * 1. [GetAllRecipeUseCase] 레시피 전체 목록
 * 2. [GetCategoryRecipeUseCase] 카테고리별 레시피 목록
 * 3. [GetSearchRecipeUseCase] 검색어별 레시피 목록
 */
class MainViewModel(
    private val getAllRecipeUseCase: GetAllRecipeUseCase,
    private val getCategoryRecipeUseCase: GetCategoryRecipeUseCase,
    private val getSearchRecipeUseCase: GetSearchRecipeUseCase,
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

    val searchWord = MutableLiveData("")
    var checkedCategory = MutableLiveData(0)

    fun filterSearchWordRecipe(mSearchWord: String) {
        if (mSearchWord.isNotEmpty()) {
            searchWord.value = mSearchWord
            onIO {
                _recipeListState.postValue(Event(RecipeListState.Loading))
                delay(1000)
                _items.postValue(getSearchRecipeUseCase("%${mSearchWord}%") ?: emptyList())
                withContext(Dispatchers.Main) {
                    recipeListVisible(_items.value?.size ?: 0)
                    _recipeListState.postValue(Event(RecipeListState.Complete))
                }
            }
        } else searchWord.value = ""
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
            onIO {
                _recipeListState.postValue(Event(RecipeListState.Loading))
                delay(1000)
                _items.postValue(if(type.typeId == 0) getAllRecipeUseCase() else getCategoryRecipeUseCase(type.typeId))
                withContext(Dispatchers.Main) {
                    recipeListVisible(_items.value?.size ?: 0)
                    _recipeListState.postValue(Event(RecipeListState.Complete))
                }
            }
        } catch (e: Exception) {
            updateRecipeVisibleState(false)
            _recipeListState.value = Event(RecipeListState.Error)
        }
    }

    private fun recipeListVisible(size: Int) {
        if(size > 0) {
            updateRecipeVisibleState(true)
        }else {
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
}