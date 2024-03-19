package com.psm.mytable.data.room.ingredient

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "table_ingredient")
class Ingredient(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    val id: Long,

    // 재료명
    var ingredientName: String = "",

    // 재료 개수
    var ingredientCount: String = "",

    // 보관방법 (냉장, 냉동, 실온)
    var storage: String = "",

    // 보관방법 (1, 2, 3)
    @ColumnInfo(defaultValue = "0")
    var storageType: Int,

    // 유통(소비)기한
    var expiryDate: String = "",

    var memo: String? = "",

    // 등록일자
    var regDate: String = "",
)