package com.psm.mytable.ui.ingredients.search

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentIngredientsSearchBinding
import com.psm.mytable.ui.ingredients.IngredientsActivity
import com.psm.mytable.ui.ingredients.IngredientsAdapter
import com.psm.mytable.ui.ingredients.IngredientsViewModel
import com.psm.mytable.ui.ingredients.ingredientUpdate.IngredientsUpdateActivity
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText

class IngredientsSearchFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentIngredientsSearchBinding
    private val viewModel by viewModels<IngredientsSearchViewModel> { getViewModelFactory() }
    private val ingredientViewModel by viewModels<IngredientsViewModel> { getViewModelFactory() }

    private lateinit var listAdapter: IngredientsAdapter
    private lateinit var searchListAdapter: IngredientSearchAdapter
    private lateinit var searchListResultAdapter: IngredientSearchResultAdapter
    var dateCallbackMethod: DatePickerDialog.OnDateSetListener? = null

    private lateinit var ingredientsDetailResult: ActivityResultLauncher<Intent>

    private lateinit var callback: OnBackPressedCallback


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                closeResultListener()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun closeResultListener(){
        if(App.instance.isIngredientChange){
            activity?.setResult(Activity.RESULT_OK)
        }else{
            activity?.setResult(Activity.RESULT_CANCELED)
        }
        activity?.finish()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ingredientsDetailResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                App.instance.isIngredientChange = true
                viewModel.getIngredientsList()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentIngredientsSearchBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this

        initToolbar(view)
        setTitleText(view, R.string.ingredients_search_1_000)
        viewModel.appInit(requireContext())
        setupEvent()
        setupListAdapter()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = IngredientsAdapter(ingredientViewModel)
            searchListAdapter = IngredientSearchAdapter(ingredientViewModel)
            searchListResultAdapter = IngredientSearchResultAdapter(ingredientViewModel)
            viewDataBinding.ingredientList.adapter = searchListAdapter
            viewDataBinding.ingredientSearchResultList.adapter = searchListResultAdapter
        } else {
            //Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupEvent() {

        viewModel.backEvent.observe(viewLifecycleOwner, EventObserver{
            closeResultListener()


        })

        ingredientViewModel.openIngredientsDetailEvent.observe(viewLifecycleOwner, EventObserver{ingredients->
            val intent = Intent(activity, IngredientsUpdateActivity::class.java)
            intent.putExtra(IngredientsActivity.EXTRA_UPDATE_INGREDIENTS, ingredients)
            ingredientsDetailResult.launch(intent)
        })

        viewModel.ingredientsListSortEvent.observe(viewLifecycleOwner, EventObserver{
            searchListAdapter.notifyDataSetChanged()
        })

        viewModel.searchResultSortEvent.observe(viewLifecycleOwner, EventObserver{
            searchListResultAdapter.notifyDataSetChanged()
        })
    }
    companion object {
        fun newInstance() = IngredientsSearchFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}