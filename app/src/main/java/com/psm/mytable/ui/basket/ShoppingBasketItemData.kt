package com.psm.mytable.ui.basket

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingBasketItemData(
    // 장바구니 항목 ID
    val id: Long,

    // 장바구니 항목명
    val itemName: String,

    // 등록일자
    var reg_date: String,

): Parcelable