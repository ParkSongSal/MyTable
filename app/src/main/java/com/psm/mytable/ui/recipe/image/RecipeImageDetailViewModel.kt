package com.psm.mytable.ui.recipe.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.ui.recipe.RecipeItemData
import com.psm.mytable.utils.extension.onMain

/**
 * 레시피 이미지 상세
 */
class RecipeImageDetailViewModel(
) : ViewModel(){


    private val _recipeImage = MutableLiveData<String>()
    val recipeImage: LiveData<String> = _recipeImage

    private val _setTitleEvent = MutableLiveData<Event<String>>()
    val setTitleEvent: LiveData<Event<String>>
        get() = _setTitleEvent

    fun getRecipeDetailData(recipeItemData: RecipeItemData){
        onMain{
            _recipeImage.postValue(recipeItemData.recipeImage)
            _setTitleEvent.postValue(Event(recipeItemData.recipeName))
        }
    }
}