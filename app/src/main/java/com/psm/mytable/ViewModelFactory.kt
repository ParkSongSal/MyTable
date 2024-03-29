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
import com.psm.mytable.data.repository.AppRepository
import com.psm.mytable.domain.UseCaseInjection
import com.psm.mytable.ui.main.MainViewModel
import com.psm.mytable.ui.basket.ShoppingBasketListViewModel
import com.psm.mytable.ui.camera.CameraViewModel
import com.psm.mytable.ui.dialog.recipe.SelectRecipeTypeViewModel
import com.psm.mytable.ui.ingredients.IngredientsViewModel
import com.psm.mytable.ui.ingredients.cold.ColdStorageViewModel
import com.psm.mytable.ui.ingredients.frozen.FrozenStorageViewModel
import com.psm.mytable.ui.ingredients.ingredientUpdate.IngredientsUpdateViewModel
import com.psm.mytable.ui.ingredients.ingredientsAdd.IngredientsAddViewModel
import com.psm.mytable.ui.ingredients.roomTemperature.RoomTemperatureStorageViewModel
import com.psm.mytable.ui.ingredients.search.IngredientsSearchViewModel
import com.psm.mytable.ui.intro.IntroViewModel
import com.psm.mytable.ui.recipe.detail.RecipeDetailViewModel
import com.psm.mytable.ui.recipe.image.RecipeImageDetailViewModel
import com.psm.mytable.ui.recipe.update.RecipeUpdateViewModel
import com.psm.mytable.ui.recipe.write.RecipeWriteViewModel
import com.psm.mytable.ui.setting.SettingViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val appRepository: AppRepository,
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
                MainViewModel(
                    UseCaseInjection.provideGetAllRecipeUseCase(),
                    UseCaseInjection.provideGetCategoryRecipeUseCase(),
                    UseCaseInjection.provideGetSearchRecipeUseCase()
                )
            isAssignableFrom(RecipeWriteViewModel::class.java) ->
                RecipeWriteViewModel(UseCaseInjection.provideInsertRecipeUseCase())
            isAssignableFrom(SelectRecipeTypeViewModel::class.java) ->
                SelectRecipeTypeViewModel(appRepository)
            isAssignableFrom(CameraViewModel::class.java) ->
                CameraViewModel(appRepository)
            isAssignableFrom(RecipeUpdateViewModel::class.java) ->
                RecipeUpdateViewModel(UseCaseInjection.provideUpdateRecipeUseCase())
            isAssignableFrom(RecipeDetailViewModel::class.java) ->
                RecipeDetailViewModel(UseCaseInjection.provideDeleteRecipeUseCase())
            isAssignableFrom(IntroViewModel::class.java) ->
                IntroViewModel(appRepository)
            isAssignableFrom(ShoppingBasketListViewModel::class.java) ->
                ShoppingBasketListViewModel(appRepository)
            isAssignableFrom(RecipeImageDetailViewModel::class.java) ->
                RecipeImageDetailViewModel()
            isAssignableFrom(SettingViewModel::class.java) ->
                SettingViewModel(appRepository)
            isAssignableFrom(IngredientsViewModel::class.java) ->
                IngredientsViewModel(appRepository)
            isAssignableFrom(ColdStorageViewModel::class.java) ->
                ColdStorageViewModel(appRepository)
            isAssignableFrom(FrozenStorageViewModel::class.java) ->
                FrozenStorageViewModel(appRepository)
            isAssignableFrom(RoomTemperatureStorageViewModel::class.java) ->
                RoomTemperatureStorageViewModel(appRepository)
            isAssignableFrom(IngredientsAddViewModel::class.java) ->
                IngredientsAddViewModel(appRepository)
            isAssignableFrom(IngredientsUpdateViewModel::class.java) ->
                IngredientsUpdateViewModel(appRepository)
            isAssignableFrom(IngredientsSearchViewModel::class.java) ->
                IngredientsSearchViewModel(appRepository)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
