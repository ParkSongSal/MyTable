package com.psm.mytable

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.psm.mytable.ui.basket.ShoppingBasketAdapter
import com.psm.mytable.ui.basket.ShoppingBasketItemData
import com.psm.mytable.ui.recipe.RecipeAdapter
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.ui.recipe.RecipeSearchAdapter


/**
 * [BindingAdapter]s for the [RecipeItemData]s list.
 */


/**
    # 레시피 목록
*/
@BindingAdapter("app:recipeList")
fun setRecipeListItem(listView: RecyclerView, items: List<RecipeItemData>?) {
    items?.let {
        (listView.adapter as RecipeAdapter).submitList(items)
    }
}


/**
* # 레시피 검색 목록
* */
@BindingAdapter("app:searchResultItems")
fun setRecipeSearchListItem(listView: RecyclerView, items: List<RecipeItemData>?) {
    items?.let {
        (listView.adapter as RecipeSearchAdapter).submitList(items)
    }
}

/**
 * # 장바구니 목록
 * */
@BindingAdapter("app:shoppingBasketList")
fun setShoppingBasketListItem(listView: RecyclerView, items: List<ShoppingBasketItemData>?) {
    items?.let {
        (listView.adapter as ShoppingBasketAdapter).submitList(items)
    }
}