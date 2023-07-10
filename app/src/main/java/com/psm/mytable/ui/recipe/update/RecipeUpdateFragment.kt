package com.psm.mytable.ui.recipe.update

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.MainActivity
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentRecipeUpdateBinding
import com.psm.mytable.ui.camera.CameraActivity
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import com.psm.mytable.utils.showPhotoSelectDialog
import com.psm.mytable.utils.showRecipeSelectDialog
import java.io.File

class RecipeUpdateFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentRecipeUpdateBinding
    private val viewModel by viewModels<RecipeUpdateViewModel> { getViewModelFactory() }

    private lateinit var imageGalleryResult: ActivityResultLauncher<Intent>
    private lateinit var imageCameraResult: ActivityResultLauncher<Intent>

    lateinit var mView:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validatePermission()

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

    private fun setRecipeImage(imageUri:Uri){
        Glide.with(App.instance)
            .load(imageUri)
            .into(viewDataBinding.recipeImage)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentRecipeUpdateBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        mView = view
        initToolbar(view)

        setTitleText(view, R.string.recipe_update_1_001)

        requireActivity().intent.getParcelableExtra<RecipeItemData>(MainActivity.EXTRA_UPDATE_RECIPE)?.let{
            viewModel.setRecipeData(it)
        }?: errorPage("잘못된 접근입니다.")

        viewModel.init(requireContext())
        init()
        setupEvent()
        initAd()
    }

    private fun init(){

    }

    private fun initAd(){
        viewDataBinding.adView.loadAd(App.instance.adRequest)
    }

    private fun errorPage(msg: String){
        activity?.finish()
        ToastUtils.showToast(msg)
    }

    private fun setupEvent() {
        // Toolbar Title Set
        viewModel.setTitleEvent.observe(viewLifecycleOwner, EventObserver{
            setTitleText(mView, it)
        })

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
                    ToastUtils.showToast(it.name)
                }
            )
        })

        // 레시피 종류 선택 팝업 호출
        viewModel.openFoodTypeDialogEvent.observe(viewLifecycleOwner, EventObserver{
            showRecipeSelectDialog(
                positiveCallback = {
                    viewModel.setRecipeType(it)
                    ToastUtils.showToast(it.toString())
                }
            )
        })

        viewModel.errorEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("오류가 발생했습니다.")
            activity?.setResult(Activity.RESULT_CANCELED)
            activity?.finish()
        })

        viewModel.completeRecipeDataUpdateEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("레시피가 수정되었습니다.")
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
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
                    ToastUtils.showToast("Permission Granted")
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
                    ToastUtils.showToast(R.string.storage_permission_denied_message)
                }
            }
            ).check()
    }

    companion object {
        fun newInstance() = RecipeUpdateFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}