package com.psm.mytable.ui.ingredients

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.MainActivity
import com.psm.mytable.R
import com.psm.mytable.adapter.ViewPagerFragmentStateAdapter
import com.psm.mytable.databinding.FragmentIngredientsBinding
import com.psm.mytable.ui.basket.ShoppingBasketListActivity
import com.psm.mytable.ui.ingredients.ingredientUpdate.IngredientsUpdateActivity
import com.psm.mytable.ui.ingredients.ingredientsAdd.IngredientsAddActivity
import com.psm.mytable.ui.ingredients.search.IngredientsSearchActivity
import com.psm.mytable.ui.setting.SettingActivity
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import timber.log.Timber

class IngredientsFragment: Fragment(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var viewDataBinding: FragmentIngredientsBinding
    private val viewModel by viewModels<IngredientsViewModel> { getViewModelFactory() }


    private lateinit var settingResult: ActivityResultLauncher<Intent>
    private lateinit var ingredientAddResult: ActivityResultLauncher<Intent>
    private lateinit var ingredientsDetailResult: ActivityResultLauncher<Intent>
    private lateinit var ingredientSearchResult: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                activity?.recreate()
            }
        }

        ingredientAddResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                activity?.recreate()
            }
        }

        ingredientsDetailResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                activity?.recreate()
            }
        }

        ingredientSearchResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            when(it.resultCode){
                AppCompatActivity.RESULT_OK -> {
                    App.instance.isIngredientChange = false
                    activity?.recreate()
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    return@registerForActivityResult
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewDataBinding = FragmentIngredientsBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }

        // 1. View Binding 설정
        //viewDataBinding = FragmentIngredientsBinding.inflate(inflater, container, false)

        // 2. View Pager의 FragmentStateAdapter 설정
        viewDataBinding.vpIngredients.adapter = activity?.let{ ViewPagerFragmentStateAdapter(it) }

        // 3. View Pager의 Orientation 설정
        viewDataBinding.vpIngredients.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // 4. TabLayout + ViewPager2 연동 (ViewPager2에 Adapter 연동 후에)
        TabLayoutMediator(viewDataBinding.tabLayout, viewDataBinding.vpIngredients){ tab, position ->
            tab.text = getTabTitle(position)
        }.attach()


        return viewDataBinding.root
    }

    // Tab & ViewPager 연동 및 Tab Title 설정
    private fun getTabTitle(position: Int): String?{
        return when(position){
            0 -> "냉장"
            1 -> "냉동"
            2 -> "실온"
            else -> null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this

        initToolbar(view)
        setTitleText(view, R.string.ingredients_1_001)
        init()
        setUpEvent()
        //initToolbar(view)
        //setTitleText(view, R.string.setting_1_001)
        viewModel.appInit(requireContext())
    }

    private fun init(){

        viewDataBinding.navigationView.setNavigationItemSelectedListener(this)
        viewDataBinding.menuBtn.setOnClickListener{
            viewDataBinding.drawerLayout.openDrawer(GravityCompat.START)

        }
    }

    private fun setUpEvent(){

        // 식재료 검색 화면 이동
        viewModel.goIngredientSearchEvent.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(activity, IngredientsSearchActivity::class.java)
            ingredientSearchResult.launch(intent)
        })

        // 식재료 추가 화면 이동
        viewModel.goIngredientAddEvent.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(activity, IngredientsAddActivity::class.java)
            ingredientAddResult.launch(intent)
        })

        // 식재료 수정 화면 이동
        viewModel.openIngredientsDetailEvent.observe(viewLifecycleOwner, EventObserver{ingredients->
            val intent = Intent(activity, IngredientsUpdateActivity::class.java)
            intent.putExtra(IngredientsActivity.EXTRA_UPDATE_INGREDIENTS, ingredients)
            ingredientsDetailResult.launch(intent)
        })
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

            // 재료관리
            /*R.id.ingredient->{
                val intent = Intent(activity, IngredientsActivity::class.java)
                startActivity(intent)
                //settingResult.launch(intent)
                viewDataBinding.drawerLayout.close()
                return super.onOptionsItemSelected(item)
            }*/
            // 레시피 목록
            R.id.recipe->{
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                //settingResult.launch(intent)
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

    companion object {
        fun newInstance() = IngredientsFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}