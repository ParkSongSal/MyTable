package com.psm.mytable.ui.dialog.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.psm.mytable.R
import com.psm.mytable.utils.DialogUtils

class TempSaveDialog : DialogFragment() {
    private var requestKey: String? = null

    companion object {
        private const val REQUEST_KEY = "requestKey"

        fun newInstance(requestKey: String? = null): DialogFragment {
            val fragment = TempSaveDialog()
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
        requestKey = arguments?.getString(REQUEST_KEY)
        isCancelable = false

    }
    override fun onStart() {
        super.onStart()
        // 레이아웃 크기 및 위치 조정
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_temp_save, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setCanceledOnTouchOutside(true)

        view.findViewById<TextView>(R.id.textPositive).apply {
            setOnClickListener {
                requestKey?.let {
                    setFragmentResult(it, DialogUtils.clickPositive())
                }
                dismiss()
            }
        }

        view.findViewById<TextView>(R.id.textNegative).apply {
            setOnClickListener {
                requestKey?.let {
                    setFragmentResult(it, DialogUtils.clickNegative())
                }
                dismiss()
            }
        }

    }
}