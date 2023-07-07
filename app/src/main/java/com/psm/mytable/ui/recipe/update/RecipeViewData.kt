package com.psm.mytable.ui.recipe.update

import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.psm.mytable.BR
import timber.log.Timber

class RecipeViewData: BaseObservable() {

    var recipeId: Long = -1

    // 수정 전 Image Path
    var recipeImage: String = ""

    // 수정 전 레시피명
    var originalRecipeName: String = ""

    // 수정 전 재료
    var originalIngredients: String = ""
    // 수정전 만드는법
    var originalHowToMake: String = ""

    var originalRecipeType: String = ""

    var originalRecipeTypeId: Int = 0

    @get:Bindable
    var recipeTypeId: Int = 0
        set(value){
            field = value
            notifyPropertyChanged(BR.recipeTypeId)
            checkRequiredData()
        }

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
        requiredDataComplete = (originalRecipeName != recipeName) ||
                (originalIngredients != ingredients) ||
                (originalHowToMake != howToMake) ||
                (originalRecipeTypeId != recipeTypeId) ||
                recipeImageUri != null
    }

}