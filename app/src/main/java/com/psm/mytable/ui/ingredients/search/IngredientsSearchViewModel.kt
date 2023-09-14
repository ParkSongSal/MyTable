package com.psm.mytable.ui.ingredients.search

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.common.ProgressVisibility
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.ui.ingredients.IngredientsItemData
import com.psm.mytable.utils.CalendarUtils
import com.psm.mytable.utils.HangulUtils
import java.lang.IllegalStateException

/**
 * 재료 추가 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - database 초기화
 */
class IngredientsSearchViewModel(
    private val repository: AppRepository
) : ViewModel(), ProgressVisibility {

    // 식재료 리스트
    private val _ingredientsListItem = MutableLiveData<List<IngredientsItemData>>()
    val ingredientsListItem: LiveData<List<IngredientsItemData>>
        get() = _ingredientsListItem

    // 식재료 검색 결과
    private val _searchResultItems = MutableLiveData<List<IngredientsItemData>>()
    val searchResultItems: LiveData<List<IngredientsItemData>>
        get() = _searchResultItems

    private val _progressVisible = MutableLiveData(false)
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible

    private val _ingredientListVisibility = MutableLiveData(false)
    val ingredientListVisibility: LiveData<Boolean>
        get() = _ingredientListVisibility

    private val _searchListVisibility = MutableLiveData(false)
    val searchListVisibility: LiveData<Boolean>
        get() = _searchListVisibility

    // 검색결과 없음 레이아웃 노출
    private val _emptyLayoutVisible = MutableLiveData(false)
    val emptyLayoutVisible: LiveData<Boolean>
        get() = _emptyLayoutVisible

    private var _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private var _searchResultSortEvent = MutableLiveData<Event<Unit>>()
    val searchResultSortEvent: LiveData<Event<Unit>>
        get() = _searchResultSortEvent

    private var _ingredientsListSortEvent = MutableLiveData<Event<Unit>>()
    val ingredientsListSortEvent: LiveData<Event<Unit>>
        get() = _ingredientsListSortEvent

    val searchWord = MutableLiveData("")

    private var database: RoomDB? = null

    fun appInit(context: Context) {
        database = RoomDB.getInstance(context)
        hideProgress()
        getIngredientsList()
    }

    // 냉장,냉동,실온 식재료 리스트 조회
    fun getIngredientsList(){
        try{
            val ingredientsList = database?.ingredientDao()?.getAllIngredient() ?: emptyList()
            if(ingredientsList.isNotEmpty()){
                _ingredientsListItem.value = ingredientsList.map{ingredient ->
                    IngredientsItemData(
                        id = ingredient.id,
                        itemName = ingredient.ingredientName,
                        itemCount = ingredient.ingredientCount,
                        storage = ingredient.storage,
                        storageType = ingredient.storageType,
                        regDate = ingredient.regDate,
                        expiryDate = ingredient.expiryDate,
                        remainDay = CalendarUtils.getDDay(ingredient.expiryDate),
                        memo = ingredient.memo ?: ""
                    )
                }.sortedByDescending {
                    it.remainDay.toInt()
                }
            }else{
                _ingredientsListItem.value = emptyList()
            }
        }catch(e: IllegalStateException){
            _ingredientsListItem.value = emptyList()
        }catch(e: Exception){
            _ingredientsListItem.value = emptyList()
        }finally{
            updateIngredientVisibileState()
        }
    }

    fun updateIngredientVisibileState(){
        if((_ingredientsListItem.value?.size ?: 0) > 0){
            _ingredientListVisibility.value = true
            _emptyLayoutVisible.value = false
        }else{
            _ingredientListVisibility.value = false
            _emptyLayoutVisible.value = true
        }
    }

    // 식재료 초성 검색
    fun onSearchWordEditTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterSearchWordIngredient()
            }
        }
    }

    fun filterSearchWordIngredient() {
        val searchWord = searchWord.value
        if(searchWord != null) {
            _searchResultItems.value = _ingredientsListItem.value?.filter {ingredient->
                val result = HangulUtils.getHangulInitialSound(ingredient.itemName, searchWord)
                result.indexOf(searchWord) >= 0
            }

            _searchResultItems.value?.forEach{ingredient->
                ingredient.remainDay = CalendarUtils.getDDay(ingredient.expiryDate)
            }
            updateLayoutVisible()
        }

    }

    private fun updateLayoutVisible(){
        when{
            // 검색어가 있고, 검색결과가 없다면, 데이터 없음 레이아웃 표출
            !searchWord.value.isNullOrEmpty() && searchResultItems.value.isNullOrEmpty() -> {
                _emptyLayoutVisible.value = true
                _ingredientListVisibility.value = false
                _searchListVisibility.value = false
            }

            // 검색어가 있고, 검색결과가 있다면, 검색결과 레이아웃 표출
            !searchWord.value.isNullOrEmpty() && !searchResultItems.value.isNullOrEmpty() -> {
                _emptyLayoutVisible.value = false
                _ingredientListVisibility.value = false
                _searchListVisibility.value = true
            }

            // 검색어가 없고, 검색결과도 없고, 기존 레시피목록 데이터가 있다면 식재료목록 표출
            searchWord.value.isNullOrEmpty() && searchResultItems.value.isNullOrEmpty() && !_ingredientsListItem.value.isNullOrEmpty() ->{
                _emptyLayoutVisible.value = false
                _ingredientListVisibility.value = true
                _searchListVisibility.value = false
            }
            // 검색어가 없고, 검색결과도 없고, 기존 레시피목록 데이터도 없다면 데이터 없음 레이아웃 표출
            searchWord.value.isNullOrEmpty() && searchResultItems.value.isNullOrEmpty() && _ingredientsListItem.value.isNullOrEmpty() ->{
                _emptyLayoutVisible.value = true
                _ingredientListVisibility.value = false
                _searchListVisibility.value = false
            }
            else -> {
                _emptyLayoutVisible.value = false
                _ingredientListVisibility.value = true
                _searchListVisibility.value = false
            }
        }
    }

    override fun showProgress() {
        _progressVisible.value = true
    }

    override fun hideProgress() {
        _progressVisible.value = false
    }

}