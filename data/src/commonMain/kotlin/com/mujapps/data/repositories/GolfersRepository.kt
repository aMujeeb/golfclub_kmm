package com.mujapps.data.repositories

import com.mujapps.data.mappers.toDomain
import com.mujapps.data.mappers.toPlayerWithShots
import com.mujapps.data.remote.RemoteDataSource
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import io.github.aakira.napier.Napier
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mujapps.data.paging.GolferPagingSource


class GolfersRepository(private val mRemoteDataSource: RemoteDataSource) : IGolferRepository {

    override suspend fun getPlayerWithShots(playerId: String): PlayerWithShots? {
        Napier.d("Fetching player with shots for playerId: $playerId", tag = "GolfersRepository")
        return mRemoteDataSource.getGolfPlayerDetailsWithShots(playerId)
            .onSuccess {
                Napier.d(
                    "Successfully fetched player: ${it.name}",
                    tag = "GolfersRepository"
                )
            }
            .onFailure {
                Napier.e(
                    "Failed to fetch player with shots for playerId: $playerId",
                    it,
                    tag = "GolfersRepository"
                )
            }
            .getOrNull()?.toPlayerWithShots()
    }

    override fun getPlayersPagingFlow(): Flow<PagingData<GolfPlayer>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GolferPagingSource(mRemoteDataSource) } //Instantiating it inline in the repository is the standard pattern for KMP/Android Paging projects and is perfectly safe.
        ).flow
    }
}