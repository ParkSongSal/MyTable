package com.psm.mytable.type

import androidx.annotation.StringRes
import com.psm.mytable.R

/**
  PHOTO : 카메라 촬영
  ALBUM : 앨범 선택
 *
 */
enum class PhotoType(@StringRes val stringResId: Int, @StringRes typeId: Int) {
    CAMERA(R.string.etc_1_001, 1),
    ALBUM(R.string.etc_1_002, 2),

}