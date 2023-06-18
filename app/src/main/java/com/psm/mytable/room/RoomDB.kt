package com.psm.mytable.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.psm.mytable.room.recipe.Recipe
import com.psm.mytable.room.recipe.RecipeDao

@Database(entities= [Recipe::class], version = 2, exportSchema = false)
abstract class RoomDB : RoomDatabase(){
    abstract fun recipeDao(): RecipeDao?

    companion object {
        private var database: RoomDB? = null
        private const val DATABASE_NAME = "database"

        @Synchronized
        fun getInstance(context: Context): RoomDB? {
            if (database == null){
                database = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return database
        }
    }
}