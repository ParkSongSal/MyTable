package com.psm.mytable.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.MainActivity
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentMainBinding
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.ui.recipe.RecipeSearchAdapter
import com.psm.mytable.ui.recipe.detail.RecipeDetailActivity
import com.psm.mytable.ui.recipe.update.RecipeUpdateActivity
import com.psm.mytable.ui.recipe.write.RecipeWriteActivity
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.recyclerview.RecyclerViewHorizontalDecoration
import com.psm.mytable.utils.recyclerview.RecyclerViewVerticalDecoration

class MainFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentMainBinding
    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var recipeWriteResult: ActivityResultLauncher<Intent>
    private lateinit var recipeUpdateResult: ActivityResultLauncher<Intent>
    private lateinit var recipeDetailResult: ActivityResultLauncher<Intent>
    private lateinit var settingResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeWriteResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                viewModel.getRecipeList()
            }
        }

        recipeDetailResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            when(it.resultCode){
                AppCompatActivity.RESULT_OK ->{
                    viewModel.getRecipeList()
                }
                9002 ->{
                    val mIntent = it.data
                    val itemData = mIntent?.getParcelableExtra<RecipeItemData>(RecipeDetailActivity.EXTRA_RECIPE)
                    requireActivity().intent.getParcelableExtra<RecipeItemData>(RecipeDetailActivity.EXTRA_RECIPE)
                    val intent = Intent(activity, RecipeUpdateActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_UPDATE_RECIPE, itemData)
                    recipeUpdateResult.launch(intent)
                }
            }
        }

        recipeUpdateResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                viewModel.getRecipeList()
            }
        }
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

        view.findViewById<View>(R.id.imgToolbarBack)?.apply {
            setOnClickListener {
                activity?.finish()
            }
        }

        viewModel.init(requireContext())
        setupListAdapter()
        init()
    }

    override fun onResume() {
        super.onResume()

        // SearchView 자동 포커스 제거
        viewDataBinding.SearchView.setQuery("", false)
        viewDataBinding.mainLayout.requestFocus()
    }

    private fun init(){



        // SearchView 자동 포커스 제거
        viewDataBinding.mainLayout.isFocusableInTouchMode = true
        viewDataBinding.mainLayout.isFocusable = true

        viewDataBinding.SearchView.setIconifiedByDefault(false)
        viewDataBinding.SearchView.isIconified = false
        viewDataBinding.SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            // 완료 누르면
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // 검색어가 변경될때 마다
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterSearchWordRecipe(newText ?: "")
                return true
            }

        })
    }
    private fun setupListAdapter(){

        viewDataBinding.recipeList.apply{
            layoutManager = GridLayoutManager(App.instance, 2)
            addItemDecoration(RecyclerViewHorizontalDecoration(30))
            addItemDecoration(RecyclerViewVerticalDecoration(30))
            adapter = RecipeAdapter(viewModel)
            //adapter = pagingAdapter
        }

        viewDataBinding.searchResultList.apply{
            layoutManager = GridLayoutManager(App.instance, 2)
            addItemDecoration(RecyclerViewHorizontalDecoration(30))
            addItemDecoration(RecyclerViewVerticalDecoration(30))
            adapter = RecipeSearchAdapter(viewModel)
        }
    }

    private fun setupEvent() {

        // 레시피 작성 화면 이동
        viewModel.goRecipeWriteEvent.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(requireContext(), RecipeWriteActivity::class.java)
            recipeWriteResult.launch(intent)
        })

        // 레시피 상세 화면 이동
        viewModel.openRecipeDetailEvent.observe(viewLifecycleOwner, EventObserver{ recipe ->
            val intent = Intent(activity, RecipeDetailActivity::class.java)
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE, recipe)
            recipeDetailResult.launch(intent)
        })
    }

    companion object {
        fun newInstance() = MainFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}