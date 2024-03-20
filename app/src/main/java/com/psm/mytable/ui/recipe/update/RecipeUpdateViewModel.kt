package com.psm.mytable.ui.recipe.update

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.AmazonServiceException
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.psm.mytable.App
import com.psm.mytable.Event
import com.psm.mytable.data.room.recipe.Recipe
import com.psm.mytable.domain.recipe.UpdateRecipeUseCase
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.utils.extension.onIO
import com.psm.mytable.utils.extension.onMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

/**
 * 레시피 수정 ViewModel
 * 필요한 UseCase
 * 1. [UpdateRecipeUseCase] 레시피 수정
 */
class RecipeUpdateViewModel(
    private val updateRecipeUseCase: UpdateRecipeUseCase
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

    private var _recipeType = MutableLiveData<String>("한식")
    val recipeType: LiveData<String>
        get() = _recipeType

    private val _imageInfoObjectVisible = MutableLiveData(View.INVISIBLE)
    val imageInfoObjectVisible: LiveData<Int>
        get() = _imageInfoObjectVisible

    private var _recipeUpdateState =
        MutableLiveData<Event<RecipeUpdateState>>(Event(RecipeUpdateState.UnInitialized))
    val recipeUpdateState: LiveData<Event<RecipeUpdateState>> = _recipeUpdateState

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
        _recipeType.value = type.categoryName
        _recipeData.value?.recipeTypeId = type.typeId
    }

    fun clickPhotoDialog(){
        _openPhotoDialogEvent.value = Event(Unit)
    }

    fun clickSelectType(){
        _openFoodTypeDialogEvent.value = Event(Unit)
    }

    fun clickNext(){
        var file:File? = null
        var fileName = ""
        val mRecipeImagePath = if(_recipeData.value?.recipeImageUri != null){
            val fileUri = Uri.parse(_recipeData.value?.recipeImageUri.toString())
            val filePath = App.instance.getFilePath(fileUri)
            file = File(filePath)
            fileName = file.name

            "https://my-test-butket.s3.ap-southeast-2.amazonaws.com/test1/$fileName"
        }else{
            _recipeData.value?.recipeImage!!
        }

        val mRecipeName = if(_recipeData.value?.recipeName != _recipeData.value?.originalRecipeName) {
            _recipeData.value?.recipeName
        } else {
            _recipeData.value?.originalRecipeName
        }

        val mRecipeType = _recipeType.value

        val mRecipeTypeId = if(_recipeData.value?.recipeTypeId != _recipeData.value?.originalRecipeTypeId){
            _recipeData.value?.recipeTypeId
        }else{
            _recipeData.value?.originalRecipeTypeId
        }

        val mIngredients = if(_recipeData.value?.ingredients != _recipeData.value?.originalIngredients){
            _recipeData.value?.ingredients
        }else{
            _recipeData.value?.originalIngredients
        }

        val mHowToMake = if(_recipeData.value?.howToMake != _recipeData.value?.originalHowToMake){
            _recipeData.value?.howToMake
        }else{
            _recipeData.value?.originalHowToMake
        }

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val nowDate = sdf.format(date)

        val mData = Recipe(
            id = _recipeData.value?.recipeId!!.toInt(),
            recipeName = mRecipeName ?: "",
            recipeType = mRecipeType ?: "한식",
            recipeTypeId = mRecipeTypeId ?: 1,
            ingredients = mIngredients ?: "",
            howToMake = mHowToMake ?: "",
            reg_date = nowDate,
            recipeImagePath = mRecipeImagePath
        )

        uploadS3Image(fileName, file, mData)
    }

    fun roomRecipeUpdate(mData: Recipe, fileDeleteFlag : Boolean){
        onIO{
            updateRecipeUseCase(mData)
        }
        if(fileDeleteFlag){
            deleteS3Image()
        }else{
            onMain{
                _recipeUpdateState.postValue(Event(RecipeUpdateState.Complete))
            }
        }
    }

    private fun uploadS3Image(fileName: String, file: File?, mData: Recipe){
        _recipeUpdateState.postValue(Event(RecipeUpdateState.Loading))
        if(file != null){
            App.instance.transferLossHandler()
            val mBucket = "my-test-butket"
            val uploadObserver = App.transferUtility?.upload(
                mBucket,
                "test1/$fileName",
                file
            )
            uploadObserver?.setTransferListener(object : TransferListener{
                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> roomRecipeUpdate(mData, true)
                        TransferState.FAILED, TransferState.CANCELED -> _recipeUpdateState.postValue(Event(RecipeUpdateState.Error))
                        else -> {}
                    }
                }
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                override fun onError(id: Int, ex: java.lang.Exception?) {
                    _recipeUpdateState.postValue(Event(RecipeUpdateState.Error))
                }
            })
        }else{
            roomRecipeUpdate(mData, false)
        }
    }
    private fun deleteS3Image(){

        val mRecipeImage = _recipeData.value?.recipeImage
        val mBucket = "my-test-butket"
        val mKey = mRecipeImage?.substring(mRecipeImage.indexOf("test1/"))
        try{
            onIO {
                val deleteObjectRequest = DeleteObjectRequest(mBucket, mKey)
                App.instance.s3Client.deleteObject(deleteObjectRequest)
                withContext(Dispatchers.Main) {
                    _recipeUpdateState.postValue(Event(RecipeUpdateState.Complete))
                }
            }
        }catch (e: AmazonServiceException){
            _recipeUpdateState.postValue(Event(RecipeUpdateState.Error))
        }catch(e:Exception){
            _recipeUpdateState.postValue(Event(RecipeUpdateState.Error))
        }
    }
}