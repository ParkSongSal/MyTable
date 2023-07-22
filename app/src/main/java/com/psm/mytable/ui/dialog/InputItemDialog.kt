package com.psm.mytable.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.psm.mytable.R
import com.psm.mytable.utils.DialogUtils
import com.psm.mytable.utils.setFragmentResult

class InputItemDialog: TouchCancelDialogFragment() {
    private var requestKey: String? = null
    private var title: String = ""
    private var hint: String = ""

    companion object {
        private const val REQUEST_KEY = "requestKey"
        private const val REQUEST_TITLE = "requestTitle"
        private const val REQUEST_HINT = "requestHint"

        fun newInstance(requestKey: String? = null, title: String, hint: String): DialogFragment {
            val fragment = InputItemDialog()
            val args = Bundle()


            requestKey?.let {
                args.putString(REQUEST_KEY, requestKey)
            }
            args.putString(REQUEST_TITLE, title)
            args.putString(REQUEST_HINT, hint)

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
            title = it.getString(REQUEST_TITLE) ?: ""
            hint = it.getString(REQUEST_HINT) ?: ""
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
        return inflater.inflate(R.layout.dialog_input_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setCanceledOnTouchOutside(true)



        view.findViewById<TextView>(R.id.textTitle).text  = title
        val editText = view.findViewById<EditText>(R.id.editInput)
        editText.hint = hint


        val textPositive = view.findViewById<TextView>(R.id.textPositive)
        val textNegative = view.findViewById<TextView>(R.id.textNegative)


        textPositive.apply {
            setOnClickListener {
                requestKey?.let {
                    val text = editText.text.toString()
                    setFragmentResult(it, DialogUtils.clickInputText(text))
                }
                dismiss()
            }
        }

        view.findViewById<View>(R.id.textNegative).apply {
            setOnClickListener {
                requestKey?.let {
                    setFragmentResult(it, DialogUtils.clickNegative())
                }
                dismiss()
            }
        }
    }
}