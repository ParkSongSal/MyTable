/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.psm.mytable.utils

/**
 * Extension functions for Fragment.
 */

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.psm.mytable.App
import com.psm.mytable.R
import com.psm.mytable.ViewModelFactory
import com.psm.mytable.type.PhotoType
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.dialog.recipe.SelectGetPhotoTypeDialog
import com.psm.mytable.ui.dialog.recipe.SelectRecipeTypeDialog

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as App).repository
    return ViewModelFactory(repository, this)
}

fun Fragment.setTitleText(view: View, @StringRes stringResId: Int) {
    setTitleText(view, getString(stringResId))
}

fun setTitleText(view: View, title: String) {
    view.findViewById<TextView>(R.id.textToolbarTitle)?.text = title
}

fun Fragment.initToolbar(view: View) {
    /*view.findViewById<View>(R.id.imgToolbarClose)?.apply {
        setOnClickListener {
            activity?.finish()
        }
    }*/

    view.findViewById<View>(R.id.imgToolbarBack)?.apply {
        setOnClickListener {
            activity?.finish()
        }
    }
}

fun Fragment.showRecipeSelectDialog(
    positiveCallback: (type: RecipeType) -> Unit ){
    val requestKey = System.currentTimeMillis().toString()
    setFragmentResultListener(requestKey) {
            _, result ->
        if(DialogUtils.isPositiveClick(result)){
            val type = DialogUtils.getRecipeType(result)
            positiveCallback(type)
        }
    }
    val newFragment = SelectRecipeTypeDialog.newInstance(requestKey)
    newFragment.show(getSupportFragmentMananger(), null)
}

fun Fragment.showPhotoSelectDialog(
    positiveCallback: (type: PhotoType) -> Unit ){
    val requestKey = System.currentTimeMillis().toString()
    setFragmentResultListener(requestKey) {
            _, result ->
        if(DialogUtils.isPositiveClick(result)){
            val type = DialogUtils.getPhotoType(result)
            positiveCallback(type)
        }
    }
    val newFragment = SelectGetPhotoTypeDialog.newInstance(requestKey)
    newFragment.show(getSupportFragmentMananger(), null)
}


fun Fragment.setFragmentResult(
    requestKey: String,
    result: Bundle
) = parentFragmentManager.setFragmentResult(requestKey, result)

fun Fragment.setFragmentResultListener(
    requestKey: String,
    listener: ((resultKey: String, bundle: Bundle) -> Unit)
) {
    parentFragmentManager.setFragmentResultListener(requestKey, this, listener)
}

fun Fragment.getSupportFragmentManager() = (activity as FragmentActivity).supportFragmentManager

fun Fragment.hideKeyboard(){
    activity?.let {
        val focusWindow = it.currentFocus
        if(focusWindow != null){
            val inputMethodManager = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(focusWindow.windowToken, 0)
        }
    }
}

fun Fragment.getSupportFragmentMananger() = (activity as FragmentActivity).supportFragmentManager