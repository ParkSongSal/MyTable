package com.psm.mytable.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment

open class TouchCancelDialogFragment: DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setCanceledOnTouchOutside(true)
        super.onViewCreated(view, savedInstanceState)
    }
}