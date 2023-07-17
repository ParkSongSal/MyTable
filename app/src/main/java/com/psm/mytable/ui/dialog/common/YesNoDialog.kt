package com.psm.mytable.ui.dialog.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.psm.mytable.R
import com.psm.mytable.utils.DialogUtils

class YesNoDialog : DialogFragment() {
    private var message: String? = null
    private var positiveButtonText: String? = null
    private var negativeButtonText: String? = null
    private var requestKey: String? = null

    companion object {
        private const val MESSAGE = "message"
        private const val POSITIVE_BUTTON_TEXT = "positiveButtonText"
        private const val NEGATIVE_BUTTON_TEXT = "negativeButtonText"
        private const val REQUEST_KEY = "requestKey"

        fun newInstance(message: String,
                        positiveButtonText: String? = null,
                        negativeButtonText: String? = null,
                        requestKey: String? = null): DialogFragment {
            val fragment = YesNoDialog()
            val args = Bundle()
            args.putString(MESSAGE, message)

            positiveButtonText?.let {
                args.putString(POSITIVE_BUTTON_TEXT, positiveButtonText)
            }

            negativeButtonText?.let {
                args.putString(NEGATIVE_BUTTON_TEXT, negativeButtonText)
            }

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
        message = arguments?.getString(MESSAGE)
        positiveButtonText = arguments?.getString(POSITIVE_BUTTON_TEXT)
        negativeButtonText = arguments?.getString(NEGATIVE_BUTTON_TEXT)
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
        return inflater.inflate(R.layout.dialog_yes_no, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setCanceledOnTouchOutside(true)

        view.findViewById<TextView>(R.id.textMessage).text = message
        view.findViewById<TextView>(R.id.textPositive).apply {
            positiveButtonText?.let {
                text = positiveButtonText
            }

            setOnClickListener {
                requestKey?.let {
                    setFragmentResult(it, DialogUtils.clickPositive())
                }
                dismiss()
            }
        }

        view.findViewById<TextView>(R.id.textNegative).apply {
            negativeButtonText?.let {
                text = negativeButtonText
            }

            setOnClickListener {
                requestKey?.let {
                    setFragmentResult(it, DialogUtils.clickNegative())
                }
                dismiss()
            }
        }

    }
}