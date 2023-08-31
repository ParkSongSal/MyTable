package com.psm.mytable.ui.ingredients.ingredientsAdd

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psm.mytable.Event
import com.psm.mytable.common.ProgressVisibility
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.room.ingredient.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date

/**
 * 재료 추가 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - database 초기화
 */
class IngredientsAddViewModel(
    private val repository: AppRepository
) : ViewModel(), ProgressVisibility {

    private val _ingredientsAddViewData: LiveData<IngredientViewData> = MutableLiveData(IngredientViewData())
    val ingredientsAddViewData: LiveData<IngredientViewData> = _ingredientsAddViewData

    private var _selectExpirationDateEvent = MutableLiveData<Event<Unit>>()
    val selectExpirationDateEvent: LiveData<Event<Unit>>
        get() = _selectExpirationDateEvent

    private var _expirationDateSearchEvent = MutableLiveData<Event<String>>()
    val expirationDateSearchEvent: LiveData<Event<String>>
        get() = _expirationDateSearchEvent

    private var _expirationDateSearchErrorEvent = MutableLiveData<Event<Unit>>()
    val expirationDateSearchErrorEvent: LiveData<Event<Unit>>
        get() = _expirationDateSearchErrorEvent

    private var _completeIngredientDataInsertEvent = MutableLiveData<Event<Unit>>()
    val completeIngredientDataInsertEvent: LiveData<Event<Unit>>
        get() = _completeIngredientDataInsertEvent

    private val _progressVisible = MutableLiveData(false)
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible


    private var database: RoomDB? = null

    fun appInit(context: Context) {
        database = RoomDB.getInstance(context)
        hideProgress()
    }


    fun clickSelectExpirationDate(){
        _selectExpirationDateEvent.value = Event(Unit)
    }

    fun ingredientAddClick(){
        showProgress()

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val nowDate = sdf.format(date)

        val mData = Ingredient(
            id = 0,
            ingredientName = _ingredientsAddViewData.value?.ingredientName ?: "",
            ingredientCount = _ingredientsAddViewData.value?.ingredientCount ?: "",
            storage = _ingredientsAddViewData.value?.storage ?: "냉장",
            storageType = _ingredientsAddViewData.value?.storageType ?: 1,
            expiryDate = _ingredientsAddViewData.value?.expirationDate ?: "",
            memo = _ingredientsAddViewData.value?.memo ?: "",
            regDate = nowDate
        )
        viewModelScope.launch(Dispatchers.IO){
            database?.ingredientDao()?.insert(mData)
        }
        hideProgress()
        val test = database?.ingredientDao()?.getAllIngredient()
        if(test?.isNotEmpty() == true){
            Timber.d("psm_ingredient : ${test.toString()}")
        }
        _completeIngredientDataInsertEvent.value = Event(Unit)

    }

    fun expirationDateSearchClick(){

        val ingredientName = _ingredientsAddViewData.value?.ingredientName
        if(ingredientName?.isNotEmpty() == true){
            _expirationDateSearchEvent.value = Event(ingredientName)
        }else{
            _expirationDateSearchErrorEvent.value = Event(Unit)
        }
    }

    fun setExpirationDate(year : Int, month : String, day: String){
        _ingredientsAddViewData.value?.expirationDate = "$year-$month-$day"
    }




    override fun showProgress() {
        _progressVisible.value = true
    }

    override fun hideProgress() {
        _progressVisible.value = false
    }

}