package com.psm.mytable.room.basket

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShoppingBasketDao {

    @Insert(onConflict = REPLACE)
    fun insert(shoppingBasket: ShoppingBasket)

    @Delete
    fun delete(shoppingBasket: ShoppingBasket)

    @Delete
    fun allDelete(shoppingBasket : List<ShoppingBasket>)

    @Update
    fun updateShoppingBasketItem(shoppingBasket: ShoppingBasket) : Int

    @Query("SELECT * FROM table_shopping_basket order by id")
    fun getAllShoppingBasket(): List<ShoppingBasket>

    @Query("SELECT * FROM table_shopping_basket order by id desc")
    fun getAllRecipePaging(): PagingSource<Int, ShoppingBasket>
}