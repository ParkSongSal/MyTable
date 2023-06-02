package com.psm.mytable.ui.recipe

import android.os.Parcelable
import com.psm.mytable.type.RecipeType
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeItemData(
    // 레시피 ID
    val id: Long,

    // 레시피 Image
    val recipeImage: String,

    // 레시피명
    val recipeName: String,

    // 타입(한식, 중식, 일식, 간식, 분식)
    val type: RecipeType

): Parcelable