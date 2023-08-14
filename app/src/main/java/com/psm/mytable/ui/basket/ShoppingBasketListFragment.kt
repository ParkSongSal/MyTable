package com.psm.mytable.ui.basket

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.psm.mytable.App
import com.psm.mytable.EventObserver
import com.psm.mytable.R
import com.psm.mytable.databinding.FragmentShoppingBasketListBinding
import com.psm.mytable.utils.ToastUtils
import com.psm.mytable.utils.getViewModelFactory
import com.psm.mytable.utils.initToolbar
import com.psm.mytable.utils.setTitleText
import com.psm.mytable.utils.showItemAddDialog
import com.psm.mytable.utils.showYesNoDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShoppingBasketListFragment: Fragment() {
    private lateinit var viewDataBinding: FragmentShoppingBasketListBinding
    private val viewModel by viewModels<ShoppingBasketListViewModel> { getViewModelFactory() }

    lateinit var mView:View
    lateinit var mAdapter : ShoppingBasketPagingAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentShoppingBasketListBinding.inflate(inflater, container, false).apply{
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mView = view
        initToolbar(view)
        setTitleText(view, R.string.shopping_basket_list_1_001)


        viewModel.init(requireContext())
        setupEvent()
        setupListAdapter()

        initView(view)
    }

    private fun initView(view: View){
        view.findViewById<ImageFilterView>(R.id.imgToolbarAdd).setOnClickListener{
            viewModel.addShoppingItem()
        }

    }

    private fun setupListAdapter(){

        mAdapter = ShoppingBasketPagingAdapter(viewModel)

        viewDataBinding.shoppingList.apply{
            layoutManager = LinearLayoutManager(App.instance)
            //adapter = ShoppingBasketAdapter(viewModel)
            adapter = mAdapter
        }

        lifecycleScope.launch{
            viewModel.shoppingBasketPagingData.collectLatest{
                (viewDataBinding.shoppingList.adapter as ShoppingBasketPagingAdapter).submitData(it)
            }
        }
    }

    private fun errorPage(msg: String){
        activity?.finish()
        ToastUtils.showToast(msg)
    }

    private fun setupEvent() {
        viewModel.showAddShoppingItemDialogEvent.observe(viewLifecycleOwner, EventObserver{
            val title = getString(R.string.shopping_dialog_1_001)
            val hint = getString(R.string.shopping_dialog_1_002)
            showItemAddDialog(title, hint, inputCallback = {
                viewModel.addShoppingItemAct(it)
            }, cancelCallback = {})
        })

        viewModel.completeShoppingItemInsertEvent.observe(viewLifecycleOwner, EventObserver{
            mAdapter.refresh()
            viewModel.getShoppingBasketList()
        })

        viewModel.completeShoppingItemDeleteEvent.observe(viewLifecycleOwner, EventObserver{
            ToastUtils.showToast("삭제되었습니다.")
            mAdapter.refresh()
            viewModel.getShoppingBasketList()
        })


        viewModel.showDeleteShoppingItemDialogEvent.observe(viewLifecycleOwner, EventObserver{itemData->
            val message = getString(R.string.shopping_dialog_1_003)
            val positiveButton = getString(R.string.confirm)
            val negativeButton = getString(R.string.cancel)
            showYesNoDialog(message, positiveButton, negativeButton, positiveCallback = {
                viewModel.deleteShoppingItemAct(itemData)
            })
        })
    }


    companion object {
        fun newInstance() = ShoppingBasketListFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}