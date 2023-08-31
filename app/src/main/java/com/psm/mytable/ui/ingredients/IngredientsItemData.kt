package com.psm.mytable.ui.ingredients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientsItemData(
    // 항목 ID
    val id: Long,

    // 재료명
    val itemName: String,

    // 재료 개수
    var itemCount: String,

    // 보관방법
    val storage: String,

    val storageType: Int,

    // 등록일자
    val regDate: String,

    // 유통기한/소비기한
    val expiryDate:  String,

    var remainDay: String,

    // 비고 메모
    val memo: String,

    ): Parcelable