package com.psm.mytable.room.recipe

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {

    @Insert(onConflict = REPLACE)
    fun insert(recipe: Recipe)

    @Delete
    fun delete(recipe: Recipe)

    @Delete
    fun allDelete(recipe : List<Recipe>)

    /*@Query("UPDATE table_recipe SET recipeName = :recipe.a WHERE ID = :sID")
    fun update(sID: Int, sContent: String, recipe: Recipe) : Int*/

    @Update
    fun updateRecipe(recipe: Recipe) : Int

    @Query("SELECT * FROM table_recipe order by id")
    fun getAllRecipe(): List<Recipe>
}