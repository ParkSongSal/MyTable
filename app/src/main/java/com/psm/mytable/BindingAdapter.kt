package com.psm.mytable

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.ui.recipe.RecipeItemData


/**
 * [BindingAdapter]s for the [RecipeItemData]s list.
 */


/*
    # 레시피 목록
*/
@BindingAdapter("app:recipeList")
fun setRecipeListItem(listView: RecyclerView, items: List<RecipeItemData>?) {
    items?.let {
        (listView.adapter as RecipeAdapter).submitList(items)
    }
}
