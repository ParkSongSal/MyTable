package com.psm.mytable.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.psm.mytable.App
import com.psm.mytable.data.room.recipe.Recipe
import com.psm.mytable.ui.basket.ShoppingBasketItemData
import com.psm.mytable.ui.basket.ShoppingBasketPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * API 구현
 */
class AppRepository : MyTableRepository{

    private val recipeDao = App.database?.recipeDao()
    private val basketDao = App.database?.shoppingBasketDao()
    private val ingredientDao = App.database?.ingredientDao()

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

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDao?.insert(recipe)
    }
    override suspend fun getSearchAllRecipeList(searchWord: String): List<Recipe> {
        return recipeDao?.getSearchAllRecipeList(searchWord) ?: emptyList()
    }
    override suspend fun getAllRecipeList(): List<Recipe> {
        return recipeDao?.getAllRecipe() ?: emptyList()
    }
    override suspend fun getCategoryRecipeList(typeId : Int): List<Recipe> {
        return recipeDao?.getCategoryRecipe(typeId) ?: emptyList()
    }
    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDao?.updateRecipe(recipe)
    }
    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao?.delete(recipe)
    }
}