package com.psm.mytable.domain.recipe

import com.psm.mytable.data.repository.MyTableRepository
import com.psm.mytable.data.room.recipe.Recipe
import com.psm.mytable.domain.UseCase
import com.psm.mytable.ui.recipe.RecipeItemData

class GetSearchRecipeUseCase(
    private val repository: MyTableRepository
): UseCase {
    suspend operator fun invoke(searchWord: String): List<RecipeItemData> {
        val recipeItemData = repository.getSearchAllRecipeList(searchWord).map {recipe ->
            RecipeItemData(
                id = recipe.id.toLong(),
                recipeImage = recipe.recipeImagePath,
                recipeName = recipe.recipeName,
                ingredients = recipe.ingredients,
                howToMake = recipe.howToMake,
                reg_date = recipe.reg_date,
                type = recipe.recipeType,
                typeId = recipe.recipeTypeId
            )
        }
        return recipeItemData
    }
}