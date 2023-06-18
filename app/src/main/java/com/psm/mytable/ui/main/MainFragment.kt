package com.psm.mytable.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentMainBinding
import com.psm.mytable.room.RoomDB
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.ui.recipe.write.RecipeWriteActivity
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.recyclerview.RecyclerViewDecoration
import com.psm.mytable.utils.recyclerview.RecyclerViewHorizontalDecoration
import com.psm.mytable.utils.recyclerview.RecyclerViewVerticalDecoration

class MainFragment: Fragment(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var viewDataBinding: FragmentMainBinding
    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    private lateinit var recipeWriteResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        recipeWriteResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
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
        viewDataBinding.recipeList.layoutManager = GridLayoutManager(App.instance, 2)
        viewModel.init(requireContext())
        setupListAdapter()
        //setupListDivider()
        init()
    }

    private fun init(){
        viewDataBinding.navigationView.setNavigationItemSelectedListener(this)
        viewDataBinding.menuBtn.setOnClickListener{
            viewDataBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
        /*val adRequest = AdRequest.Builder().build()
        viewDataBinding.adView.loadAd(adRequest)*/
    }
    private fun checkPermission() {
    }


    private fun setupListAdapter(){

        viewDataBinding.recipeList.apply{
            addItemDecoration(RecyclerViewHorizontalDecoration(30))
            addItemDecoration(RecyclerViewVerticalDecoration(30))
            adapter = RecipeAdapter(viewModel)
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
            R.id.phone->{
                Toast.makeText(App.instance, "전화번호부", Toast.LENGTH_SHORT).show()
                return super.onOptionsItemSelected(item)
            }
            R.id.stopWatch->{
                Toast.makeText(App.instance, "스탑워치", Toast.LENGTH_SHORT).show()
                //intent = Intent(this, StopWatchActivity::class.java)
                //startActivity(intent)
                return super.onOptionsItemSelected(item)
            }
            R.id.settingItem->{
                // 설정 클릭시
                Toast.makeText(App.instance, "설정", Toast.LENGTH_SHORT).show()
                //intent = Intent(this, StopWatchActivity::class.java)
                //startActivity(intent)
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }
}