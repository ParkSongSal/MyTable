package com.psm.mytable.ui.recipe.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.psm.mytable.App
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentMainBinding
import com.psm.mytable.databinding.FragmentRecipeWriteBinding
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.recyclerview.RecyclerViewDecoration
import com.psm.mytable.utils.setTitleText

class RecipeWriteFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentRecipeWriteBinding
    private val viewModel by viewModels<RecipeWriteViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentRecipeWriteBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this
        initToolbar(view)
        setTitleText(view, R.string.recipe_write_1_001)

        setupEvent()
    }

    private fun init(){
    }


    private fun setupEvent() {

    }

    companion object {
        fun newInstance() = RecipeWriteFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}