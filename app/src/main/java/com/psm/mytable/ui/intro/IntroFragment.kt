package com.psm.mytable.ui.intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.MainActivity
import com.psm.mytable.databinding.FragmentIntroBinding
import com.psm.mytable.ui.ingredients.IngredientsActivity
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import timber.log.Timber

class IntroFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentIntroBinding
    private val viewModel by viewModels<IntroViewModel> { getViewModelFactory() }

    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentIntroBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this
        setupEvent()
        init()
    }

    private fun init(){
        lottieInit()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                initAd()
            },1500
        )
    }

    private fun lottieInit(){
        viewDataBinding.lottie.apply{
            playAnimation()
            loop(true)
        }

    }

    private fun setupEvent() {

        viewModel.appInitCompleteEvent.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(context, IngredientsActivity::class.java)
            startActivity(intent)
            activity?.finish()
            /*Handler(Looper.getMainLooper()).postDelayed(
                {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }, 1000
            )*/
        })
    }

    private fun initAd(){
        InterstitialAd.load(App.instance, "ca-app-pub-3145363349418895/5216668354", App.instance.adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                mInterstitialAd = null
                viewModel.appInit()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                super.onAdLoaded(interstitialAd)
                mInterstitialAd = interstitialAd

                if(mInterstitialAd != null){
                    showInterstitial()
                }else{
                    viewModel.appInit()
                    ToastUtils.showToast("Ad wasn`t loaded")
                }

            }
        })
    }

    fun showInterstitial(){
        if(mInterstitialAd != null){
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdClicked() {
                    super.onAdClicked()
                    Timber.d("psm_onAdClikced")
                }

                // Called When ad is dismissed
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Timber.d("psm_onAdDismissedFullScreenContent")
                    mInterstitialAd = null
                    viewModel.appInit()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    Timber.d("psm_onAdFailedToShowFullScreenContent")
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Timber.d("psm_onAdImpression")
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    Timber.d("psm_onAdShowedFullScreenContent")
                }

            }
            mInterstitialAd?.show(requireActivity())
           // viewModel.appInit()
        }else{
            viewModel.appInit()
        }
    }

    companion object {
        fun newInstance() = IntroFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}