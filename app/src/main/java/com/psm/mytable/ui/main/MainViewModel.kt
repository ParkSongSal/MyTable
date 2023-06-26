package com.psm.mytable.ui.main

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psm.mytable.App
import com.psm.mytable.Event
import com.psm.mytable.room.MyTableRepository
import com.psm.mytable.room.RoomDB
import com.psm.mytable.room.recipe.Recipe
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.recipe.RecipeItemData
import timber.log.Timber

/**
 * 인트로 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - 클라이언트 ID 정보가 없는 경우 받오온다.
 * - FCM 토큰 등록이 안되 있으면 등록 한다.
 * - 로그인 사용자, 비 로그인 사용자에 해당되는 앱 초기화 완료 이벤트를 전달한다.
 * - 버전 체크 (강제 또는 선택 업데이트 알럿 노출)
 */
class MainViewModel(
    private val repository: MyTableRepository
) : ViewModel(){


    private val _items = MutableLiveData<List<RecipeItemData>>()
    val items: LiveData<List<RecipeItemData>>
        get() = _items

    private val _recipeListVisibility = MutableLiveData(View.INVISIBLE)
    val recipeListVisibility: LiveData<Int>
        get() = _recipeListVisibility



    private val _emptyRecipeListVisibility = MutableLiveData(View.INVISIBLE)
    val emptyRecipeListVisibility: LiveData<Int>
        get() = _emptyRecipeListVisibility

    private val _openRecipeDetailEvent = MutableLiveData<Event<RecipeItemData>>()
    val openRecipeDetailEvent: LiveData<Event<RecipeItemData>>
        get() = _openRecipeDetailEvent

    private val _goRecipeWriteEvent = MutableLiveData<Event<Unit>>()
    val goRecipeWriteEvent: LiveData<Event<Unit>>
        get() = _goRecipeWriteEvent

    private var database: RoomDB? = null

    fun init(context: Context){
        database = RoomDB.getInstance(context)
        getRecipeList()
    }

    fun getRecipeList(){
        val mRecipeList = database?.recipeDao()?.getAllRecipe() ?: listOf()
        if(mRecipeList.isNotEmpty()){
            _items.value = mRecipeList.map{recipe ->
                RecipeItemData(
                    id = recipe.id.toLong(),
                    recipeImage = recipe.recipeImagePath,
                    recipeName = recipe.recipeName,
                    ingredients = recipe.ingredients,
                    howToMake = recipe.howToMake,
                    reg_date = recipe.reg_date,
                    type = recipe.recipeType
                )
            }
            _recipeListVisibility.value = View.VISIBLE
            _emptyRecipeListVisibility.value = View.GONE
        }else{
            _recipeListVisibility.value = View.GONE
            _emptyRecipeListVisibility.value = View.VISIBLE
        }

    }

    fun clickAddRecipe(){
        _goRecipeWriteEvent.value = Event(Unit)
    }

    fun clickRecipeDetail(item: RecipeItemData){
        _openRecipeDetailEvent.value = Event(item)
    }
}