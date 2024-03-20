package com.psm.mytable.ui.recipe.write

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.psm.mytable.App
import com.psm.mytable.Event
import com.psm.mytable.data.room.recipe.Recipe
import com.psm.mytable.domain.recipe.InsertRecipeUseCase
import com.psm.mytable.type.RecipeType
import com.psm.mytable.utils.extension.onIO
import com.starry.file_utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
class RecipeWriteViewModel(
    private val insertRecipeUseCase: InsertRecipeUseCase
) : ViewModel(){

    val recipeWriteData = MutableLiveData(RecipeViewData())

    private var _openPhotoDialogEvent = MutableLiveData<Event<Unit>>()
    val openPhotoDialogEvent: LiveData<Event<Unit>>
        get() = _openPhotoDialogEvent

    // 음식 종류 선택 Dialog 호출
    private var _openFoodTypeDialogEvent = MutableLiveData<Event<Unit>>()
    val openFoodTypeDialogEvent: LiveData<Event<Unit>>
        get() = _openFoodTypeDialogEvent

    private var _completeRecipeDataInsertEvent = MutableLiveData<Event<Unit>>()
    val completeRecipeDataInsertEvent: LiveData<Event<Unit>>
        get() = _completeRecipeDataInsertEvent

    private var _recipeType = MutableLiveData<String>("한식")
    val recipeType: LiveData<String>
        get() = _recipeType

    private var _recipeTypeId = MutableLiveData<Int>(1)
    val recipeTypeId: LiveData<Int>
        get() = _recipeTypeId
    val testName = MutableLiveData<String>()

    private val _progressVisible = MutableLiveData(false)
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible

    fun setRecipeImageUri(uri: Uri){
        recipeWriteData.value?.recipeImageUri = uri
    }

    fun setRecipeType(type: RecipeType){
        _recipeType.value = type.categoryName
        _recipeTypeId.value = type.typeId
    }

    fun clickPhotoDialog(){
        _openPhotoDialogEvent.value = Event(Unit)
    }
    fun clickSelectType(){
        _openFoodTypeDialogEvent.value = Event(Unit)
    }

    fun showProgress(){
        _progressVisible.value = true
    }

    fun hideProgress(){
        _progressVisible.value = false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun clickNext(){
        showProgress()
        val fileUri = Uri.parse(recipeWriteData.value?.recipeImageUri.toString())
        val filePath = FileUtils(App.instance.applicationContext).getPath(fileUri)
        val file = File(filePath)
        val fileName = file.name
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val nowDate = sdf.format(date)
        val mData = Recipe(
            id = 0,
            recipeName = recipeWriteData.value?.recipeName ?: "",
            recipeType = _recipeType.value.toString(),
            recipeTypeId = _recipeTypeId.value ?: 1,
            ingredients = recipeWriteData.value?.ingredients ?: "",
            howToMake = recipeWriteData.value?.howToMake ?: "",
            reg_date = nowDate,
            recipeImagePath = "https://my-test-butket.s3.ap-southeast-2.amazonaws.com/test1/$fileName"
        )
        uploadWithTransferUtility(fileName, file, mData)
    }

    private fun uploadWithTransferUtility(fileName: String, file: File, mData : Recipe) : Int?{
        try {
            App.instance.transferLossHandler()
            val observer = App.transferUtility?.upload("my-test-butket", "test1/$fileName", file)
            observer?.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> {
                            onIO {
                                insertRecipeUseCase(mData)
                                withContext(Dispatchers.Main) {
                                    _completeRecipeDataInsertEvent.value = Event(Unit)
                                    hideProgress()
                                }
                            }
                        }
                        TransferState.FAILED, TransferState.CANCELED -> hideProgress()
                        else -> {}
                    }
                }
                override fun onError(id: Int, ex: Exception?) {
                    hideProgress()
                }
            })
            return observer?.id
        } catch (e: IllegalArgumentException) {
            return null
        }
    }

}