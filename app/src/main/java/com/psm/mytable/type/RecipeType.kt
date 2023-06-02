package com.psm.mytable.type

import androidx.annotation.StringRes
import com.psm.mytable.R

/**

 KR : 한식
 JP : 일식
 CN : 중식
 SB : 분식
 SN : 간식
 ETC : 기타
 *
 */
enum class RecipeType(@StringRes val stringResId: Int, @StringRes typeId: Int) {
    KR(R.string.recipe_type_1_001, 1),
    JP(R.string.recipe_type_1_002, 2),
    CN(R.string.recipe_type_1_003, 3)

}