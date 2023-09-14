package com.psm.mytable.ui.ingredients.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.psm.mytable.R
import com.psm.mytable.utils.replaceFragmentInActivity

class IngredientsSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: replaceFragmentInActivity(IngredientsSearchFragment.newInstance(), R.id.contentFrame)
    }
}