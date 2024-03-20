package com.psm.mytable.domain.recipe

import com.psm.mytable.data.repository.MyTableRepository
import com.psm.mytable.data.room.recipe.Recipe
import com.psm.mytable.domain.UseCase

class UpdateRecipeUseCase(
    private val repository: MyTableRepository
): UseCase {

    suspend operator fun invoke(recipe: Recipe) {
        return repository.updateRecipe(recipe)
    }
}