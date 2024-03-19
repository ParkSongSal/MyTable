package com.psm.mytable.data.room.basket

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "table_shopping_basket")
class ShoppingBasket(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    val id: Int,

    // 장바구니 항목명
    var itemName: String = "",

    // 등록일자
    var reg_date: String = ""
    )