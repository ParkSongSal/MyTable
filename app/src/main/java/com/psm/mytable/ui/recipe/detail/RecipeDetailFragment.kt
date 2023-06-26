package com.psm.mytable.ui.recipe.detail

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentRecipeDetailBinding
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText

class RecipeDetailFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentRecipeDetailBinding
    private val viewModel by viewModels<RecipeDetailViewModel> { getViewModelFactory() }

    lateinit var mView:View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentRecipeDetailBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        mView = view
        initToolbar(view)
        setTitleText(view, R.string.recipe_detail_1_001)

        viewModel.init(requireContext())

        requireActivity().intent.getParcelableExtra<RecipeItemData>(RecipeDetailActivity.EXTRA_RECIPE)?.let{
            viewModel.getRecipeDetailData(it)
        }?: errorPage()

        setupEvent()

        initView()
    }

    private fun initView(){
        mView.findViewById<View>(R.id.imgToolbarMore)?.apply {
            setOnClickListener {
                if(getLayoutPopupMenuVisibility() == View.VISIBLE) {
                    viewModel.hideMoreMenuLayout()
                } else {
                    viewModel.showMoreMenuLayout()
                }
            }
        }
    }

    private fun errorPage(){
        activity?.finish()
        ToastUtils.showToast("잘못된 접근입니다.")
    }

    private fun setupEvent() {

        // Toolbar Title Set
        viewModel.setTitleEvent.observe(viewLifecycleOwner, EventObserver{
            setTitleText(mView, it)
        })

        viewModel.completeRecipeDeleteEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("레시피가 삭제되었습니다.")
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })
    }

    private fun getLayoutPopupMenuVisibility(): Int {
        return mView.findViewById<View>(R.id.layoutPopupMenu).visibility
    }

    companion object {
        fun newInstance() = RecipeDetailFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}