package com.psm.mytable.ui.recipe.detail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeDetailItemData(
    // 레시피 ID
    val id: Long,

    // 레시피 Image
    val recipeImage: String,

    // 레시피명
    val recipeName: String,

    // 타입(한식, 중식, 일식, 간식, 분식)
    val type: String

): Parcelable