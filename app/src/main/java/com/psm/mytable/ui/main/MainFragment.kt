package com.psm.mytable.ui.main

import android.content.Intent
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
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentMainBinding
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.ui.recipe.write.RecipeWriteActivity
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.recyclerview.RecyclerViewDecoration

class MainFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentMainBinding
    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentMainBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this
        setupEvent()
        checkPermission()
        viewDataBinding.recipeList.layoutManager = LinearLayoutManager(App.instance)
        viewModel.init()
        setupListAdapter()
        //setupListDivider()
        init()
    }

    private fun init(){
        /*val adRequest = AdRequest.Builder().build()
        viewDataBinding.adView.loadAd(adRequest)*/
    }
    private fun checkPermission() {
    }


    private fun setupListAdapter(){
        viewDataBinding.recipeList.addItemDecoration(RecyclerViewDecoration(60))
        viewDataBinding.recipeList.adapter = RecipeAdapter(viewModel)
    }

    private fun setupListDivider(){
        ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)?.let {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(it)
            viewDataBinding.recipeList.addItemDecoration(divider)
        }
    }

    private fun setupEvent() {

        // 레시피 작성 화면 이동
        viewModel.goRecipeWriteEvent.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(requireContext(), RecipeWriteActivity::class.java)
            startActivity(intent)
        })
    }

    companion object {
        fun newInstance() = MainFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}