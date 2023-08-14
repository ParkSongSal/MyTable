package com.psm.mytable.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.psm.mytable.room.basket.ShoppingBasket
import com.psm.mytable.room.basket.ShoppingBasketDao
import com.psm.mytable.room.recipe.Recipe
import com.psm.mytable.room.recipe.RecipeDao

@Database(entities= [Recipe::class, ShoppingBasket::class], version = 9, exportSchema = false)
abstract class RoomDB : RoomDatabase(){
    abstract fun recipeDao(): RecipeDao?
    abstract fun shoppingBasketDao(): ShoppingBasketDao?

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
                )
                    .addMigrations(MIGRATION_8_9)
                    .allowMainThreadQueries()
                    //.fallbackToDestructiveMigration()
                    .build()
            }
            return database
        }
        private val MIGRATION_8_9 = object : Migration(8, 9){
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
            }
        }
    }


}