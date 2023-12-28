package com.psm.mytable.ui.recipe.write

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.Prefs
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentRecipeWriteBinding
import com.psm.mytable.ui.camera.CameraActivity
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import com.psm.mytable.utils.showPhotoSelectDialog
import com.psm.mytable.utils.showRecipeSelectDialog
import com.psm.mytable.utils.showTempSaveDialog
import com.psm.mytable.utils.showYesNoDialog
import java.io.File

class RecipeWriteFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentRecipeWriteBinding
    private val viewModel by viewModels<RecipeWriteViewModel> { getViewModelFactory() }

    private lateinit var imageGalleryResult: ActivityResultLauncher<Intent>
    private lateinit var imageCameraResult: ActivityResultLauncher<Intent>

    private lateinit var callback: OnBackPressedCallback

    private var adLoader: AdLoader? = null
    private var isDestroyed : Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                checkSaveDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validatePermission()

        // 기존 카메라
        /*imageCameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri = this.imageUri ?: throw IllegalStateException()
                setRecipeImage(imageUri)
            }
        }*/

        imageCameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                val imageUri = it.data?.getStringExtra("imageUri")?.toUri()
                if(imageUri != null){
                    viewModel.setRecipeImageUri(imageUri)
                    setRecipeImage(imageUri)
                }

            }
        }

        imageGalleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                it.data?.dataString?.let { uri ->
                    viewModel.setRecipeImageUri(uri.toUri())
                    setRecipeImage(uri.toUri())
                }
            }
        }
    }

    fun checkSaveDialog(){
        if(viewModel.recipeWriteData.value?.recipeName?.isNotEmpty() == true ||
                viewModel.recipeWriteData.value?.ingredients?.isNotEmpty() == true ||
                viewModel.recipeWriteData.value?.howToMake?.isNotEmpty() == true){
            showTempSaveDialogListener()
        }else{
            activity?.finish()
        }
    }

    private fun showTempSaveDialogListener(){
        showTempSaveDialog(positiveCallback = {
            Prefs.recipeName = viewModel.recipeWriteData.value?.recipeName ?: ""
            Prefs.ingredients = viewModel.recipeWriteData.value?.ingredients ?: ""
            Prefs.howToMake = viewModel.recipeWriteData.value?.howToMake ?: ""
            ToastUtils.showToast("임시저장 되었습니다.")
            activity?.finish()
        }, negativeCallback = {
            activity?.finish()
        })
    }

    fun setRecipeImage(imageUri:Uri){
        if(imageUri.toString().isNotEmpty()){
            viewDataBinding.recipeImageText.visibility = View.GONE
            Glide.with(App.instance)
                .load(imageUri)
                .into(viewDataBinding.recipeImage)
        }else{
            viewDataBinding.recipeImageText.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentRecipeWriteBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        initToolbar(view)
        initView(view)
        setTitleText(view, R.string.recipe_write_1_001)

        viewModel.init(requireContext())
        init()
        initAd()
        setupEvent()
        checkTempSaved()
    }


    private fun initView(view: View){
        val toolbarClose = view.findViewById<ImageFilterView>(R.id.imgToolbarClose)
        toolbarClose.setOnClickListener{
            checkSaveDialog()
        }
    }

    private fun init(){

    }

    private fun checkTempSaved(){
        if(Prefs.recipeName.isNotEmpty() || Prefs.ingredients.isNotEmpty() || Prefs.howToMake.isNotEmpty()){
            val message = getString(R.string.recipe_dialog_1_003)
            val positiveButton = getString(R.string.confirm)
            val negativeButton = getString(R.string.cancel)
            showYesNoDialog(message, positiveButton, negativeButton, positiveCallback = {
                setTempSavedData()
            })
        }else{
            return
        }
    }

    private fun setTempSavedData(){
        viewModel.recipeWriteData.value?.recipeName = Prefs.recipeName
        viewModel.recipeWriteData.value?.ingredients = Prefs.ingredients
        viewModel.recipeWriteData.value?.howToMake = Prefs.howToMake
    }


    override fun onDestroy() {
        super.onDestroy()
        isDestroyed = true
    }
    private fun initAd(){
        MobileAds.initialize(requireActivity())
        adLoader = AdLoader.Builder(App.instance, getString(R.string.native_admob_key))
            .forNativeAd{ad : NativeAd ->
                if(isDestroyed){
                    ad.destroy()
                    return@forNativeAd
                }
                viewDataBinding.myTemplate.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {

                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            ).build()

        adLoader?.loadAd(AdRequest.Builder().build())

    }


    private fun setupEvent() {

        // 이미지 가져오기(카메라or앨범선택) 팝업 호출
        viewModel.openPhotoDialogEvent.observe(viewLifecycleOwner, EventObserver{
            showPhotoSelectDialog(
                positiveCallback = {
                    when(it.name){
                        // 카메라 촬영
                        "CAMERA"-> {
                            val intent = Intent(activity, CameraActivity::class.java)
                            imageCameraResult.launch(intent)
                            /*imageUri = createImageUri()
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                            imageCameraResult.launch(intent)*/
                        }
                        // 앨범선택
                        else -> {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = MediaStore.Images.Media.CONTENT_TYPE
                            imageGalleryResult.launch(intent)
                        }
                    }
                }
            )
        })

        // 레피시 종류 선택 팝업 호출
        viewModel.openFoodTypeDialogEvent.observe(viewLifecycleOwner, EventObserver{
            showRecipeSelectDialog(
                positiveCallback = {
                    viewModel.setRecipeType(it)
                }
            )
        })

        viewModel.completeRecipeDataInsertEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("레시피가 등록되었습니다.")
            Prefs.clearRecipe()
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
            //viewModel.getAllRecipe()
        })
    }

    private fun createImageUri(): Uri {
        val ctx = App.instance ?: throw IllegalStateException()
        val photoFile = createImageFile()
        return FileProvider.getUriForFile(ctx, ctx.packageName, photoFile)
    }


    private fun createImageFile(): File {
        val ctx = App.instance ?: throw IllegalStateException()
        val storageDir =
            ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: throw IllegalStateException()
        val imageFileName = "Image_" + System.currentTimeMillis()
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    // Camera 권한 체크
    private fun validatePermission() {
        Dexter.withContext(App.instance)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    //ToastUtils.showToast("Permission Granted")
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    AlertDialog.Builder(App.instance)
                        .setTitle(R.string.storage_permission_rationale_title)
                        .setMessage(R.string.storage_permission_rationale_message)
                        .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            token?.cancelPermissionRequest()
                        }
                        .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            token?.continuePermissionRequest()
                        }
                        .show()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    //ToastUtils.showToast(R.string.storage_permission_denied_message)
                }
            }
            ).check()
    }

    companion object {
        fun newInstance() = RecipeWriteFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}