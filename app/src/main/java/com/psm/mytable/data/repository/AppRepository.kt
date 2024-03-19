package com.psm.mytable.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.psm.mytable.data.room.RoomDB
import com.psm.mytable.ui.basket.ShoppingBasketItemData
import com.psm.mytable.ui.basket.ShoppingBasketPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * API 구현
 */
class AppRepository(private val database: RoomDB){

    /**
     * Pager를 사용하여 PagingData를 반환
     * - PagingConfig로 페이지 동작을 결정(페이지 크기)
     * */
    fun getShoppingBasketPagingSource() : Flow<PagingData<ShoppingBasketItemData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {ShoppingBasketPagingSource()}
        ).flow
    }

}