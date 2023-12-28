package com.psm.mytable.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentSettingBinding
import com.psm.mytable.type.AppEvent
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import com.psm.mytable.utils.showYesNoDialog

class SettingFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentSettingBinding
    private val viewModel by viewModels<SettingViewModel> { getViewModelFactory() }
    private var adLoader: AdLoader? = null
    private var isDestroyed : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentSettingBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this

        initToolbar(view)
        setTitleText(view, R.string.setting_1_001)

        viewModel.appInit(requireContext())

        setupEvent()
        initAd()
        adLoader?.loadAd(AdRequest.Builder().build())
    }

    override fun onDestroy() {
        super.onDestroy()
        isDestroyed = true
    }
    private fun initAd() {
        MobileAds.initialize(requireActivity())
        adLoader = AdLoader.Builder(App.instance, getString(R.string.native_admob_key))
            .forNativeAd{ad : NativeAd ->
                if(isDestroyed){
                    ad.destroy()
                    return@forNativeAd
                }
                viewDataBinding.myTemplate.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {

                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            ).build()
    }
    private fun setupEvent() {

        viewModel.appEvent.observe(viewLifecycleOwner, EventObserver{
            when(it){
                AppEvent.SHARE ->{
                    appShareListener()
                }
                AppEvent.INQUIRE -> {
                    inquireListener()
                }
                else -> {
                    ToastUtils.showToast("잘못된 요청입니다.")
                }
            }
        })

        viewModel.dataResetConfirmEvent.observe(viewLifecycleOwner, EventObserver{
            val message = getString(R.string.data_reset_dialog_1_001)
            val positiveButton = getString(R.string.reset)
            val negativeButton = getString(R.string.cancel)
            showYesNoDialog(message, positiveButton, negativeButton, positiveCallback = {
                viewModel.resetAct()
            })
        })

        viewModel.errorEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("오류가 발생했습니다.")
        })

        viewModel.resetCompleteEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("데이터가 초기화 되었습니다.")
            activity?.setResult(Activity.RESULT_OK)
        })

    }

    // 문의하기
    private fun inquireListener(){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        // email setting 배열로 해놔서 복수 발송 가능
        val address = arrayOf("psmapp0102@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, address)
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(intent)
    }

    // 앱 공유하기
    private fun appShareListener(){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.psm.mytable")
        val chooserIntent = Intent.createChooser(intent, "앱공유하기")
        activity?.startActivity(chooserIntent)
    }

    companion object {
        fun newInstance() = SettingFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}