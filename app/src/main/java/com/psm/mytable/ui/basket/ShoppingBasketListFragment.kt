package com.psm.mytable.ui.basket

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.Prefs
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentShoppingBasketListBinding
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.hideProgress
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import com.psm.mytable.utils.showItemAddDialog
import com.psm.mytable.utils.showProgress
import com.psm.mytable.utils.showYesNoDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShoppingBasketListFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentShoppingBasketListBinding
    private val viewModel by viewModels<ShoppingBasketListViewModel> { getViewModelFactory() }
    private var mInterstitialAd: InterstitialAd? = null

    lateinit var mView:View
    lateinit var mAdapter : ShoppingBasketPagingAdapter

    var showBasketAdYN : String = "N"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentShoppingBasketListBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mView = view
        initToolbar(view)
        setTitleText(view, R.string.shopping_basket_list_1_001)

        viewModel.init(requireContext())
        setupEvent()
        setupListAdapter()

        initAd()

        lifecycleScope.launch{
            viewModel.shoppingBasketPagingData.collectLatest{
                mAdapter.submitData(it)
            }
        }
    }


    private fun setupListAdapter(){
        mAdapter = ShoppingBasketPagingAdapter()
        viewDataBinding.shoppingList.adapter = mAdapter
        viewDataBinding.shoppingList.layoutManager = LinearLayoutManager(App.instance)
        viewDataBinding.shoppingList.itemAnimator = null

        mAdapter.removeListener(object: ShoppingBasketPagingAdapter.CustomListenerInterface {
            override fun removeListener(position: Int, itemData: ShoppingBasketItemData) {
                viewModel.clickDeleteItem(itemData)
            }
        })
    }

    private fun errorPage(msg: String){
        activity?.finish()
        ToastUtils.showToast(msg)
    }

    private fun showItemAddDialogEvent(){
        val title = getString(R.string.shopping_dialog_1_001)
        val hint = getString(R.string.shopping_dialog_1_002)
        showItemAddDialog(title, hint, inputCallback = {
            viewModel.addShoppingItemAct(it)
        }, cancelCallback = {})
    }

    private fun setupEvent() {
        viewModel.showAddShoppingItemDialogEvent.observe(viewLifecycleOwner, EventObserver{
            Prefs.adStackBasket++
            if(Prefs.adStackBasket.toInt() % 3 == 0 && showBasketAdYN == "N"){
                Prefs.adStackBasket = 0
                if(mInterstitialAd != null){
                    showProgress(viewDataBinding.progress, activity)
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            showInterstitial()
                        },1500
                    )
                }else{
                    showItemAddDialogEvent()
                }
            }else{
                showItemAddDialogEvent()
            }
        })

        viewModel.completeShoppingItemInsertEvent.observe(viewLifecycleOwner, EventObserver{
            lifecycleScope.launch {
                mAdapter.refresh()
                viewModel.getShoppingBasketListCount()
            }
        })

        viewModel.completeShoppingItemDeleteEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("삭제되었습니다.")
            mAdapter.refresh()
        })


        viewModel.showDeleteShoppingItemDialogEvent.observe(viewLifecycleOwner, EventObserver{itemData->
            val message = getString(R.string.shopping_dialog_1_003)
            val positiveButton = getString(R.string.confirm)
            val negativeButton = getString(R.string.cancel)
            showYesNoDialog(message, positiveButton, negativeButton, positiveCallback = {
                viewModel.deleteShoppingItemAct(itemData)
            })
        })
    }



    private fun initAd(){
        InterstitialAd.load(App.instance, getString(R.string.basket_front_admob_key), App.instance.adRequest, object : InterstitialAdLoadCallback(){
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

    private fun showInterstitial(){
        if(mInterstitialAd != null){
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                // Called When ad is dismissed
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    mInterstitialAd = null
                    //showItemAddDialogEvent()
                    hideProgress(viewDataBinding.progress, activity)
                    showItemAddDialogEvent()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }

            }
            showBasketAdYN = "Y"
            mInterstitialAd?.show(requireActivity())
        }else{
            showItemAddDialogEvent()
            hideProgress(viewDataBinding.progress, activity)
        }
    }

    companion object {
        fun newInstance() = ShoppingBasketListFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}