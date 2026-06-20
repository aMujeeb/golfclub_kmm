package com.mujapps.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mujapps.data.mappers.toDomain
import com.mujapps.data.remote.RemoteDataSource
import com.mujapps.domain.models.GolfPlayer

class GolferPagingSource(
    private val mRemoteDataSource: RemoteDataSource
) : PagingSource<Int, GolfPlayer>() {

    override fun getRefreshKey(state: PagingState<Int, GolfPlayer>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GolfPlayer> {
        val page = params.key ?: 1
        val limit = params.loadSize
        
        return try {
            val response = mRemoteDataSource.getGolfPlayers(page = page, limit = limit)
            val playersDto = response.getOrThrow()
            val players = playersDto.map { it.toDomain() }
            
            LoadResult.Page(
                data = players,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (players.isEmpty() || players.size < limit) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
