package com.psm.mytable.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.MainActivity
import com.psm.mytable.Prefs
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentMainBinding
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.ui.recipe.RecipeSearchAdapter
import com.psm.mytable.ui.recipe.detail.RecipeDetailActivity
import com.psm.mytable.ui.recipe.update.RecipeUpdateActivity
import com.psm.mytable.ui.recipe.write.RecipeWriteActivity
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.hideProgress
import com.psm.mytable.utils.recyclerview.RecyclerViewHorizontalDecoration
import com.psm.mytable.utils.recyclerview.RecyclerViewVerticalDecoration
import com.psm.mytable.utils.showProgress

class MainFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentMainBinding
    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var recipeWriteResult: ActivityResultLauncher<Intent>
    private lateinit var recipeUpdateResult: ActivityResultLauncher<Intent>
    private lateinit var recipeDetailResult: ActivityResultLauncher<Intent>
    private lateinit var settingResult: ActivityResultLauncher<Intent>
    var showRecipeAdYN: String = "N"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeWriteResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == AppCompatActivity.RESULT_OK) {
                    viewModel.getRecipeCategory(RecipeType.ALL)
                }
            }

        recipeDetailResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        viewModel.getRecipeCategory(RecipeType.ALL)
                    }

                    9002 -> {
                        val mIntent = it.data
                        val itemData =
                            mIntent?.getParcelableExtra<RecipeItemData>(RecipeDetailActivity.EXTRA_RECIPE)
                        requireActivity().intent.getParcelableExtra<RecipeItemData>(
                            RecipeDetailActivity.EXTRA_RECIPE
                        )
                        val intent = Intent(activity, RecipeUpdateActivity::class.java)
                        intent.putExtra(MainActivity.EXTRA_UPDATE_RECIPE, itemData)
                        recipeUpdateResult.launch(intent)
                    }
                }
            }

        recipeUpdateResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == AppCompatActivity.RESULT_OK) {
                    viewModel.getRecipeCategory(RecipeType.ALL)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentMainBinding.inflate(inflater, container, false).apply {
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
        init()
        setupListAdapter()
        initAd()
    }

    override fun onResume() {
        super.onResume()
        // SearchView 자동 포커스 제거
        viewDataBinding.SearchView.setQuery("", false)
        viewDataBinding.mainLayout.requestFocus()
    }

    private fun init() = with(viewDataBinding) {
        // SearchView 자동 포커스 제거

        mainLayout.apply {
            isFocusableInTouchMode = true
            isFocusable = true
        }
        SearchView.apply {
            setIconifiedByDefault(false)
            isIconified = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                // 완료 누르면
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query.isNullOrEmpty()) {
                        ToastUtils.showToast("검색어를 입력해주세요.")
                    } else {
                        viewModel.filterSearchWordRecipe(query ?: "")
                        categoryRg.clearCheck()
                        //rbAll.isChecked = true
                    }
                    return false
                }

                // 검색어가 변경될때 마다
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText == "") {
                        viewModel.searchWord.value = ""
                        rbAll.isChecked = true
                    } else {
                        return false
                    }
                    return true
                }
            })
        }
        categoryRg.setOnCheckedChangeListener { group, checkedId ->
            if(viewModel.searchWord.value.isNullOrEmpty()) {
                when (checkedId) {
                    R.id.rbAll-> viewModel.getRecipeCategory(RecipeType.ALL)
                    R.id.rbKr -> viewModel.getRecipeCategory(RecipeType.KR)
                    R.id.rbJp -> viewModel.getRecipeCategory(RecipeType.JP)
                    R.id.rbWe -> viewModel.getRecipeCategory(RecipeType.WESTERN)
                    R.id.rbCn -> viewModel.getRecipeCategory(RecipeType.CN)
                    R.id.rbSb -> viewModel.getRecipeCategory(RecipeType.SNACKBAR)
                    R.id.rbBa -> viewModel.getRecipeCategory(RecipeType.BA)
                    else -> viewModel.getRecipeCategory(RecipeType.ALL)
                }
            } else {
                return@setOnCheckedChangeListener
            }

        }
    }

    private fun setupListAdapter() = with(viewDataBinding) {
        recipeList.apply {
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(RecyclerViewHorizontalDecoration(30))
            addItemDecoration(RecyclerViewVerticalDecoration(30))
            adapter = RecipeAdapter(viewModel)
            itemAnimator = null
        }
        searchResultList.apply {
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(RecyclerViewHorizontalDecoration(30))
            addItemDecoration(RecyclerViewVerticalDecoration(30))
            adapter = RecipeSearchAdapter(viewModel)
        }
    }

    private fun setupEvent() {
        viewModel.recipeListState.observe(viewLifecycleOwner, EventObserver { state ->
            when (state) {
                is RecipeListState.UnInitialized -> viewModel.getRecipeCategory(RecipeType.ALL)
                is RecipeListState.Loading -> showProgress(viewDataBinding.progress, activity)
                is RecipeListState.Error -> {
                    ToastUtils.showToast("알 수 없는 오류가 발생했습니다. 다시 시도바랍니다.")
                    activity?.finish()
                }

                is RecipeListState.Success -> hideProgress(viewDataBinding.progress, activity)
                is RecipeListState.Complete -> hideProgress(viewDataBinding.progress, activity)
            }
        })

        // 레시피 작성 화면 이동
        viewModel.goRecipeWriteEvent.observe(viewLifecycleOwner, EventObserver {
            Prefs.adStackRecipe++
            if (Prefs.adStackRecipe.toInt() % 3 == 0 && showRecipeAdYN == "N") {
                Prefs.adStackRecipe = 0
                if (mInterstitialAd != null) {
                    showProgress(viewDataBinding.progress, activity)
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            showInterstitial()
                        }, 1000
                    )
                } else {
                    goRecipeWrite()
                }
            } else {
                goRecipeWrite()
            }
        })

        // 레시피 상세 화면 이동
        viewModel.openRecipeDetailEvent.observe(viewLifecycleOwner, EventObserver { recipe ->
            goRecipeDetail(recipe)
        })
    }

    private fun goRecipeWrite() {
        val intent = Intent(requireContext(), RecipeWriteActivity::class.java)
        recipeWriteResult.launch(intent)
    }

    private fun goRecipeDetail(recipe: RecipeItemData) {
        val intent = Intent(activity, RecipeDetailActivity::class.java)
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE, recipe)
        recipeDetailResult.launch(intent)
    }


    private fun initAd() {
        InterstitialAd.load(
            App.instance,
            getString(R.string.recipe_front_admob_key),
            App.instance.adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                // Called When ad is dismissed
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    mInterstitialAd = null
                    hideProgress(viewDataBinding.progress, activity)
                    goRecipeWrite()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    mInterstitialAd = null
                }
            }
            showRecipeAdYN = "Y"
            mInterstitialAd?.show(requireActivity())
        } else {
            goRecipeWrite()
            hideProgress(viewDataBinding.progress, activity)
        }
    }

    companion object {
        fun newInstance() = MainFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}