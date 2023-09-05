package com.psm.mytable.ui.ingredients.cold

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
import com.psm.mytable.databinding.FragmentColdStorageBinding
import com.psm.mytable.ui.ingredients.IngredientsActivity
import com.psm.mytable.ui.ingredients.IngredientsAdapter
import com.psm.mytable.ui.ingredients.IngredientsViewModel
import com.psm.mytable.ui.ingredients.ingredientUpdate.IngredientsUpdateActivity
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar

class ColdStorageFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentColdStorageBinding
    private val viewModel by viewModels<ColdStorageViewModel> { getViewModelFactory() }
    private val mViewModel by viewModels<IngredientsViewModel> { getViewModelFactory() }
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

        // View Binding 설정
        viewDataBinding = FragmentColdStorageBinding.inflate(inflater, container, false).apply{
            viewmodel = mViewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this

        // Toolbar 설정
        initToolbar(view)

        //
        mViewModel.appInit(requireContext())
        mViewModel.getColdStorageList()
        setupListAdapter()
        setupEvent()
    }

    private fun setupEvent() {
        mViewModel.openIngredientsDetailEvent.observe(viewLifecycleOwner, EventObserver{ingredients->
            val intent = Intent(activity, IngredientsUpdateActivity::class.java)
            intent.putExtra(IngredientsActivity.EXTRA_UPDATE_INGREDIENTS, ingredients)
            ingredientsDetailResult.launch(intent)
        })
    }

    private fun setupListAdapter(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        viewDataBinding.coldStorageList.apply{
            layoutManager = linearLayoutManager
            adapter = IngredientsAdapter(mViewModel)

        }
    }

    companion object {
        fun newInstance() = ColdStorageFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}