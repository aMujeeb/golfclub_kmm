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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GolfersRepository(
    private val mRemoteDataSource: RemoteDataSource, private val playerDao: PlayerDao,
    private val shotDao: ShotDao,
) : IGolferRepository {

    override fun getPlayerShots(playerId: String): Flow<PlayerWithShots?> = channelFlow {
        Napier.d("Fetching shots for playerId: $playerId", tag = "GolfersRepository")

        launch {
            try {
                val response = mRemoteDataSource.getGolfPlayerShots(playerId)
                response.onSuccess { dtos ->
                    Napier.d(
                        "Successfully fetched player shots, saving to DB",
                        tag = "GolfersRepository"
                    )
                    shotDao.deleteShotsForPlayer(playerId)
                    shotDao.insertShots(dtos.map { it.toEntity() })
                }.onFailure {
                    Napier.e(
                        "Failed to fetch shots from API for playerId: $playerId",
                        it,
                        tag = "GolfersRepository"
                    )
                }
            } catch (e: Exception) {
                Napier.e(
                    "Network exception fetching shots for playerId: $playerId",
                    e,
                    tag = "GolfersRepository"
                )
            }
        }

        playerDao.getPlayerWithShots(playerId).collectLatest { entity ->
            send(entity?.toDomain())
        }
    }

    override fun getPlayersPagingFlow(): Flow<PagingData<GolfPlayer>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GolferPagingSource(
                    mRemoteDataSource,
                    playerDao
                )
            } //Instantiating it inline in the repository is the standard pattern for KMP/Android Paging projects and is perfectly safe.
        ).flow
    }
}