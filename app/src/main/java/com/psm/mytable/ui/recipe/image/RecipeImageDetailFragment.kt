package com.psm.mytable.ui.recipe.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentRecipeImageDetailBinding
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.ui.recipe.detail.RecipeDetailActivity
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText

class RecipeImageDetailFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentRecipeImageDetailBinding
    private val viewModel by viewModels<RecipeImageDetailViewModel> { getViewModelFactory() }

    lateinit var mView:View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentRecipeImageDetailBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this
        setupEvent()
        mView = view

        initToolbar(view)
        requireActivity().intent.getParcelableExtra<RecipeItemData>(RecipeImageDetailActivity.EXTRA_RECIPE)?.let{
            viewModel.getRecipeDetailData(it)
        }?: errorPage("잘못된 접근입니다.")

    }

    private fun setupEvent() {
        // Toolbar Title Set
        viewModel.setTitleEvent.observe(viewLifecycleOwner, EventObserver{
            setTitleText(mView, it)
        })
    }

    private fun errorPage(msg: String){
        activity?.finish()
        ToastUtils.showToast(msg)
    }


    companion object {
        fun newInstance() = RecipeImageDetailFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}