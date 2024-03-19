package com.psm.mytable.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.psm.mytable.data.room.basket.ShoppingBasket
import com.psm.mytable.data.room.basket.ShoppingBasketDao
import com.psm.mytable.data.room.ingredient.Ingredient
import com.psm.mytable.data.room.ingredient.IngredientDao
import com.psm.mytable.data.room.recipe.Recipe
import com.psm.mytable.data.room.recipe.RecipeDao

@Database(entities= [Recipe::class, ShoppingBasket::class, Ingredient::class], version = 15, exportSchema = false)
abstract class RoomDB : RoomDatabase(){
    abstract fun recipeDao(): RecipeDao?
    abstract fun shoppingBasketDao(): ShoppingBasketDao?

    abstract fun ingredientDao(): IngredientDao?

    companion object {

        @Volatile
        var database: RoomDB? = null
        private const val DATABASE_NAME = "database"

        @Synchronized
        fun getInstance(context: Context): RoomDB? {
            if (database == null){
                database = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_9_11)
                    .addMigrations(MIGRATION_10_11)
                    .addMigrations(MIGRATION_11_12)
                    .addMigrations(MIGRATION_12_13)
                    .addMigrations(MIGRATION_13_14)
                    .addMigrations(MIGRATION_14_15)
                    //.addMigrations(MIGRATION_13_14)
                    .allowMainThreadQueries()
                    //.fallbackToDestructiveMigration()
                    .build()
            }
            return database
        }
        private val MIGRATION_9_11 = object : Migration(9, 11){
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        private val MIGRATION_10_11 = object : Migration(10, 11){
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        private val MIGRATION_11_12 = object : Migration(11, 12){
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        private val MIGRATION_12_13 = object : Migration(12, 13){
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }

        private val MIGRATION_13_14 = object : Migration(13, 14){
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        private val MIGRATION_14_15 = object : Migration(14, 15){
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        private val MIGRATION_13_14_2 = object : Migration(13, 14){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `table_recipe_v2`(`id` INTEGER NOT NULL DEFAULT 0 , `recipeName` TEXT NOT NULL , `recipeType` TEXT NOT NULL, `recipeTypeId` INTEGER NOT NULL DEFAULT 0 , `ingredients` TEXT NOT NULL," +
                        "`howToMake` TEXT NOT NULL, `reg_date` TEXT NOT NULL, `recipeImagePath` TEXT NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("INSERT INTO table_recipe_v2 (id, recipeName, recipeType, recipeTypeId, ingredients, howToMake, reg_date, recipeImagePath) SELECT id, recipeName, recipeType, recipeTypeId, ingredients, howToMake, reg_date, recipeImagePath FROM table_recipe")
                database.execSQL("DROP TABLE `table_recipe`")
                database.execSQL("ALTER TABLE table_recipe_v2 RENAME TO table_recipe")

                database.execSQL("CREATE TABLE `table_shopping_basket_v2`(`id` INTEGER NOT NULL DEFAULT 0, `itemName` TEXT NOT NULL, `reg_date` TEXT NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("INSERT INTO table_shopping_basket_v2 (id, itemName, reg_date) SELECT id, itemName, reg_date FROM table_shopping_basket")
                database.execSQL("DROP TABLE `table_shopping_basket`")
                database.execSQL("ALTER TABLE table_shopping_basket_v2 RENAME TO table_shopping_basket")

                //database.execSQL("CREATE TABLE `table_ingredient`(`id` INTEGER NOT NULL DEFAULT 0 , `ingredientName` TEXT NOT NULL , `ingredientCount` TEXT NOT NULL, `storage` TEXT NOT NULL , `storageType` INTEGER NOT NULL DEFAULT 0," +
                //        "`expiryDate` TEXT NOT NULL, `memo` TEXT , `regDate` TEXT NOT NULL, PRIMARY KEY(`id`))")

                database.execSQL("CREATE TABLE `table_ingredient_v2`(`id` INTEGER NOT NULL DEFAULT 0 , `ingredientName` TEXT NOT NULL , `ingredientCount` TEXT NOT NULL, `storage` TEXT NOT NULL , `storageType` INTEGER NOT NULL DEFAULT 0," +
                        "`expiryDate` TEXT NOT NULL, `memo` TEXT , `regDate` TEXT NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("INSERT INTO table_ingredient_v2 (id, ingredientName,ingredientCount, storage, storageType, expiryDate, memo, regDate)" +
                        " SELECT id, ingredientName, ingredientCount, storage, storageType, expiryDate, memo, regDate FROM table_ingredient")
                database.execSQL("DROP TABLE `table_ingredient`")
                database.execSQL("ALTER TABLE table_ingredient_v2 RENAME TO table_ingredient")

                /*database.execSQL("CREATE TABLE `table_test`(`id` INTEGER NOT NULL DEFAULT 0, `itemName` TEXT NOT NULL, `reg_date` TEXT NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("CREATE TABLE `table_test_v2`(`id` INTEGER NOT NULL DEFAULT 0, `itemName` TEXT NOT NULL, `reg_date` TEXT NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("INSERT INTO table_test_v2 (id, itemName, reg_date) SELECT id, itemName, reg_date FROM table_test_v2")
                database.execSQL("DROP TABLE `table_test`")
                database.execSQL("ALTER TABLE table_test_v2 RENAME TO table_test")*/
            }
        }
    }


}