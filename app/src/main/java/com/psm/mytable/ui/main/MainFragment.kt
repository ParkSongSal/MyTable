package com.psm.mytable.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.MainActivity
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentMainBinding
import com.psm.mytable.ui.basket.ShoppingBasketListActivity
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.ui.recipe.RecipeSearchAdapter
import com.psm.mytable.ui.recipe.detail.RecipeDetailActivity
import com.psm.mytable.ui.recipe.update.RecipeUpdateActivity
import com.psm.mytable.ui.recipe.write.RecipeWriteActivity
import com.psm.mytable.ui.setting.SettingActivity
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.recyclerview.RecyclerViewHorizontalDecoration
import com.psm.mytable.utils.recyclerview.RecyclerViewVerticalDecoration

class MainFragment: Fragment(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var viewDataBinding: FragmentMainBinding
    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var recipeWriteResult: ActivityResultLauncher<Intent>
    private lateinit var recipeUpdateResult: ActivityResultLauncher<Intent>
    private lateinit var recipeDetailResult: ActivityResultLauncher<Intent>
    private lateinit var settingResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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

        settingResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
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
        checkPermission()

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


        viewDataBinding.navigationView.setNavigationItemSelectedListener(this)
        viewDataBinding.menuBtn.setOnClickListener{
            viewDataBinding.drawerLayout.openDrawer(GravityCompat.START)

        }
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
    private fun checkPermission() {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_navigationmenu, menu)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            // 장바구니
            R.id.shoppingBasket->{
                val intent = Intent(activity, ShoppingBasketListActivity::class.java)
                startActivity(intent)
                viewDataBinding.drawerLayout.close()
                return super.onOptionsItemSelected(item)
            }
            // 설정
            R.id.setting->{
                val intent = Intent(activity, SettingActivity::class.java)
                settingResult.launch(intent)
                viewDataBinding.drawerLayout.close()
                return super.onOptionsItemSelected(item)
            }
            /*// 재료관리
            R.id.materialMng->{
                Toast.makeText(App.instance, "재료관리", Toast.LENGTH_SHORT).show()
                //intent = Intent(this, StopWatchActivity::class.java)
                //startActivity(intent)
                return super.onOptionsItemSelected(item)
            }
            // 주간식단
            R.id.weeklyDiet->{
                Toast.makeText(App.instance, "주간식단", Toast.LENGTH_SHORT).show()
                //intent = Intent(this, StopWatchActivity::class.java)
                //startActivity(intent)
                return super.onOptionsItemSelected(item)
            }

            // 스탑워치
            R.id.stopWatch->{
                Toast.makeText(App.instance, "스탑워치", Toast.LENGTH_SHORT).show()
                //intent = Intent(this, StopWatchActivity::class.java)
                //startActivity(intent)
                return super.onOptionsItemSelected(item)
            }
           */
        }
        return true
    }
}