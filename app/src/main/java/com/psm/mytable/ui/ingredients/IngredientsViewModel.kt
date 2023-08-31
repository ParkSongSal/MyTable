package com.psm.mytable.ui.ingredients

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.utils.CalendarUtils
import timber.log.Timber

/**
 * 설정 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - database 초기화
 * - 현재 버전정보 체크
 */
class IngredientsViewModel(
    private val repository: AppRepository
) : ViewModel() {

    // 냉장 식재료 리스트
    private val _coldStorageListItem = MutableLiveData<List<IngredientsItemData>>()
    val coldStorageListItem: LiveData<List<IngredientsItemData>>
        get() = _coldStorageListItem

    // 냉동 식재료 리스트
    private val _frozenStorageListItem = MutableLiveData<List<IngredientsItemData>>()
    val frozenStorageListItem: LiveData<List<IngredientsItemData>>
        get() = _frozenStorageListItem

    // 실온 식재료 리스트
    private val _roomTemperatureStorageListItem = MutableLiveData<List<IngredientsItemData>>()
    val roomTemperatureStorageListItem: LiveData<List<IngredientsItemData>>
        get() = _roomTemperatureStorageListItem

    // 냉장 식재료 유무 체크
    private val _emptyColdStorageListVisibility = MutableLiveData(false)
    val emptyColdStorageListVisibility: LiveData<Boolean>
        get() = _emptyColdStorageListVisibility

    // 냉동 식재료 유무 체크
    private val _emptyFrozenStorageListVisibility = MutableLiveData(false)
    val emptyFrozenStorageListVisibility: LiveData<Boolean>
        get() = _emptyFrozenStorageListVisibility

    // 실온 식재료 유무 체크
    private val _emptyRoomTemperatureStorageListVisibility = MutableLiveData(false)
    val emptyRoomTemperatureStorageListVisibility: LiveData<Boolean>
        get() = _emptyRoomTemperatureStorageListVisibility

    private var _goIngredientAddEvent = MutableLiveData<Event<Unit>>()
    val goIngredientAddEvent: LiveData<Event<Unit>>
        get() = _goIngredientAddEvent


    private var database: RoomDB? = null

    fun appInit(context: Context) {
        database = RoomDB.getInstance(context)

    }

    fun getColdStorageList() {

        val mColdList = database?.ingredientDao()?.getColdStorage() ?: listOf()

        if(mColdList.isNotEmpty()){
            _coldStorageListItem.value = mColdList.map{ingredient ->
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
            }
            /**
             * Dummy Data
             * _coldStorageList = 0개
             * */
            //_frozenStorageListItem.value = emptyList()
        }else{
            _coldStorageListItem.value = emptyList()
        }

        /**
         * Dummy Data
         * _coldStorageList = 2개
         * */
        /*_coldStorageListItem.value = listOf(
            IngredientsItemData(
                id = 1,
                itemName = "대파",
                itemCount = "2개",
                storage = "냉장",
                storageType = 1,
                regDate = "2023-07-18",
                expiryDate = "2023-07-21",
                remainDay = "-3",
                memo = "대파요리"
            ),
            IngredientsItemData(
                id = 2,
                itemName = "비엔나소시지",
                itemCount = "1개",
                storage = "냉장",
                storageType = 1,
                regDate = "2023-07-18",
                expiryDate = "2023-08-25",
                remainDay = "-30",
                memo = "소시지야채볶음"
            ),
            IngredientsItemData(
                id = 3,
                itemName = "우유",
                itemCount = "1L",
                storage = "냉장",
                storageType = 1,
                regDate = "2023-07-18",
                expiryDate = "2023-07-25",
                remainDay = "-7",
                memo = ""
            ),
            IngredientsItemData(
                id = 4,
                itemName = "돼지고기",
                itemCount = "500G",
                storage = "냉장",
                storageType = 1,
                regDate = "2023-07-14",
                expiryDate = "2023-08-18",
                remainDay = "+7",
                memo = ""
            ),
            IngredientsItemData(
                id = 5,
                itemName = "소고기",
                itemCount = "300G",
                storage = "냉장",
                storageType = 1,
                regDate = "2023-07-16",
                expiryDate = "2023-07-16",
                remainDay = "0",
                memo = ""
            )
        )*/
        /**
         * Dummy Data
         * _coldStorageList = 0개
         * */
        //_coldStorageList.value = listOf()
        updateColdStorageVisibleState()
    }

    fun getFrozenStorageList() {
        /**
         * Dummy Data
         * _coldStorageList = 2개
         * */
        /*_frozenStorageListItem.value = listOf(
            IngredientsItemData(
                id = 0,
                itemName = "고기만두",
                itemCount = "1봉지",
                storage = "냉동",
                storageType = 2,
                regDate = "2023-07-16",
                expiryDate = "2024-07-16",
                remainDay = "365",
                memo = ""
            ),
            IngredientsItemData(
                id = 1,
                itemName = "아이스크림",
                itemCount = "10개",
                storage = "냉동",
                storageType = 2,
                regDate = "2023-07-16",
                expiryDate = "2024-07-16",
                remainDay = "365",
                memo = ""
            )
        )*/
        val mFrozenList = database?.ingredientDao()?.getFrozenStorage() ?: listOf()

        if(mFrozenList.isNotEmpty()){
            _frozenStorageListItem.value = mFrozenList.map{ingredient ->
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
            }
            /**
             * Dummy Data
             * _coldStorageList = 0개
             * */
            //_frozenStorageListItem.value = emptyList()
        }else{
            _frozenStorageListItem.value = emptyList()
        }
        updateFrozenStorageVisibleState()
    }

    fun getRoomTemperatureList(){

        val mRoomTemperatureList = database?.ingredientDao()?.getRoomTemperatureStorage() ?: listOf()

        if(mRoomTemperatureList.isNotEmpty()){
            _roomTemperatureStorageListItem.value = mRoomTemperatureList.map{ingredient ->
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
            }
            /**
             * Dummy Data
             * _coldStorageList = 0개
             * */
            //_frozenStorageListItem.value = emptyList()
        }else{
            _roomTemperatureStorageListItem.value = emptyList()
        }
        updateRoomTemperatureStorageVisibleState()
    }

    private fun updateColdStorageVisibleState() {

        // 냉장 식재료 유무 체크
        _emptyColdStorageListVisibility.value = (_coldStorageListItem.value?.size ?: 0) <= 0
        //_emptyColdStorageListVisibility.value = (_items.value?.size ?: 0) > 0
    }

    private fun updateFrozenStorageVisibleState() {
        _emptyFrozenStorageListVisibility.value = (_frozenStorageListItem.value?.size ?: 0) <= 0
    }

    private fun updateRoomTemperatureStorageVisibleState() {
        _emptyRoomTemperatureStorageListVisibility.value = (_roomTemperatureStorageListItem.value?.size ?: 0) <= 0
    }

    fun clickIngredientAdd(){
        _goIngredientAddEvent.value = Event(Unit)
    }
}