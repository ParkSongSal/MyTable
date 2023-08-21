package com.psm.mytable.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psm.mytable.R
import com.psm.mytable.utils.replaceFragmentInActivity

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: replaceFragmentInActivity(SettingFragment.newInstance(), R.id.contentFrame)
    }
}