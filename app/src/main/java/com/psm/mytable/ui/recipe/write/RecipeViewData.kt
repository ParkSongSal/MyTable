package com.psm.mytable.ui.recipe.write

import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.psm.mytable.BR
import timber.log.Timber

class RecipeViewData: BaseObservable() {

    @get:Bindable
    var recipeName: String = ""
        set(value){
            field = value
            notifyPropertyChanged(BR.recipeName)
            checkRequiredData()
        }

    @get:Bindable
    var ingredients: String = ""
        set(value){
            field = value
            notifyPropertyChanged(BR.ingredients)
            checkRequiredData()
        }

    @get:Bindable
    var howToMake: String = ""
        set(value){
            field = value
            notifyPropertyChanged(BR.howToMake)
            checkRequiredData()
        }

    // 레시피 이미지 Uri
    @get:Bindable
    var recipeImageUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.recipeImageUri)
            checkRequiredData()
        }

    @get:Bindable
    var requiredDataComplete: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.requiredDataComplete)
        }



    private fun checkRequiredData() {
        requiredDataComplete = recipeName.isNotEmpty() &&
                ingredients.isNotEmpty()&&
                howToMake.isNotEmpty()&&
                recipeImageUri != null


    }

}