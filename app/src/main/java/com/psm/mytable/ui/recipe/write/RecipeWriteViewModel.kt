package com.psm.mytable.ui.recipe.write

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
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
class RecipeWriteViewModel(
    private val repository: MyTableRepository
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

    private var database: RoomDB? = null

    fun init(context: Context){
        database = RoomDB.getInstance(context)
    }

    fun setRecipeImageUri(uri: Uri){
        recipeWriteData.value?.recipeImageUri = uri
    }

    fun setRecipeType(type: RecipeType){
        _recipeType.value = type.recipeName
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

        Timber.d("psm_mData : ${mData.toString()}")
        uploadWithTransferUtility(fileName, file, mData)

    }

    fun uploadWithTransferUtility(fileName: String, file: File, mData : Recipe) {

        val awsCredentials: AWSCredentials =
            BasicAWSCredentials(
                "AKIARYHOJEIGIYROZHE4",
                "SJobb4jPsRBo0ab9wnQcnpNqs836o3CeGa1C8nz9"
            ) // IAM 생성하며 받은 것 입력

        val s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTHEAST_2))

        val transferUtility = TransferUtility.builder().s3Client(s3Client)
            .context(App.instance).build()
        TransferNetworkLossHandler.getInstance(App.instance.applicationContext)

        val uploadObserver = transferUtility.upload(
            "my-test-butket",
            "test1/$fileName",
            file,
            CannedAccessControlList.PublicRead
        ) // (bucket api, file이름, file객체)


        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state === TransferState.COMPLETED) {
                    // Handle a completed upload
                    viewModelScope.launch(Dispatchers.IO){
                        database?.recipeDao()?.insert(mData)
                    }

                    _completeRecipeDataInsertEvent.value = Event(Unit)
                    hideProgress()
                    /*viewDataBinding.titleText.text = test?.get(0)?.title ?: "test"
                    Glide.with(this@MainActivity)
                        .load(test?.get(0)?.image)
                        .error(R.mipmap.ic_launcher)
                        .into(viewDataBinding.dbImageView)*/
                }
            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
                val done = (current.toDouble() / total * 100.0).toInt()
                Log.d("MYTAG", "UPLOAD - - ID: \$id, percent done = \$done")
            }

            override fun onError(id: Int, ex: Exception) {
                Log.d("MYTAG", "UPLOAD ERROR - - ID: \$id - - EX:$ex")
                hideProgress()
            }
        })
    }
}