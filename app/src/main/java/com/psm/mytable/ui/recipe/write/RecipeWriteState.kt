package com.psm.mytable.ui.recipe.write

sealed class RecipeWriteState {
    object UnInitialized: RecipeWriteState()
    object Loading: RecipeWriteState()
    object Error: RecipeWriteState()
    object Complete: RecipeWriteState()
    object Failed: RecipeWriteState()
    object Cancel: RecipeWriteState()
}
