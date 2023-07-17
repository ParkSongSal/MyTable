package com.psm.mytable.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.psm.mytable.databinding.ItemRecipeBinding
import com.psm.mytable.ui.main.MainViewModel

class RecipeSearchAdapter(private val viewModel: MainViewModel) : ListAdapter<RecipeItemData, RecipeSearchAdapter.ViewHolder>(
    ServiceInformationDataDiffCallback()
) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val item = getItem(position)
        holder.bind(viewModel, item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: MainViewModel, itemData: RecipeItemData) {
            binding.viewmodel = viewModel
            binding.itemData = itemData

            binding.recipeNameText.isSelected = true
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRecipeBinding.inflate(layoutInflater, parent, false)

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
    class ServiceInformationDataDiffCallback : DiffUtil.ItemCallback<RecipeItemData>() {
        override fun areItemsTheSame(oldItem: RecipeItemData, newItem: RecipeItemData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecipeItemData, newItem: RecipeItemData): Boolean {
            return oldItem == newItem
        }
    }
}