package com.psm.mytable.ui.basket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psm.mytable.R
import com.psm.mytable.utils.replaceFragmentInActivity

class ShoppingBasketListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: replaceFragmentInActivity(ShoppingBasketListFragment.newInstance(), R.id.contentFrame)
    }

    companion object{
        const val EXTRA_RECIPE = "EXTRA_RECIPE"
    }
}