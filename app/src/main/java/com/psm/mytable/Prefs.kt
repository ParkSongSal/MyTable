package com.psm.mytable

import android.content.Context.MODE_PRIVATE
import android.preference.PreferenceManager


object Prefs {
    private const val PURCHASE_MONTH = "purchaseMonth"
    private const val PURCHASE_DAY = "purchaseDay"

    private const val RECIPE_NAME = "recipeName"
    private const val INGREDIENTS = "ingredients"
    private const val HOWTOMAKE = "howToMake"


    private val prefs by lazy {
        App.instance.getSharedPreferences("common", MODE_PRIVATE)
        //PreferenceManager.getDefaultSharedPreferences(App.instance)
    }

    private fun getStringValue(key: String): String
            = prefs.getString(key, "") ?: ""

    private fun setStringValue(key: String , value: String)
            = prefs.edit()
            .putString(key, value)
            .apply()

    private fun getLongValue(key: String): Long
            = prefs.getLong(key, 0)

    private fun setLongValue(key: String , value: Long)
            = prefs.edit()
        .putLong(key, value)
        .apply()

    private fun getFloatValue(key: String): Float
            = prefs.getFloat(key, 0.0f)

    private fun setFloatValue(key: String , value: Float)
            = prefs.edit()
        .putFloat(key, value)
        .apply()


    private fun getBooleanValue(key: String): Boolean
            = prefs.getBoolean(key, false)

    private fun setBooleanValue(key: String , value: Boolean)
            = prefs.edit()
        .putBoolean(key, value)
        .apply()

    var recipeName
        get() = getStringValue(RECIPE_NAME)
        set(value) = setStringValue(RECIPE_NAME, value)

    var ingredients
        get() = getStringValue(INGREDIENTS)
        set(value) = setStringValue(INGREDIENTS, value)

    var howToMake
        get() = getStringValue(HOWTOMAKE)
        set(value) = setStringValue(HOWTOMAKE, value)

    fun clear() {
    }

    fun clearRecipe(){
        recipeName = ""
        ingredients = ""
        howToMake = ""
    }


}