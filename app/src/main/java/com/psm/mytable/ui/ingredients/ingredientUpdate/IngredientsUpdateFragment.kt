package com.psm.mytable.ui.ingredients.ingredientUpdate

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
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.databinding.FragmentIngredientsUpdateBinding
import com.psm.mytable.ui.ingredients.IngredientsActivity
import com.psm.mytable.ui.ingredients.IngredientsItemData
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import java.util.Calendar

class IngredientsUpdateFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentIngredientsUpdateBinding
    private val viewModel by viewModels<IngredientsUpdateViewModel> { getViewModelFactory() }

    var dateCallbackMethod: DatePickerDialog.OnDateSetListener? = null
    lateinit var mView:View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentIngredientsUpdateBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this

        mView = view
        initToolbar(view)

        requireActivity().intent.getParcelableExtra<IngredientsItemData>(IngredientsActivity.EXTRA_UPDATE_INGREDIENTS)?.let{
            viewModel.setIngredientsData(it)
        }?: errorPage("잘못된 접근입니다.")

        viewModel.appInit(requireContext())
        initAd()
        setupEvent()
        InitializeListener()
    }

    private fun initAd(){
        viewDataBinding.adView.loadAd(App.instance.adRequest)
    }

    private fun setupEvent() {

        viewModel.setTitleEvent.observe(viewLifecycleOwner, EventObserver{
            setTitleText(mView, it)
        })

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

        viewModel.completeIngredientDataUpdateEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("재료가 수정되었습니다.")
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })

        viewModel.recipeSearchEvent.observe(viewLifecycleOwner, EventObserver{map ->
            val searchType = map["searchType"]
            val ingredientName = map["ingredientName"]
            val srchString = ingredientName
            when(searchType){
                "NAVER" -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.search.naver.com/search.naver?query=$srchString 레시피"))
                    startActivity(intent)
                }
                "MANGAE" -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.10000recipe.com/recipe/list.html?q=$srchString 레시피"))
                    startActivity(intent)
                }
                "YOUTUBE" -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$srchString 레시피"))
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.search.naver.com/search.naver?query=$srchString 레시피"))
                    startActivity(intent)
                }
            }
        })

        // 재료 삭제 완료
        viewModel.completeIngredientDeleteEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("재료가 삭제되었습니다.")
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })

        // 오류 발생
        viewModel.errorEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("다시 시도바랍니다.")
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })
    }

    private fun errorPage(msg: String){
        activity?.finish()
        ToastUtils.showToast(msg)
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
        fun newInstance() = IngredientsUpdateFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}