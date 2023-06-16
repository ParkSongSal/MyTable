package com.psm.mytable.room.recipe

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "table_recipe")
class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    // 레시피명
    var recipeName: String,

    // 레시피 종류
    var recipeType: String,

    // 재료
    var ingredients: String,

    // 만드는방법
    var howToMake: String,

    // 등록일자
    var reg_date: String,

    // 레시피 이미지 경로
    var recipeImagePath: String
)