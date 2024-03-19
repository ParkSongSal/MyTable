package com.psm.mytable.ui.setting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.psm.mytable.App
import com.psm.mytable.BuildConfig
import com.psm.mytable.Event
import com.psm.mytable.data.repository.AppRepository
import com.psm.mytable.data.room.RoomDB
import com.psm.mytable.data.room.recipe.Recipe
import com.psm.mytable.type.AppEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

/**
 * 설정 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - database 초기화
 * - 현재 버전정보 체크
 */
class SettingViewModel(
    private val repository: AppRepository
) : ViewModel(){

    private var database: RoomDB? = null

    private var _resetCompleteEvent = MutableLiveData<Event<Unit>>()
    val resetCompleteEvent: LiveData<Event<Unit>>
        get() = _resetCompleteEvent

    private var _errorEvent = MutableLiveData<Event<Unit>>()
    val errorEvent: LiveData<Event<Unit>>
        get() = _errorEvent

    private var _dataResetConfirmEvent = MutableLiveData<Event<Unit>>()
    val dataResetConfirmEvent: LiveData<Event<Unit>>
        get() = _dataResetConfirmEvent

    private var _appEvent = MutableLiveData<Event<AppEvent>>()
    val appEvent: LiveData<Event<AppEvent>>
        get() = _appEvent

    private var _mVersion = MutableLiveData<String>("")
    val mVersion: LiveData<String>
        get() = _mVersion

    fun appInit(context: Context){
        database = RoomDB.getInstance(context)
        _mVersion.value = BuildConfig.VERSION_NAME
    }


    /**
     * 광고제거 구독
     * */
    fun clickSubscribe(){

    }

    /**
     * 문의하기
     * */
    fun clickInquire(){
        _appEvent.value = Event(AppEvent.INQUIRE)
    }

    /**
     * 앱 공유하기
     * */
    fun clickShare(){
        _appEvent.value = Event(AppEvent.SHARE)
    }


    /**
     * 데이터 초기화
     * */
    fun clickReset(){
        _dataResetConfirmEvent.value = Event(Unit)
    }

    /**
     * 데이터 초기화 실행
     * 1) 레시피
     * 2) 장바구니
     * */
    fun resetAct(){
        try{
            runBlocking{
                val job = viewModelScope.launch(Dispatchers.IO){
                    database?.shoppingBasketDao()?.basketAllDelete()
                    database?.ingredientDao()?.ingredientAllDelete()
                    //database?.recipeDao()?.recipeAllDelete()
                    val mRecipeList = database?.recipeDao()?.getAllRecipe() ?: listOf()
                    val mBucket = "my-test-butket"
                    mRecipeList.let{
                        mRecipeList.forEach{recipe->
                            val mKey = recipe.recipeImagePath.substring(recipe.recipeImagePath.indexOf("test1/"))
                            try{
                                val deleteObjectRequest = DeleteObjectRequest(mBucket, mKey)
                                App.instance.s3Client.deleteObject(deleteObjectRequest)

                                val mData = Recipe(
                                    id = recipe.id.toInt() ?: 0,
                                    recipeName = recipe.recipeName ?: "",
                                    recipeType = recipe.toString(),
                                    recipeTypeId = recipe.recipeTypeId ?: 0,
                                    ingredients = recipe.ingredients ?: "",
                                    howToMake = recipe.howToMake ?: "",
                                    reg_date = recipe.reg_date ?: "",
                                    recipeImagePath = recipe.recipeImagePath ?: ""
                                )

                                database?.recipeDao()?.delete(mData)
                            }catch(e: AmazonServiceException){
                                _errorEvent.value = Event(Unit)
                            }catch(e: Exception){
                                _errorEvent.value = Event(Unit)
                            }
                        }
                    }
                }
                job.join()
            }
            _resetCompleteEvent.value = Event(Unit)
        }catch(e: Exception){
            _errorEvent.value = Event(Unit)
        }
    }
}