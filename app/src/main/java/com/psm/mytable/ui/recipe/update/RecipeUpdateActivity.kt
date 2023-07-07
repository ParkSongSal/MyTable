package com.psm.mytable.ui.recipe.update

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.psm.mytable.R
import com.psm.mytable.utils.replaceFragmentInActivity

class RecipeUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: replaceFragmentInActivity(RecipeUpdateFragment.newInstance(), R.id.contentFrame)
    }
}