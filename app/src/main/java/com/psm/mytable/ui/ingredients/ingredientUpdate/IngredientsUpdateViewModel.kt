package com.psm.mytable.ui.ingredients.ingredientUpdate

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psm.mytable.Event
import com.psm.mytable.common.ProgressVisibility
import com.psm.mytable.data.repository.AppRepository
import com.psm.mytable.data.room.RoomDB
import com.psm.mytable.data.room.ingredient.Ingredient
import com.psm.mytable.type.RecipeSearchType
import com.psm.mytable.ui.ingredients.IngredientsItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date

/**
 * 재료 추가 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - database 초기화
 */
class IngredientsUpdateViewModel(
    private val repository: AppRepository
) : ViewModel(), ProgressVisibility {

    private val _ingredientsData = MutableLiveData<IngredientUpdateViewData>()
    val ingredientsData: LiveData<IngredientUpdateViewData> = _ingredientsData

    // Toolbar Title Set
    private val _setTitleEvent = MutableLiveData<Event<String>>()
    val setTitleEvent: LiveData<Event<String>>
        get() = _setTitleEvent

    private var _selectExpirationDateEvent = MutableLiveData<Event<Unit>>()
    val selectExpirationDateEvent: LiveData<Event<Unit>>
        get() = _selectExpirationDateEvent

    private var _expirationDateSearchEvent = MutableLiveData<Event<String>>()
    val expirationDateSearchEvent: LiveData<Event<String>>
        get() = _expirationDateSearchEvent

    private var _recipeSearchEvent = MutableLiveData<Event<MutableMap<String, String>>>()
    val recipeSearchEvent: LiveData<Event<MutableMap<String,String>>>
        get() = _recipeSearchEvent

    private var _expirationDateSearchErrorEvent = MutableLiveData<Event<Unit>>()
    val expirationDateSearchErrorEvent: LiveData<Event<Unit>>
        get() = _expirationDateSearchErrorEvent

    // 재료 수정 완료
    private var _completeIngredientDataUpdateEvent = MutableLiveData<Event<Unit>>()
    val completeIngredientDataUpdateEvent: LiveData<Event<Unit>>
        get() = _completeIngredientDataUpdateEvent

    // 재료 삭제 완료
    private var _completeIngredientDeleteEvent = MutableLiveData<Event<Unit>>()
    val completeIngredientDeleteEvent: LiveData<Event<Unit>>
        get() = _completeIngredientDeleteEvent

    // 오류 발생
    private var _errorEvent = MutableLiveData<Event<Unit>>()
    val errorEvent: LiveData<Event<Unit>>
        get() = _errorEvent

    private val _progressVisible = MutableLiveData(false)
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible


    private var database: RoomDB? = null

    fun appInit(context: Context) {
        database = RoomDB.getInstance(context)
        hideProgress()
    }

    fun setIngredientsData(ingredientsItemData: IngredientsItemData){
        val ingredientsData = IngredientUpdateViewData()
        ingredientsData.apply{
            ingredientId = ingredientsItemData.id
            ingredientName = ingredientsItemData.itemName
            originalIngredientName = ingredientsItemData.itemName
            ingredientCount = ingredientsItemData.itemCount
            originalIngredientCount = ingredientsItemData.itemCount
            storage = ingredientsItemData.storage
            originalStorage = ingredientsItemData.storage
            storageType = ingredientsItemData.storageType
            originalStorageType = ingredientsItemData.storageType
            expirationDate = ingredientsItemData.expiryDate
            originalExpirationDate = ingredientsItemData.expiryDate
            memo = ingredientsItemData.memo
            originalMemo = ingredientsItemData.memo
            setStorage(ingredientsItemData.storageType)
        }
        _ingredientsData.value = ingredientsData

        _setTitleEvent.value = Event(ingredientsItemData.itemName)

    }

    fun clickSelectExpirationDate(){
        _selectExpirationDateEvent.value = Event(Unit)
    }

    fun clickNext(){
        showProgress()

        val mIngredientName = if(_ingredientsData.value?.ingredientName != _ingredientsData.value?.originalIngredientName){
            _ingredientsData.value?.ingredientName
        }else{
            _ingredientsData.value?.originalIngredientName
        }

        val mIngredientCount = if(_ingredientsData.value?.ingredientCount != _ingredientsData.value?.originalIngredientCount){
            _ingredientsData.value?.ingredientCount
        }else{
            _ingredientsData.value?.originalIngredientCount
        }

        val mExpirationDate = if(_ingredientsData.value?.expirationDate != _ingredientsData.value?.originalExpirationDate){
            _ingredientsData.value?.expirationDate
        }else{
            _ingredientsData.value?.originalExpirationDate
        }

        val mStorage = if(_ingredientsData.value?.storage != _ingredientsData.value?.originalStorage){
            _ingredientsData.value?.storage
        }else{
            _ingredientsData.value?.originalStorage
        }

        val mStorageType = if(_ingredientsData.value?.storageType != _ingredientsData.value?.originalStorageType){
            _ingredientsData.value?.storageType
        }else{
            _ingredientsData.value?.originalStorageType
        }

        val mMemo = if(_ingredientsData.value?.memo != _ingredientsData.value?.originalMemo){
            _ingredientsData.value?.memo
        }else{
            _ingredientsData.value?.originalMemo
        }

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val nowDate = sdf.format(date)

        val mData = Ingredient(
            id = _ingredientsData.value?.ingredientId!!,
            ingredientName = mIngredientName ?: "",
            ingredientCount = mIngredientCount ?: "",
            storage = mStorage ?: "냉장",
            storageType = mStorageType ?: 1,
            expiryDate = mExpirationDate ?: "",
            memo = mMemo,
            regDate = nowDate
        )
        viewModelScope.launch(Dispatchers.IO){
            database?.ingredientDao()?.updateIngredientItem(mData)
        }
        hideProgress()

        _completeIngredientDataUpdateEvent.value = Event(Unit)
    }

    fun clickDelete(itemData: LiveData<IngredientUpdateViewData>){
        val mData = Ingredient(
            id = itemData.value?.ingredientId ?: 0,
            ingredientName = itemData.value?.ingredientName ?: "",
            ingredientCount = itemData.value?.ingredientCount ?: "",
            storage = itemData.value?.storage ?: "",
            storageType = itemData.value?.storageType ?: 1,
            expiryDate = itemData.value?.expirationDate ?: "",
            memo = itemData.value?.memo
        )
        try{
            runBlocking{
                val job = viewModelScope.launch(Dispatchers.IO){
                    database?.ingredientDao()?.delete(mData)
                }
                job.join()
            }

            _completeIngredientDeleteEvent.value = Event(Unit)
        }catch(e: Exception){
            _errorEvent.value = Event(Unit)
        }
    }

    fun expirationDateSearchClick(){

        val ingredientName = _ingredientsData.value?.ingredientName
        if(ingredientName?.isNotEmpty() == true){
            _expirationDateSearchEvent.value = Event(ingredientName)
        }else{
            _expirationDateSearchErrorEvent.value = Event(Unit)
        }
    }

    fun recipeSearchClick(recipeSearchType: RecipeSearchType){
        val ingredientName = _ingredientsData.value?.ingredientName
        if(ingredientName?.isNotEmpty() == true){
            val mutableMap = mutableMapOf<String, String>()
            mutableMap.put("searchType", recipeSearchType.name)
            mutableMap.put("ingredientName", ingredientName)
            _recipeSearchEvent.value = Event(mutableMap)
        }else{
            _expirationDateSearchErrorEvent.value = Event(Unit)
        }
    }


    fun setExpirationDate(year : Int, month : String, day: String){
        _ingredientsData.value?.expirationDate = "$year-$month-$day"
    }

    override fun showProgress() {
        _progressVisible.value = true
    }

    override fun hideProgress() {
        _progressVisible.value = false
    }

}