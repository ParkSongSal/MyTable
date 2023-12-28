package com.psm.mytable.ui.intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psm.mytable.EventObserver
import com.psm.mytable.databinding.FragmentIntroBinding
import com.psm.mytable.ui.ingredients.IngredientsActivity
import com.psm.mytable.utils.getViewModelFactory

class IntroFragment: Fragment(){
    private lateinit var viewDataBinding: FragmentIntroBinding
    private val viewModel by viewModels<IntroViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentIntroBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this
        setupEvent()
        init()
    }

    private fun init(){
        lottieInit()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                viewModel.appInit()
            },1500
        )
    }

    private fun lottieInit(){
        viewDataBinding.lottie.apply{
            playAnimation()
            loop(true)
        }

    }

    private fun setupEvent() {

        viewModel.appInitCompleteEvent.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(context, IngredientsActivity::class.java)
            startActivity(intent)
            activity?.finish()
        })
    }

    companion object {
        fun newInstance() = IntroFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}