package com.psm.mytable.ui.ingredients

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.psm.mytable.R
import com.psm.mytable.databinding.ActivityIngredientsBinding

class IngredientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIngredientsBinding
    private val fragmentManager = supportFragmentManager
    private lateinit var transaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. View Binding 설정
        binding = ActivityIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Main Fragment 설정
        transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.frameLayout, IngredientsFragment())
        transaction.commit()


    }
}