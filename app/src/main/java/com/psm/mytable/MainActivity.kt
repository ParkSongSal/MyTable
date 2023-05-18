package com.psm.mytable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psm.mytable.main.MainFragment
import com.psm.mytable.utils.replaceFragmentInActivity

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
}