package com.psm.mytable.data.repository

import com.psm.mytable.data.room.recipe.Recipe

/**
 * API 추상화
 */
interface MyTableRepository {

    suspend fun insertRecipe(recipe: Recipe)
    suspend fun getSearchAllRecipeList(searchWord: String) : List<Recipe>
    suspend fun getAllRecipeList() : List<Recipe>
    suspend fun getCategoryRecipeList(typeId: Int) : List<Recipe>
    suspend fun updateRecipe(recipe: Recipe)

    suspend fun deleteRecipe(recipe: Recipe)
}