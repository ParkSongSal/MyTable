package com.psm.mytable.ui.ingredients.roomTemperature

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.psm.mytable.EventObserver
import com.psm.mytable.databinding.FragmentRoomTemperatureStorageBinding
import com.psm.mytable.ui.ingredients.IngredientsActivity
import com.psm.mytable.ui.ingredients.IngredientsAdapter
import com.psm.mytable.ui.ingredients.IngredientsViewModel
import com.psm.mytable.ui.ingredients.ingredientUpdate.IngredientsUpdateActivity
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar

class RoomTemperatureStorageFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentRoomTemperatureStorageBinding
    private val viewModel by viewModels<IngredientsViewModel> { getViewModelFactory() }

    private lateinit var ingredientsDetailResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ingredientsDetailResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                activity?.recreate()
            }
        }
    }

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
        viewModel.openIngredientsDetailEvent.observe(viewLifecycleOwner, EventObserver{ingredients->
            val intent = Intent(activity, IngredientsUpdateActivity::class.java)
            intent.putExtra(IngredientsActivity.EXTRA_UPDATE_INGREDIENTS, ingredients)
            ingredientsDetailResult.launch(intent)
        })
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