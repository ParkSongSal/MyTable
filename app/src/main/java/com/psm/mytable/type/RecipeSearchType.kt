package com.psm.mytable.type

import androidx.annotation.StringRes
import com.psm.mytable.R

/**
 * NAVER : 네이버
 * MANGAE : 만개의레시피
 * YOUTUBE : 유튜브
 *
 */
enum class RecipeSearchType(@StringRes val stringResId: Int, @StringRes typeId: Int) {
    NAVER(R.string.etc_1_003, 1),
    MANGAE(R.string.etc_1_004, 2),
    YOUTUBE(R.string.etc_1_005, 3),

}