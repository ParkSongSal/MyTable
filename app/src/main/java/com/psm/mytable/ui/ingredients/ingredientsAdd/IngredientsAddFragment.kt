package com.psm.mytable.ui.ingredients.ingredientsAdd

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentIngredientsAddBinding
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import java.util.Calendar


class IngredientsAddFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentIngredientsAddBinding
    private val viewModel by viewModels<IngredientsAddViewModel> { getViewModelFactory() }

    var dateCallbackMethod: DatePickerDialog.OnDateSetListener? = null
    private var adLoader: AdLoader? = null
    private var isDestroyed : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentIngredientsAddBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this

        initToolbar(view)
        setTitleText(view, R.string.ingredients_add_1_000)
        viewModel.appInit(requireContext())
        setupEvent()
        InitializeListener()
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

        // 유통(소비) 기한 날짜 선택
        viewModel.selectExpirationDateEvent.observe(viewLifecycleOwner, EventObserver{
            val currentDate = Calendar.getInstance()
            val mYear = currentDate.get(Calendar.YEAR)
            val mMonth = currentDate.get(Calendar.MONTH)
            val mDay = currentDate.get(Calendar.DATE)
            //  .. 포커스시
            val dialog = DatePickerDialog(requireActivity(), dateCallbackMethod, mYear, mMonth, mDay)

            dialog.show()

        })

        // 유통(소비)기한 검색
        viewModel.expirationDateSearchEvent.observe(viewLifecycleOwner, EventObserver{
            val srchString = it
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.search.naver.com/search.naver?query=$srchString 유통기한"))
            startActivity(intent)
        })

        // 유통(소비)기한 검색 실패
        viewModel.expirationDateSearchErrorEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("검색할 재료를 먼저 입력바랍니다.")
        })

        viewModel.completeIngredientDataInsertEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("재료가 추가되었습니다.")
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })
    }

    fun InitializeListener() {
        dateCallbackMethod =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val month = when (monthOfYear + 1) {
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    10 -> "10"
                    11 -> "11"
                    12 -> "12"
                    else -> monthOfYear.toString()
                }
                val day = when (dayOfMonth) {
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> dayOfMonth.toString()
                }
                viewModel.setExpirationDate(year, month, day)
            }
    }

    companion object {
        fun newInstance() = IngredientsAddFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}