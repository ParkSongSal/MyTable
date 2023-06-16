package com.psm.mytable.ui.recipe.write

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentMainBinding
import com.psm.mytable.databinding.FragmentRecipeWriteBinding
import com.psm.mytable.ui.camera.CameraActivity
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.recyclerview.RecyclerViewDecoration
import com.psm.mytable.utils.setTitleText
import com.psm.mytable.utils.showPhotoSelectDialog
import com.psm.mytable.utils.showRecipeSelectDialog
import timber.log.Timber
import java.io.File

class RecipeWriteFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentRecipeWriteBinding
    private val viewModel by viewModels<RecipeWriteViewModel> { getViewModelFactory() }

    //private lateinit var imageCameraResult: ActivityResultLauncher<Intent>
    private lateinit var imageGalleryResult: ActivityResultLauncher<Intent>
    private lateinit var imageCameraResult: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
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
                    setRecipeImage(imageUri)
                }

            }
        }

        imageGalleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                it.data?.dataString?.let { uri ->
                    setRecipeImage(uri.toUri())
                }
            }
        }
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
        viewDataBinding.lifecycleOwner = this

        initToolbar(view)

        setTitleText(view, R.string.recipe_write_1_001)

        init()
        setupEvent()
    }

    private fun init(){

    }


    private fun setupEvent() {

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

        viewModel.openFoodTypeDialogEvent.observe(viewLifecycleOwner, EventObserver{
            showRecipeSelectDialog(
                positiveCallback = {
                    viewModel.setRecipeType(it)
                    ToastUtils.showToast(it.toString())
                }
            )
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
        fun newInstance() = RecipeWriteFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}