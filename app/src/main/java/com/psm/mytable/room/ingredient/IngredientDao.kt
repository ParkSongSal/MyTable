package com.psm.mytable.room.ingredient

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface IngredientDao {

    @Insert(onConflict = REPLACE)
    fun insert(ingredient: Ingredient)

    @Delete
    fun delete(ingredient: Ingredient)

    @Query("DELETE FROM table_ingredient")
    fun ingredientAllDelete()

    @Update
    fun updateIngredientItem(ingredient: Ingredient) : Int

    /**
     * getAllIngredient()
     * 냉장, 냉동, 실온 재료 모두 조회
     */
    @Query("SELECT * FROM table_ingredient order by id")
    fun getAllIngredient(): List<Ingredient>

    /**
     * getColdStorage()
     * 냉장 재료만 조회
     */
    @Query("SELECT * FROM table_ingredient WHERE storageType = 1 order by id")
    fun getColdStorage(): List<Ingredient>

    /**
     * getFrozenStorage()
     * 냉동 재료만 조회
     */
    @Query("SELECT * FROM table_ingredient WHERE storageType = 2 order by id")
    fun getFrozenStorage(): List<Ingredient>

    /**
     * getRoomTemperatureStorage()
     * 실온 재료만 조회
     */
    @Query("SELECT * FROM table_ingredient WHERE storageType = 3 order by id")
    fun getRoomTemperatureStorage(): List<Ingredient>

    @Query("SELECT * FROM table_ingredient order by id desc")
    fun getAllIngredientPaging(): PagingSource<Int, Ingredient>

    @Query("SELECT * FROM table_ingredient ORDER BY id ASC LIMIT 10 OFFSET (:page-1)*10")
    fun getIngredientPagingList(page:Int): List<Ingredient>

    @Query("SELECT count(*) FROM table_ingredient")
    fun getIngredientListCount(): Int
}