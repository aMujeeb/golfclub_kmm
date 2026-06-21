package com.mujapps.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mujapps.data.mappers.toDomain
import com.mujapps.data.remote.RemoteDataSource
import com.mujapps.domain.models.GolfPlayer

import com.mujapps.data.local.dao.PlayerDao
import com.mujapps.data.mappers.toEntity

class GolferPagingSource(
    private val mRemoteDataSource: RemoteDataSource,
    private val playerDao: PlayerDao,
    private val query: String? = null
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
            val response = mRemoteDataSource.getGolfPlayers(page = page, limit = limit, search = query)
            val playersDto = response.getOrThrow()

            // Save to DB
            playerDao.insertPlayers(playersDto.map { it.toEntity() })

            val players = playersDto.map { it.toDomain() }

            LoadResult.Page(
                data = players,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (players.isEmpty() || players.size < limit) null else page + 1
            )
        } catch (e: Exception) {
            val offlinePlayers = if (query.isNullOrBlank()) {
                playerDao.getAllPlayersOffline()
            } else {
                playerDao.searchPlayersOffline("%$query%")
            }.map { it.toDomain() }
            if (offlinePlayers.isNotEmpty()) {
                LoadResult.Page(
                    data = offlinePlayers,
                    prevKey = null,
                    nextKey = null
                )
            } else {
                LoadResult.Error(e)
            }
        }
    }
}
