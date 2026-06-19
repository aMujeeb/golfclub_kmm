package com.mujapps.data.repositories

import com.mujapps.data.remote.RemoteDataSource
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow

class GolfersRepository(private val mRemoteDataSource: RemoteDataSource) : IGolferRepository {
    override fun getAllPlayers(): Flow<List<GolfPlayer>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayerById(playerId: String): GolfPlayer? {
        TODO("Not yet implemented")
    }

    override fun getPlayerWithShots(playerId: String): Flow<PlayerWithShots?> {
        TODO("Not yet implemented")
    }
}