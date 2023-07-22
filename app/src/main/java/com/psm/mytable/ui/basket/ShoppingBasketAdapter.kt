package com.psm.mytable.ui.basket

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.psm.mytable.databinding.ItemShoppingBasketBinding

class ShoppingBasketAdapter(private val viewModel: ShoppingBasketListViewModel) : ListAdapter<ShoppingBasketItemData, ShoppingBasketAdapter.ViewHolder>(
    ServiceInformationDataDiffCallback()
) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val item = getItem(position)
        holder.bind(viewModel, item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemShoppingBasketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: ShoppingBasketListViewModel, itemData: ShoppingBasketItemData) {

            if(itemData.id.toInt() % 2 == 0){
                binding.pointView.setBackgroundColor(Color.parseColor("#A1B4D1"))
            }else{
                binding.pointView.setBackgroundColor(Color.parseColor("#CDDC39"))
            }

            binding.viewmodel = viewModel
            binding.itemData = itemData
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemShoppingBasketBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    /**
     * Callback for calculating the diff between two non-null items in a list.
     *
     * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
     * list that's been passed to `submitList`.
     */
    class ServiceInformationDataDiffCallback : DiffUtil.ItemCallback<ShoppingBasketItemData>() {
        override fun areItemsTheSame(oldItem: ShoppingBasketItemData, newItem: ShoppingBasketItemData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingBasketItemData, newItem: ShoppingBasketItemData): Boolean {
            return oldItem == newItem
        }
    }
}