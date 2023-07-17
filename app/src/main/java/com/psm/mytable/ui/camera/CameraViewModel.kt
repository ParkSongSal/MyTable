package com.psm.mytable.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.Event
import com.psm.mytable.room.AppRepository
import com.psm.mytable.room.MyTableRepository

class CameraViewModel(
    private val repository: AppRepository
) : ViewModel(){

    private var _completeTakeImageEvent = MutableLiveData<Event<String>>()
    val completeTakeImageEvent: LiveData<Event<String>>
        get() = _completeTakeImageEvent

    fun init(uriList: List<String>, faceMode: Boolean) {

    }

    fun completeImage(uri: String){
        _completeTakeImageEvent.value = Event(uri)
    }

}
