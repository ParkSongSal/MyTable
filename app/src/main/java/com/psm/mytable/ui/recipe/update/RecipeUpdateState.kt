package com.psm.mytable.ui.recipe.update

sealed class RecipeUpdateState {
    object UnInitialized: RecipeUpdateState()
    object Loading: RecipeUpdateState()
    object Error: RecipeUpdateState()
    object Complete: RecipeUpdateState()
}
