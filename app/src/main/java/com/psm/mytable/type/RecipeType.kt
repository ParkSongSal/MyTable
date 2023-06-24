package com.psm.mytable.type

import androidx.annotation.StringRes
import com.psm.mytable.R

/**

 KR : 한식
 JP : 일식
 CN : 중식
 SB : 분식
 SN : 간식
 BA : 제과/제빵
 ETC : 기타
 *
 */
enum class RecipeType(val recipeName: String, @StringRes val stringResId: Int, @StringRes val typeId: Int) {
    KR("한식", R.string.recipe_type_1_001, 1),
    JP("일식", R.string.recipe_type_1_002, 2),
    CN("중식", R.string.recipe_type_1_003, 3),
    BA("제과/제빵", R.string.recipe_type_1_006, 6)

}