package com.psm.mytable.ui.basket

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.psm.mytable.Event
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.room.basket.ShoppingBasket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date

/**
 * 레시피 작성(등록, 수정)
 * [앱 초기화]
 * - 클라이언트 ID 정보가 없는 경우 받오온다.
 * - FCM 토큰 등록이 안되 있으면 등록 한다.
 * - 로그인 사용자, 비 로그인 사용자에 해당되는 앱 초기화 완료 이벤트를 전달한다.
 * - 버전 체크 (강제 또는 선택 업데이트 알럿 노출)
 */
class ShoppingBasketListViewModel(
    private val repository: AppRepository
) : ViewModel(){

    private val _items = MutableLiveData<List<ShoppingBasketItemData>>()
    val items: LiveData<List<ShoppingBasketItemData>>
        get() = _items

    val shoppingBasketPagingData = repository.getShoppingBasketPagingSource().cachedIn(viewModelScope)


    private val _shoppingListVisibility = MutableLiveData(true)
    val shoppingListVisibility: LiveData<Boolean>
        get() = _shoppingListVisibility

    private val _emptyShoppingListVisibility = MutableLiveData(false)
    val emptyShoppingListVisibility: LiveData<Boolean>
        get() = _emptyShoppingListVisibility

    private var _showAddShoppingItemDialogEvent = MutableLiveData<Event<Unit>>()
    val showAddShoppingItemDialogEvent: LiveData<Event<Unit>>
        get() = _showAddShoppingItemDialogEvent

    private var _showDeleteShoppingItemDialogEvent = MutableLiveData<Event<ShoppingBasketItemData>>()
    val showDeleteShoppingItemDialogEvent: LiveData<Event<ShoppingBasketItemData>>
        get() = _showDeleteShoppingItemDialogEvent

    private var _completeShoppingItemInsertEvent = MutableLiveData<Event<Unit>>()
    val completeShoppingItemInsertEvent: LiveData<Event<Unit>>
        get() = _completeShoppingItemInsertEvent

    private var _completeShoppingItemDeleteEvent = MutableLiveData<Event<Unit>>()
    val completeShoppingItemDeleteEvent: LiveData<Event<Unit>>
        get() = _completeShoppingItemDeleteEvent

    private var database: RoomDB? = null


    fun init(context: Context){

        database = RoomDB.getInstance(context)

        //getShoppingBasketList()
        getShoppingBasketListCount()
    }

    fun getShoppingBasketListCount(){
        try{
            val mCount = database?.shoppingBasketDao()?.getShoppingBasketListCount()
            if((mCount ?: 0) > 0){
                updateShoppingBasketVisibleState(true)
            }else{
                updateShoppingBasketVisibleState(false)
            }
        }catch(e: IllegalStateException){
            updateShoppingBasketVisibleState(false)
        }catch(e: Exception){
            updateShoppingBasketVisibleState(false)
        }
    }
    fun getShoppingBasketList(){

        /*runBlocking {
            repeat(22){
                val now = System.currentTimeMillis()
                val date = Date(now)
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val nowDate = sdf.format(date)
                val mData = ShoppingBasket(
                    id = 0,
                    itemName = "test$it",
                    reg_date = nowDate
                )

                viewModelScope.launch(Dispatchers.IO){
                    database?.shoppingBasketDao()?.insert(mData)
                }
            }
        }*/

        /*_items.value = listOf(
            ShoppingBasketItemData(
                id = 1,
                itemName = "item1",
                reg_date = "2023-07-18"
            ),
            ShoppingBasketItemData(
                id = 2,
                itemName = "item2",
                reg_date = "2023-07-18"
            )
        )*/
        /*try{
            val mShoppingBasketList = database?.shoppingBasketDao()?.getAllShoppingBasket() ?: listOf()
            if(mShoppingBasketList.isNotEmpty()){
                _items.value = mShoppingBasketList.map{item ->
                    ShoppingBasketItemData(
                        id = item.id.toLong(),
                        itemName = item.itemName,
                        reg_date = item.reg_date
                    )
                }
                updateShoppingBasketVisibleState(true)
            }else{
                updateShoppingBasketVisibleState(false)
            }
        }catch(e: IllegalStateException){
            updateShoppingBasketVisibleState(false)
        }catch(e: Exception){
            updateShoppingBasketVisibleState(false)
        }*/
    }

    private fun updateShoppingBasketVisibleState(boolean: Boolean){
        if(boolean){
            _shoppingListVisibility.value = true
            _emptyShoppingListVisibility.value = false
        }else{
            _shoppingListVisibility.value = false
            _emptyShoppingListVisibility.value = true
        }
    }

    fun addShoppingItem(){
        _showAddShoppingItemDialogEvent.value = Event(Unit)
    }

    fun addShoppingItemAct(item: String){
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val nowDate = sdf.format(date)
        val mData = ShoppingBasket(
            id = 0,
            itemName = item,
            reg_date = nowDate
        )

        val job = viewModelScope.launch(Dispatchers.IO){
            Timber.d("psm_Thread is Start Job...")
            database?.shoppingBasketDao()?.insert(mData)
        }

        runBlocking{
            job.join()
            Timber.d("psm_Thread is continuing...")
        }
        Timber.d("psm_Thread is The End...")
        _completeShoppingItemInsertEvent.value = Event(Unit)
    }

    fun clickDeleteItem(itemData: ShoppingBasketItemData){
        _showDeleteShoppingItemDialogEvent.value = Event(itemData)
    }

    fun deleteShoppingItemAct(itemData: ShoppingBasketItemData){

        val mData = ShoppingBasket(
            id = itemData.id.toInt(),
            itemName = itemData.itemName,
            reg_date = itemData.reg_date
        )

        val job = viewModelScope.launch(Dispatchers.IO){
            Timber.d("psm_Thread is Start Job...")
            database?.shoppingBasketDao()?.delete(mData)
        }

        runBlocking{
            job.join()
            Timber.d("psm_Thread is continuing...")
        }
        Timber.d("psm_Thread is The End...")
        _completeShoppingItemDeleteEvent.postValue(Event(Unit))
    }
}