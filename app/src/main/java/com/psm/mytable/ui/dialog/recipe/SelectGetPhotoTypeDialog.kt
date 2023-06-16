package com.psm.mytable.ui.dialog.recipe

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.DialogSelectGetPhotoTypeBinding
import com.psm.mytable.databinding.DialogSelectRecipeTypeBinding
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.dialog.TouchCancelDialogFragment
import com.psm.mytable.utils.DialogUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.setFragmentResult

class SelectGetPhotoTypeDialog: TouchCancelDialogFragment() {

    private lateinit var viewDataBinding: DialogSelectGetPhotoTypeBinding

    private val viewModel by viewModels<SelectRecipeTypeViewModel> { getViewModelFactory() }

    private var requestKey: String? = null
    private var hospitalName: String? = ""
    private var latitude: String? = ""
    private var longitude: String? = ""

    companion object {
        private const val REQUEST_KEY = "requestKey"
        private const val HOSPITAL_NAME = "hospitalName"
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"
        fun newInstance(requestKey: String? = null): DialogFragment {
            val fragment = SelectGetPhotoTypeDialog()
            val args = Bundle()

            requestKey?.let {
                args.putString(REQUEST_KEY, requestKey)
            }

            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 스타일 적용
        setStyle(STYLE_NORMAL, R.style.CustomFullDialog)


        arguments?.let {
            requestKey = it.getString(REQUEST_KEY)
            hospitalName = it.getString(HOSPITAL_NAME)
            latitude = it.getString(LATITUDE)
            longitude = it.getString(LONGITUDE)
        }
        isCancelable = false

    }
    override fun onStart() {
        super.onStart()
        // 레이아웃 크기 및 위치 조정
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DialogSelectGetPhotoTypeBinding.inflate(inflater, container, false)
        viewDataBinding.viewModel = viewModel
        return viewDataBinding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setCanceledOnTouchOutside(true)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setupEvent()
        viewModel.init()

    }


    fun setupEvent(){

        viewModel.closeEvent.observe(viewLifecycleOwner, EventObserver{
            requestKey?.let{ requestKey ->
                setFragmentResult(requestKey, DialogUtils.clickNegative())
                dismiss()
            }
        })

        viewModel.showPhotoEvent.observe(viewLifecycleOwner, EventObserver{type->
            requestKey?.let{requestKey ->
                setFragmentResult(requestKey, DialogUtils.clickPhotoType(type))
                dismiss()
            }
        })
    }
}