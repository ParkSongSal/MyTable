package com.psm.mytable.ui.recipe.update

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.psm.mytable.App
import com.psm.mytable.Event
import com.psm.mytable.room.MyTableRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.room.recipe.Recipe
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.recipe.RecipeItemData
import com.starry.file_utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
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
class RecipeUpdateViewModel(
    private val repository: MyTableRepository
) : ViewModel(){

    private val _recipeData = MutableLiveData<RecipeViewData>()
    val recipeData: LiveData<RecipeViewData> = _recipeData

    // Toolbar Title Set
    private val _setTitleEvent = MutableLiveData<Event<String>>()
    val setTitleEvent: LiveData<Event<String>>
        get() = _setTitleEvent

    // 레시피 이미지 선택 Dialog 호출
    private var _openPhotoDialogEvent = MutableLiveData<Event<Unit>>()
    val openPhotoDialogEvent: LiveData<Event<Unit>>
        get() = _openPhotoDialogEvent

    // 음식 종류 선택 Dialog 호출
    private var _openFoodTypeDialogEvent = MutableLiveData<Event<Unit>>()
    val openFoodTypeDialogEvent: LiveData<Event<Unit>>
        get() = _openFoodTypeDialogEvent

    private var _recipeType = MutableLiveData<String>("")
    val recipeType: LiveData<String>
        get() = _recipeType

    private val _imageInfoObjectVisible = MutableLiveData(View.INVISIBLE)
    val imageInfoObjectVisible: LiveData<Int>
        get() = _imageInfoObjectVisible

    private val _progressVisible = MutableLiveData(false)
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible

    private var database: RoomDB? = null

    fun init(context: Context){
        database = RoomDB.getInstance(context)
    }

    fun setRecipeData(recipeItemData: RecipeItemData){

        val recipeViewData = RecipeViewData()
        recipeViewData.apply{
            recipeId = recipeItemData.id
            recipeImage = recipeItemData.recipeImage
            originalRecipeName = recipeItemData.recipeName
            originalIngredients = recipeItemData.ingredients
            originalHowToMake = recipeItemData.howToMake
            originalRecipeType = recipeItemData.type
            originalRecipeTypeId = recipeItemData.typeId
            recipeName = recipeItemData.recipeName
            ingredients = recipeItemData.ingredients
            howToMake = recipeItemData.howToMake
            recipeTypeId = recipeItemData.typeId
        }

        _recipeData.value = recipeViewData
        _recipeType.value = recipeItemData.type

        _setTitleEvent.value = Event(recipeItemData.recipeName)
        imageInfoObjectVisible()
    }

    fun setRecipeImageUri(uri: Uri){
        _recipeData.value?.recipeImageUri = uri
    }


    private fun imageInfoObjectVisible(){
        _imageInfoObjectVisible.value = if(_recipeData.value?.recipeImage?.isNotEmpty() == true){
            View.GONE
        }else{
            View.VISIBLE
        }
    }

    fun setRecipeType(type: RecipeType){
        _recipeType.value = type.recipeName
        _recipeData.value?.recipeTypeId = type.typeId
    }

    fun clickPhotoDialog(){
        _openPhotoDialogEvent.value = Event(Unit)
    }

    fun clickSelectType(){
        _openFoodTypeDialogEvent.value = Event(Unit)
    }

    fun clickNext(){

    }

    fun showProgress(){
        _progressVisible.value = true
    }

    fun hideProgress(){
        _progressVisible.value = false
    }
}