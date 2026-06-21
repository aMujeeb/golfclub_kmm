package com.mujapps.data.repositories

import com.mujapps.data.mappers.toDomain
import com.mujapps.data.mappers.toEntity
import com.mujapps.data.remote.RemoteDataSource
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import io.github.aakira.napier.Napier
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mujapps.data.local.dao.PlayerDao
import com.mujapps.data.local.dao.ShotDao
import com.mujapps.data.paging.GolferPagingSource
import com.mujapps.domain.models.GolfShot
import com.mujapps.domain.models.PlayerWithShots
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class GolfersRepository(
    private val mRemoteDataSource: RemoteDataSource, private val playerDao: PlayerDao,
    private val shotDao: ShotDao,
) : IGolferRepository {

    override fun getPlayerShots(playerId: String): Flow<PlayerWithShots?> {
        Napier.d("Observing player shots from DB for playerId: $playerId", tag = "GolfersRepository")
        return playerDao.getPlayerWithShots(playerId)
            .distinctUntilChanged()
            .map { it?.toDomain() }
    }

    override suspend fun syncPlayerShots(playerId: String) {
        Napier.d("Starting background API sync for playerId: $playerId", tag = "GolfersRepository")
        try {
            val response = mRemoteDataSource.getGolfPlayerShots(playerId)
            response.onSuccess { dtos ->
                Napier.d("API sync success! Saving ${dtos.size} shots to DB", tag = "GolfersRepository")
                shotDao.deleteShotsForPlayer(playerId)
                shotDao.insertShots(dtos.map { it.toEntity() })
            }.onFailure {
                Napier.e("API sync failed for playerId: $playerId", it, tag = "GolfersRepository")
            }
        } catch (e: Exception) {
            Napier.e("Network exception during sync for playerId: $playerId", e, tag = "GolfersRepository")
        }
    }

    override fun getPlayersPagingFlow(query: String?, clubs: List<String>): Flow<PagingData<GolfPlayer>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GolferPagingSource(
                    mRemoteDataSource,
                    playerDao,
                    query,
                    clubs
                )
            } //Instantiating it inline in the repository is the standard pattern for KMP/Android Paging projects and is perfectly safe.
        ).flow
    }
}