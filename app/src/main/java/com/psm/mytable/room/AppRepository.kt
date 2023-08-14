package com.psm.mytable.room

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.psm.mytable.room.recipe.Recipe
import com.psm.mytable.room.recipe.RecipeDao
import com.psm.mytable.ui.basket.ShoppingBasketItemData
import com.psm.mytable.ui.basket.ShoppingBasketPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * API 구현
 */
class AppRepository(private val dao: RecipeDao){

    val recipeDao : PagingSource<Int, Recipe> = dao.getAllRecipePaging()

    /**
     * Pager를 사용하여 PagingData를 반환
     * - PagingConfig로 페이지 동작을 결정(페이지 크기)
     * */
    fun getShoppingBasketPagingSource() : Flow<PagingData<ShoppingBasketItemData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {ShoppingBasketPagingSource()}
        ).flow
    }

}