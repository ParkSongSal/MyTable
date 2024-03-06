package com.psm.mytable.ui.basket

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.psm.mytable.databinding.ItemShoppingBasketBinding

class ShoppingBasketPagingAdapter() : PagingDataAdapter<ShoppingBasketItemData, ShoppingBasketPagingAdapter.ViewHolder>(
    ServiceInformationDataDiffCallback
) {

    interface CustomListenerInterface {
        fun removeListener(position: Int, itemData: ShoppingBasketItemData)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemShoppingBasketBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
        //return ViewHolder.from(parent)
    }

    private var onRemoveListener: CustomListenerInterface? = null
    fun removeListener(pOnClick: CustomListenerInterface) {
        this.onRemoveListener = pOnClick
    }
    inner class ViewHolder (val binding: ItemShoppingBasketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemData: ShoppingBasketItemData) {

            if(itemData.id.toInt() % 2 == 0){
                binding.pointView.setBackgroundColor(Color.parseColor("#A1B4D1"))
            }else{
                binding.pointView.setBackgroundColor(Color.parseColor("#CDDC39"))
            }
            binding.itemNameTextView.text = itemData.itemName
            binding.deleteLinear.setOnClickListener {
                onRemoveListener?.removeListener(bindingAdapterPosition, itemData)
            }
        }
    }

    /**
     * Callback for calculating the diff between two non-null items in a list.
     *
     * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
     * list that's been passed to `submitList`.
     */
    companion object {
        private val ServiceInformationDataDiffCallback = object: DiffUtil.ItemCallback<ShoppingBasketItemData>() {
            override fun areItemsTheSame(oldItem: ShoppingBasketItemData, newItem: ShoppingBasketItemData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShoppingBasketItemData, newItem: ShoppingBasketItemData): Boolean {
                return oldItem == newItem
            }
        }
    }
}