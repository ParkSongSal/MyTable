package com.psm.mytable.ui.ingredients.roomTemperature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.psm.mytable.databinding.FragmentRoomTemperatureStorageBinding
import com.psm.mytable.ui.ingredients.IngredientsAdapter
import com.psm.mytable.ui.ingredients.IngredientsViewModel
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar

class RoomTemperatureStorageFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentRoomTemperatureStorageBinding
    private val viewModel by viewModels<IngredientsViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentRoomTemperatureStorageBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this

        initToolbar(view)

        viewModel.appInit(requireContext())
        viewModel.getRoomTemperatureList()
        setupListAdapter()

        setupEvent()
    }

    private fun setupEvent() {
    }


    private fun setupListAdapter(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        viewDataBinding.roomTemperatureStorageList.apply{
            layoutManager = linearLayoutManager
            adapter = IngredientsAdapter(viewModel)

        }
    }

    companion object {
        fun newInstance() = RoomTemperatureStorageFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}