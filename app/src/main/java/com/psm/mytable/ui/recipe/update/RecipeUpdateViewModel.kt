package com.psm.mytable.ui.recipe.update

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.AmazonServiceException
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.psm.mytable.App
import com.psm.mytable.Event
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.room.recipe.Recipe
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.recipe.RecipeItemData
import com.starry.file_utils.FileUtils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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
    private val repository: AppRepository
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

    // 레시피 수정 완료
    private var _completeRecipeDataUpdateEvent = MutableLiveData<Event<Unit>>()
    val completeRecipeDataUpdateEvent: LiveData<Event<Unit>>
        get() = _completeRecipeDataUpdateEvent

    private var _recipeType = MutableLiveData<String>("한식")
    val recipeType: LiveData<String>
        get() = _recipeType

    private val _imageInfoObjectVisible = MutableLiveData(View.INVISIBLE)
    val imageInfoObjectVisible: LiveData<Int>
        get() = _imageInfoObjectVisible

    private val _progressVisible = MutableLiveData(false)
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible

    private var _errorEvent = MutableLiveData<Event<Unit>>()
    val errorEvent: LiveData<Event<Unit>>
        get() = _errorEvent

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
        showProgress()

        var file:File? = null
        var fileName = ""
        val mRecipeImagePath = if(_recipeData.value?.recipeImageUri != null){
            val fileUri = Uri.parse(_recipeData.value?.recipeImageUri.toString())
            val filePath = FileUtils(App.instance.applicationContext).getPath(fileUri)
            file = File(filePath)
            fileName = file.name

            "https://my-test-butket.s3.ap-southeast-2.amazonaws.com/test1/$fileName"
        }else{
            _recipeData.value?.recipeImage!!
        }

        val mRecipeName = if(_recipeData.value?.recipeName != _recipeData.value?.originalRecipeName){
            _recipeData.value?.recipeName
        }else{
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

    fun roomRecipeUpdate(mData: Recipe, fileDelteFlag : Boolean){
        viewModelScope.launch(IO){
            val result = database?.recipeDao()?.updateRecipe(mData)
            // 이미지 업로드 성공시 기존 이미지 삭제
           /* if(result != 0){
                _completeRecipeDataUpdateEvent.value = Event(Unit)
            }else{
                ToastUtils.showToast("룸 업데이트 실패")
                _completeRecipeDataUpdateEvent.value = Event(Unit)
            }*/
        }
        if(fileDelteFlag){
            deleteS3Image()
        }else{
            //_completeRecipeDataUpdateEvent.postValue(Event(Unit))
            _completeRecipeDataUpdateEvent.value = Event(Unit)
        }
    }

    fun uploadS3Image(fileName: String, file: File?, mData: Recipe){
        if(file != null){

            TransferNetworkLossHandler.getInstance(App.instance.applicationContext)
            val mBucket = "my-test-butket"
            val uploadObserver = App.instance.getTransferUtility(App.instance).upload(
                mBucket,
                "test1/$fileName",
                file,
                CannedAccessControlList.PublicRead
            )
            uploadObserver.setTransferListener(object : TransferListener{
                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> {
                            roomRecipeUpdate(mData, true)

                        }
                        TransferState.FAILED, TransferState.CANCELED -> {
                            _errorEvent.value = Event(Unit)
                        }

                        else -> {}
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                }

                override fun onError(id: Int, ex: java.lang.Exception?) {
                    hideProgress()
                    _errorEvent.value = Event(Unit)
                }

            })

        }else{
            roomRecipeUpdate(mData, false)
        }
    }
    fun deleteS3Image(){

        val mRecipeImage = _recipeData.value?.recipeImage
        val mBucket = "my-test-butket"
        val mKey = mRecipeImage?.substring(mRecipeImage.indexOf("test1/"))

        try{
            val deleteObjectRequest = DeleteObjectRequest(mBucket, mKey)
            App.instance.s3Client.deleteObject(deleteObjectRequest)
            _completeRecipeDataUpdateEvent.value = Event(Unit)
        }catch (e: AmazonServiceException){
            _errorEvent.value = Event(Unit)
        }catch(e:Exception){
            _errorEvent.value = Event(Unit)
        }finally {
            hideProgress()
        }
    }

    fun showProgress(){
        _progressVisible.value = true
    }

    fun hideProgress(){
        _progressVisible.value = false
    }
}