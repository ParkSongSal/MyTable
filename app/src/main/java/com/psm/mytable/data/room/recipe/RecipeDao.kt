package com.psm.mytable.data.room.recipe

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {

    // Recipe 테이블 데이터 삽입
    @Insert(onConflict = REPLACE)
    fun insert(recipe: Recipe)


    // Recipe 테이블 선택된 데이터 삭제
    @Delete
    fun delete(recipe: Recipe)

    // Recipe 테이블 데이터 모두 삭제
    @Query("DELETE FROM table_recipe")
    fun recipeAllDelete()

    /*@Query("UPDATE table_recipe SET recipeName = :recipe.a WHERE ID = :sID")
    fun update(sID: Int, sContent: String, recipe: Recipe) : Int*/

    // Recipe 테이블 데이터 수정
    @Update
    fun updateRecipe(recipe: Recipe)

    @Query("SELECT * FROM table_recipe order by id")
    fun getAllRecipe(): List<Recipe>

    @Query("SELECT * FROM table_recipe WHERE recipeTypeId = :typeId")
    fun getCategoryRecipe(typeId: Int): List<Recipe>

    @Query("SELECT * FROM table_recipe WHERE recipeName LIKE :searchWord AND recipeTypeId = :typeId")
    fun getSearchCategoryRecipeList(searchWord: String, typeId: Int): List<Recipe>

    @Query("SELECT * FROM table_recipe WHERE recipeName LIKE :searchWord")
    fun getSearchAllRecipeList(searchWord: String): List<Recipe>

    @Query("SELECT * FROM table_recipe order by id desc")
    fun getAllRecipePaging(): PagingSource<Int, Recipe>
}