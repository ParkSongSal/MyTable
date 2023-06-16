package com.psm.mytable

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psm.mytable.ui.main.MainFragment
import com.psm.mytable.utils.replaceFragmentInActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: replaceFragmentInActivity(MainFragment.newInstance(), R.id.contentFrame)
    }

    companion object {

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }
}