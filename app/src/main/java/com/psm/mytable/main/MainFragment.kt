package com.psm.mytable.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentMainBinding
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory

class MainFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentMainBinding
    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }


    private lateinit var getDiseaseResult: ActivityResultLauncher<Intent>

    private lateinit var getCostEditResult: ActivityResultLauncher<Intent>

    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container,
            false
        )
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupEvent()
        checkPermission()
        init()
    }

    private fun init(){
        /*val adRequest = AdRequest.Builder().build()
        viewDataBinding.adView.loadAd(adRequest)*/
    }
    private fun checkPermission() {
    }



    private fun setupEvent() {

    }

    private fun goUpdate(updateLink: String){
        if(updateLink.isEmpty()) {
            goPlayStore()
        } else {
            goLink(updateLink)
        }
    }

    private fun goPlayStore(){
        val packageName = this.requireActivity().packageName
        val url = "market://details?id=${packageName}"
        goLink(url)
    }

    private fun goLink(url: String){
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()))
            startActivity(intent)
        }catch (e: Exception){
            e.printStackTrace()
            ToastUtils.showToast("update error")
        }
        requireActivity().finish()
    }


    companion object {
        fun newInstance() = MainFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}