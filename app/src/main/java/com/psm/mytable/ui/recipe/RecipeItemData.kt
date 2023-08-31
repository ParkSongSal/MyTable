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

    // 재료
    var ingredients: String,

    // 만드는방법
    var howToMake: String,

    // 등록일자
    var reg_date: String,

    // 타입(한식, 중식, 일식, 간식, 분식)
    val type: String,

    val typeId: Int,

    //var isLike: String

): Parcelable