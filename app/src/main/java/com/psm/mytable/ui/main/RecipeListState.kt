package com.psm.mytable.ui.main

sealed class RecipeListState {
    object UnInitialized: RecipeListState()
    object Loading: RecipeListState()
    object Success: RecipeListState()
    object Error: RecipeListState()
    object Complete: RecipeListState()
}
