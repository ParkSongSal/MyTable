package com.psm.mytable.type

import androidx.annotation.StringRes
import com.psm.mytable.R

/**

 KR : 한식
 JP : 일식
 CN : 중식
 SNACKBAR : 분식
 SN : 간식
 BA : 제과/제빵
 ETC : 기타
 *
 */
enum class RecipeType(val categoryName: String, @StringRes val stringResId: Int, @StringRes val typeId: Int, var selected: Boolean = false) {
    ALL("전체", R.string.recipe_type_1_000, 0),
    KR("한식", R.string.recipe_type_1_001, 1),
    JP("일식", R.string.recipe_type_1_002, 2),
    CN("중식", R.string.recipe_type_1_003, 3),
    SNACKBAR("분식", R.string.recipe_type_1_004, 4),
    BA("제과/제빵", R.string.recipe_type_1_006, 6),
    WESTERN("양식", R.string.recipe_type_1_007, 7)

}