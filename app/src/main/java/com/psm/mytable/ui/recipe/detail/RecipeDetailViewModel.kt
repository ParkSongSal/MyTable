package com.psm.mytable.ui.recipe.detail

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.psm.mytable.App
import com.psm.mytable.Event
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.room.recipe.Recipe
import com.psm.mytable.ui.recipe.RecipeItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 레시피 작성(등록, 수정)
 * [앱 초기화]
 * - 클라이언트 ID 정보가 없는 경우 받오온다.
 * - FCM 토큰 등록이 안되 있으면 등록 한다.
 * - 로그인 사용자, 비 로그인 사용자에 해당되는 앱 초기화 완료 이벤트를 전달한다.
 * - 버전 체크 (강제 또는 선택 업데이트 알럿 노출)
 */
class RecipeDetailViewModel(
    private val repository: AppRepository
) : ViewModel(){

    private val _recipeDetail = MutableLiveData<RecipeItemData>()
    val recipeDetail: LiveData<RecipeItemData> = _recipeDetail


    private val _setTitleEvent = MutableLiveData<Event<String>>()
    val setTitleEvent: LiveData<Event<String>>
        get() = _setTitleEvent

    private var _moreLayoutVisibility = MutableLiveData(View.GONE)
    val moreLayoutVisibility: LiveData<Int>
        get() = _moreLayoutVisibility

    private var _completeRecipeDeleteEvent = MutableLiveData<Event<Unit>>()
    val completeRecipeDeleteEvent: LiveData<Event<Unit>>
        get() = _completeRecipeDeleteEvent

    private var _goRecipeUpdateEvent = MutableLiveData<Event<RecipeItemData>>()
    val goRecipeUpdateEvent: LiveData<Event<RecipeItemData>>
        get() = _goRecipeUpdateEvent

    private var _doubleClickGoRecipeUpdateEvent = MutableLiveData<Event<RecipeItemData>>()
    val doubleClickGoRecipeUpdateEvent: LiveData<Event<RecipeItemData>>
        get() = _doubleClickGoRecipeUpdateEvent

    private var _errorEvent = MutableLiveData<Event<Unit>>()
    val errorEvent: LiveData<Event<Unit>>
        get() = _errorEvent

    private var database: RoomDB? = null

    fun init(context: Context){
        database = RoomDB.getInstance(context)
    }
    fun getRecipeDetailData(recipeItemData: RecipeItemData){
        _recipeDetail.value = RecipeItemData(
            id = recipeItemData.id,
            recipeImage = recipeItemData.recipeImage,
            recipeName = recipeItemData.recipeName,
            ingredients = recipeItemData.ingredients,
            howToMake = recipeItemData.howToMake,
            reg_date = recipeItemData.reg_date,
            type = recipeItemData.type,
            typeId = recipeItemData.typeId
        )

        _setTitleEvent.value = Event(recipeItemData.recipeName)
    }

    fun clickMenuBg() {
        hideMoreMenuLayout()
    }

    fun showMoreMenuLayout() {
        _moreLayoutVisibility.value = View.VISIBLE
    }

    fun hideMoreMenuLayout() {
        _moreLayoutVisibility.value = View.GONE
    }

    fun clickEditRecipe(itemData: LiveData<RecipeItemData>) {
        hideMoreMenuLayout()
        if(itemData.value?.id != null){
            _goRecipeUpdateEvent.value = Event(itemData.value!!)
        }else{
            _errorEvent.value = Event(Unit)
        }
    }

    fun clickDeleteRecipe(itemData: LiveData<RecipeItemData>) {

        val mRecipeImage = itemData.value?.recipeImage
        val mBucket = "my-test-butket"
        val mKey = mRecipeImage?.substring(mRecipeImage.indexOf("test1/"))

        try{

            val deleteObjectRequest = DeleteObjectRequest(mBucket, mKey)
            App.instance.s3Client.deleteObject(deleteObjectRequest)

            val mData = Recipe(
                id = itemData.value?.id?.toInt() ?: 0,
                recipeName = itemData.value?.recipeName ?: "",
                recipeType = itemData.value.toString(),
                recipeTypeId = itemData.value?.typeId ?: 0,
                ingredients = itemData.value?.ingredients ?: "",
                howToMake = itemData.value?.howToMake ?: "",
                reg_date = itemData.value?.reg_date ?: "",
                recipeImagePath = itemData.value?.recipeImage ?: ""
            )

            hideMoreMenuLayout()
           runBlocking{
                val job = viewModelScope.launch(Dispatchers.IO){
                    database?.recipeDao()?.delete(mData)
                }
                job.join()
            }
            _completeRecipeDeleteEvent.value = Event((Unit))
        }catch (e: AmazonServiceException){
            _errorEvent.value = Event(Unit)
        }catch (e: Exception){
            _errorEvent.value = Event(Unit)
        }


    }

    var clickTime:Long = 0
    fun itemDoubleClick(itemData: LiveData<RecipeItemData>){

        val currentTime = System.currentTimeMillis()
        if(currentTime - clickTime >= 2500){
            clickTime = currentTime
        }else{
            _doubleClickGoRecipeUpdateEvent.value = Event(itemData.value!!)
        }
    }
}