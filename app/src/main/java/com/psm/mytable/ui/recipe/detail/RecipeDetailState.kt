package com.psm.mytable.ui.recipe.detail

sealed class RecipeDetailState {
    object UnInitialized: RecipeDetailState()
    object Loading: RecipeDetailState()
    object Error: RecipeDetailState()
    object Complete: RecipeDetailState()
}
