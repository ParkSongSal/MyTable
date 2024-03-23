package com.psm.mytable.domain

import com.psm.mytable.data.repository.AppRepository
import com.psm.mytable.domain.recipe.DeleteRecipeUseCase
import com.psm.mytable.domain.recipe.GetAllRecipeUseCase
import com.psm.mytable.domain.recipe.GetCategoryRecipeUseCase
import com.psm.mytable.domain.recipe.GetSearchRecipeUseCase
import com.psm.mytable.domain.recipe.InsertRecipeUseCase
import com.psm.mytable.domain.recipe.UpdateRecipeUseCase

object UseCaseInjection {

    /* ---------------------------------- Recipe ----------------------------------*/
    /**
    * 전체 레시피 목록 조회
    * */
    fun provideGetAllRecipeUseCase() = GetAllRecipeUseCase(AppRepository())

    /**
     * 카테고리별 레시피 목록 조회
     * */
    fun provideGetCategoryRecipeUseCase() = GetCategoryRecipeUseCase(AppRepository())

    /**
     * 검색어별 레시피 목록 조회
     * */
    fun provideGetSearchRecipeUseCase() = GetSearchRecipeUseCase(AppRepository())

    /**
     * 레시피 등록
     * */
    fun provideInsertRecipeUseCase() = InsertRecipeUseCase(AppRepository())

    /**
     * 레시피 수정
     * */
    fun provideUpdateRecipeUseCase() = UpdateRecipeUseCase(AppRepository())


    /**
     * 레시피 삭제
     * */
    fun provideDeleteRecipeUseCase() = DeleteRecipeUseCase(AppRepository())

    /*--------------------------------End Recipe --------------------------------*/

}