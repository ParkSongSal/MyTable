/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.psm.mytable.ui.camera

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.databinding.FragmentCameraBinding
import com.psm.mytable.ui.recipe.write.RecipeWriteActivity
import com.psm.mytable.utils.getViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * Main fragment for this app. Implements all camera operations including:
 * - Viewfinder
 * - Photo taking
 * - Image analysis
 */
class CameraFragment : Fragment() {
    lateinit var binding : FragmentCameraBinding

    private val viewModel by viewModels<CameraViewModel> { getViewModelFactory() }

    private lateinit var outputDirectory: File
    private lateinit var mainExecutor: Executor
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null

    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainExecutor = ContextCompat.getMainExecutor(requireContext())
        cameraExecutor = Executors.newSingleThreadExecutor()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        val view = binding.root
        return view
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner
        setupEvent()

        startCamera(cameraSelector)

        // Determine the output directory
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.imageCameraCapture.setOnClickListener{
            takePhoto()
        }
    }

    fun setupEvent() {
        viewModel.completeTakeImageEvent.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(activity, RecipeWriteActivity::class.java)
            val mUri = it
            intent.putExtra("imageUri", mUri)
            activity?.setResult(RESULT_OK, intent)
            activity?.finish()
        })

    }

    private fun takePhoto(){
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            newJpgFileName())

        val outputoptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputoptions,
            ContextCompat.getMainExecutor(App.instance),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uriToString = photoFile.toUri().toString()
                    viewModel.completeImage(uriToString)
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.d("psm", "Photo capture failed : ${exception.message}")
                }

            }
        )
    }

    private fun startCamera(cameraSelector: CameraSelector){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(App.instance)

        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()
                .also{
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            try{
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }catch (exception: Exception){
                Log.d("psm","Use case binding failed $exception")
            }
        }, ContextCompat.getMainExecutor(App.instance))
    }




    private fun newJpgFileName(): String{
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "${filename}.jpg"
    }
    private fun getOutputDirectory(): File{
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let{
            File(it, "MyTable").apply{
                mkdirs()
            }
        }
        return if(mediaDir != null && mediaDir.exists()) mediaDir
        else activity?.filesDir!!
    }
    companion object {
        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)


        @JvmStatic fun newInstance(): CameraFragment = CameraFragment()
    }
}
