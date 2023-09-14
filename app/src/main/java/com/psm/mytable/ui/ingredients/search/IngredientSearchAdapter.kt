package com.psm.mytable.ui.ingredients.search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.psm.mytable.databinding.ItemSearchIngredientsBinding
import com.psm.mytable.ui.ingredients.IngredientsItemData
import com.psm.mytable.ui.ingredients.IngredientsViewModel

class IngredientSearchAdapter(private val viewModel: IngredientsViewModel) : ListAdapter<IngredientsItemData, IngredientSearchAdapter.ViewHolder>(
    ServiceInformationDataDiffCallback()
) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val item = getItem(position)
        holder.bind(viewModel, item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemSearchIngredientsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: IngredientsViewModel, itemData: IngredientsItemData) {


            if(itemData.remainDay.toInt() >= -5){
                // 유통(소비)기한 5일 이내 남음
                binding.remainText.setTextColor(Color.parseColor("#FFA391"))
            }else{
                // 유통(소비)기한 5일 이상 남음
                binding.remainText.setTextColor(Color.parseColor("#7293AD"))
            }

            binding.viewmodel = viewModel
            binding.itemData = itemData
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSearchIngredientsBinding.inflate(layoutInflater, parent, false)

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
    class ServiceInformationDataDiffCallback : DiffUtil.ItemCallback<IngredientsItemData>() {
        override fun areItemsTheSame(oldItem: IngredientsItemData, newItem: IngredientsItemData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IngredientsItemData, newItem: IngredientsItemData): Boolean {
            return oldItem == newItem
        }
    }
}