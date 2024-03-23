package com.psm.mytable.ui.recipe.detail

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.psm.mytable.App
import com.psm.mytable.Event
import com.psm.mytable.data.room.recipe.Recipe
import com.psm.mytable.domain.recipe.DeleteRecipeUseCase
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.utils.extension.onIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 레시피 상세 ViewModel
 * 필요한 UseCase
 * 1. [DeleteRecipeUseCase] 레시피 삭제
 */
class RecipeDetailViewModel(
    val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel(){

    private val _recipeDetail = MutableLiveData<RecipeItemData>()
    val recipeDetail: LiveData<RecipeItemData> = _recipeDetail

    private val _setTitleEvent = MutableLiveData<Event<String>>()
    val setTitleEvent: LiveData<Event<String>>
        get() = _setTitleEvent

    private var _moreLayoutVisibility = MutableLiveData(View.GONE)
    val moreLayoutVisibility: LiveData<Int>
        get() = _moreLayoutVisibility

    private var _goRecipeUpdateEvent = MutableLiveData<Event<RecipeItemData>>()
    val goRecipeUpdateEvent: LiveData<Event<RecipeItemData>>
        get() = _goRecipeUpdateEvent

    private var _doubleClickGoRecipeUpdateEvent = MutableLiveData<Event<RecipeItemData>>()
    val doubleClickGoRecipeUpdateEvent: LiveData<Event<RecipeItemData>>
        get() = _doubleClickGoRecipeUpdateEvent

    private var _goRecipeImageDetailEvent = MutableLiveData<Event<RecipeItemData>>()
    val goRecipeImageDetailEvent: LiveData<Event<RecipeItemData>>
        get() = _goRecipeImageDetailEvent

    private var _recipeDetailState =
        MutableLiveData<Event<RecipeDetailState>>(Event(RecipeDetailState.UnInitialized))
    val recipeDetailState: LiveData<Event<RecipeDetailState>> = _recipeDetailState

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
            _recipeDetailState.postValue(Event(RecipeDetailState.Error))
        }
    }

    fun clickDeleteRecipe(itemData: LiveData<RecipeItemData>) {
        _recipeDetailState.postValue(Event(RecipeDetailState.Loading))
        val mRecipeImage = itemData.value?.recipeImage
        val mBucket = "my-test-butket"
        val mKey = mRecipeImage?.substring(mRecipeImage.indexOf("test1/"))

        try{
            onIO {
                delay(1000)
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
                deleteRecipeUseCase(mData)
                withContext(Dispatchers.Main) {
                    hideMoreMenuLayout()
                    _recipeDetailState.postValue(Event(RecipeDetailState.Complete))
                }
            }
        }catch (e: AmazonServiceException){
            _recipeDetailState.postValue(Event(RecipeDetailState.Error))
        }catch (e: Exception){
            _recipeDetailState.postValue(Event(RecipeDetailState.Error))
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

    fun imageClick(itemData: LiveData<RecipeItemData>){
        _goRecipeImageDetailEvent.value = Event(itemData.value!!)
    }
}