package com.mujapps.data.repositories

import com.mujapps.data.mappers.toDomain
import com.mujapps.data.mappers.toPlayerWithShots
import com.mujapps.data.remote.RemoteDataSource
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow


class GolfersRepository(private val mRemoteDataSource: RemoteDataSource) : IGolferRepository {
    override suspend fun getAllPlayers(): List<GolfPlayer> {
        return mRemoteDataSource.getAllGolfPlayers()
            .getOrThrow()
            .map { it.toDomain() }
    }

    override suspend fun getPlayerWithShots(playerId: String): PlayerWithShots? {
        return mRemoteDataSource.getGolfPlayerDetailsWithShots(playerId)
            .getOrNull()?.toPlayerWithShots()
    }
}