package com.psm.mytable.ui.recipe.write

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psm.mytable.R
import com.psm.mytable.ui.main.MainFragment
import com.psm.mytable.utils.replaceFragmentInActivity

class RecipeWriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: replaceFragmentInActivity(RecipeWriteFragment.newInstance(), R.id.contentFrame)
    }
}