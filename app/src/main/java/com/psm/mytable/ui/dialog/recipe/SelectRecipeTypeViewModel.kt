package com.psm.mytable.ui.dialog.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.room.MyTableRepository
import com.psm.mytable.type.PhotoType
import com.psm.mytable.type.RecipeType

class SelectRecipeTypeViewModel(
    private val repository: MyTableRepository
) : ViewModel(){

    private val _progressVisible = MutableLiveData(false)
    val progressVisible: LiveData<Boolean>
        get() = _progressVisible

    private var _showRecipeEvent = MutableLiveData<Event<RecipeType>>()
    val showRecipeEvent: LiveData<Event<RecipeType>>
        get() = _showRecipeEvent

    private var _showPhotoEvent = MutableLiveData<Event<PhotoType>>()
    val showPhotoEvent: LiveData<Event<PhotoType>>
        get() = _showPhotoEvent

    private var _closeEvent = MutableLiveData<Event<Unit>>()
    val closeEvent: LiveData<Event<Unit>>
        get() = _closeEvent

    fun init() {

    }

    fun clickCancel(){
        _closeEvent.value = Event(Unit)

    }

    fun clickRecipeType(recipeType : RecipeType){
        _showRecipeEvent.value = Event(recipeType)
    }

    fun clickPhotoType(photoType: PhotoType){
        _showPhotoEvent.value = Event(photoType)
    }
}