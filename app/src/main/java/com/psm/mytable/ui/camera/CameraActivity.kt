package com.psm.mytable.ui.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.psm.mytable.R
import com.psm.mytable.utils.replaceFragmentInActivity

class CameraActivity: AppCompatActivity() {


    private lateinit var cameraFragment: CameraFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
        ?: replaceFragmentInActivity(CameraFragment.newInstance(), R.id.contentFrame)
    }

    companion object {
    }
}