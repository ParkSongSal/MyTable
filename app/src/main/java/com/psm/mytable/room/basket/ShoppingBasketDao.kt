package com.psm.mytable.room.basket

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.psm.mytable.ui.basket.ShoppingBasketItemData

@Dao
interface ShoppingBasketDao {

    @Insert(onConflict = REPLACE)
    fun insert(shoppingBasket: ShoppingBasket)

    @Delete
    fun delete(shoppingBasket: ShoppingBasket)

    @Query("DELETE FROM table_shopping_basket")
    fun basketAllDelete()

    @Update
    fun updateShoppingBasketItem(shoppingBasket: ShoppingBasket) : Int

    @Query("SELECT * FROM table_shopping_basket order by id")
    fun getAllShoppingBasket(): List<ShoppingBasket>

    @Query("SELECT * FROM table_shopping_basket order by id desc")
    fun getAllRecipePaging(): PagingSource<Int, ShoppingBasket>

    @Query("SELECT * FROM table_shopping_basket ORDER BY id ASC LIMIT 10 OFFSET (:page-1)*10")
    fun getShoppingBasketPagingList(page:Int): List<ShoppingBasketItemData>

    @Query("SELECT count(*) FROM table_shopping_basket")
    fun getShoppingBasketListCount(): Int
}