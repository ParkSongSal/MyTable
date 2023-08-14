package com.psm.mytable.ui.basket

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.psm.mytable.room.RoomDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class ShoppingBasketPagingSource : PagingSource<Int, ShoppingBasketItemData>(){

    /**
     * 현재 목록을 대체할 새 데이터를 로드할 때 사용
     * */
    override fun getRefreshKey(state: PagingState<Int, ShoppingBasketItemData>): Int? {
        return state.anchorPosition?.let{anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


    /**
     * 스크롤 할 때 마다 데이터를 비동기적으로 가져오는 메서드
     * */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShoppingBasketItemData> {
        // 시작 페이지
        // 처음에 null 값인 것을 고려하여 시작 값 부여
        val page = params.key ?: STARTING_PAGE

        return try{
            var data: List<ShoppingBasketItemData>? = null

            // page 값에 따른 List 호출
            // join을 사용해서 list 값을 저장
            CoroutineScope(Dispatchers.IO).launch{
                data = RoomDB.database!!.shoppingBasketDao()?.getShoppingBasketPagingList(page)
                Log.d("psm","testData Size : ${data?.size}")
            }.join()

            // 반환할 데이터
            LoadResult.Page(
                data = data!!,
                prevKey = if(page == STARTING_PAGE) null else page-1,
                nextKey = if(data.isNullOrEmpty()) null else page+1
            )
        }catch(e: IOException){
            LoadResult.Error(e)
        }catch(e: Exception){
            LoadResult.Error(e)
        }
    }
}
private const val STARTING_PAGE = 1 // 초기 페이지 상수 값