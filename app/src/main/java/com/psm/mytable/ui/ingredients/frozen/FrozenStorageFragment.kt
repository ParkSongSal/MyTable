package com.psm.mytable.ui.ingredients.frozen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentFrozenStorageBinding
import com.psm.mytable.databinding.FragmentSettingBinding
import com.psm.mytable.type.AppEvent
import com.psm.mytable.ui.ingredients.IngredientsAdapter
import com.psm.mytable.ui.ingredients.IngredientsViewModel
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import com.psm.mytable.utils.showYesNoDialog

class FrozenStorageFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentFrozenStorageBinding
    private val viewModel by viewModels<IngredientsViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentFrozenStorageBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this

        initToolbar(view)

        viewModel.appInit(requireContext())
        // 냉동 식재료 리스트 조회
        viewModel.getFrozenStorageList()
        setupListAdapter()

        setupEvent()
    }

    private fun setupEvent() {
    }


    private fun setupListAdapter(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        viewDataBinding.frozenStorageList.apply{
            layoutManager = linearLayoutManager
            adapter = IngredientsAdapter(viewModel)

        }
    }

    companion object {
        fun newInstance() = FrozenStorageFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}