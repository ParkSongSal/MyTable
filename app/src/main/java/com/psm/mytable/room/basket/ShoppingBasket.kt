package com.psm.mytable.room.basket

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "table_shopping_basket")
class ShoppingBasket(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    // 장바구니 항목명
    var itemName: String,

    // 등록일자
    var reg_date: String
)