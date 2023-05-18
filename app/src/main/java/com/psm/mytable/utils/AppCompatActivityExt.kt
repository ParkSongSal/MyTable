package com.psm.mytable.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.psm.mytable.App
import com.psm.mytable.ViewModelFactory


/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}


/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun AppCompatActivity.getViewModelFactory(): ViewModelFactory {
    val repository = (this.applicationContext as App).repository
    return ViewModelFactory(repository, this)
}




//fun AppCompatActivity.setTitleText(@StringRes stringResId: Int) {
//    findViewById<TextView>(R.id.textToolbarTitle)?.text = getString(stringResId)
//}

//fun AppCompatActivity.initToolbar() {
//    findViewById<View>(R.id.imgToolbarClose)?.apply {
//        setOnClickListener {
//            finish()
//        }
//    }
//
//    findViewById<View>(R.id.imgToolbarBack)?.apply {
//        setOnClickListener {
//            finish()
//        }
//    }
//
//}

fun AppCompatActivity.setFragmentResultListener(
    requestKey: String,
    listener: ((resultKey: String, bundle: Bundle) -> Unit)
) {
    this.setFragmentResultListener(requestKey, listener)
}
