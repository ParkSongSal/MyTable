/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.psm.mytable

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.psm.mytable.ui.main.MainViewModel
import com.psm.mytable.room.MyTableRepository
import com.psm.mytable.ui.camera.CameraViewModel
import com.psm.mytable.ui.dialog.recipe.SelectRecipeTypeViewModel
import com.psm.mytable.ui.recipe.detail.RecipeDetailViewModel
import com.psm.mytable.ui.recipe.write.RecipeWriteViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val myTableRepository: MyTableRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(myTableRepository)
            isAssignableFrom(RecipeWriteViewModel::class.java) ->
                RecipeWriteViewModel(myTableRepository)
            isAssignableFrom(SelectRecipeTypeViewModel::class.java) ->
                SelectRecipeTypeViewModel(myTableRepository)
            isAssignableFrom(CameraViewModel::class.java) ->
                CameraViewModel(myTableRepository)
            isAssignableFrom(RecipeDetailViewModel::class.java) ->
                RecipeDetailViewModel(myTableRepository)

            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
