package com.psm.mytable.data.room.recipe

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.psm.mytable.type.RecipeType


@Entity(tableName = "table_recipe")
class Recipe(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    val id: Int,

    // 레시피명
    var recipeName: String = "",

    // 레시피 종류
    var recipeType: String = "",

    @ColumnInfo(defaultValue = "0")
    var recipeTypeId: Int,

    // 재료
    var ingredients: String = "",

    // 만드는방법
    var howToMake: String = "",

    // 등록일자
    var reg_date: String = "",

    // 레시피 이미지 경로
    var recipeImagePath: String = "",

    // 좋아요 표시
    //var isLike: String
)