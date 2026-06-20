package com.mujapps.data.repositories

import com.mujapps.data.mappers.toDomain
import com.mujapps.data.mappers.toPlayerWithShots
import com.mujapps.data.remote.RemoteDataSource
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import io.github.aakira.napier.Napier


class GolfersRepository(private val mRemoteDataSource: RemoteDataSource) : IGolferRepository {
    override suspend fun getAllPlayers(): List<GolfPlayer> {
        Napier.d("Fetching all players from remote", tag = "GolfersRepository")
        return mRemoteDataSource.getAllGolfPlayers()
            .onSuccess { Napier.d("Successfully fetched ${it.size} players", tag = "GolfersRepository") }
            .onFailure { Napier.e("Failed to fetch players", it, tag = "GolfersRepository") }
            .getOrThrow()
            .map { it.toDomain() }
    }

    override suspend fun getPlayerWithShots(playerId: String): PlayerWithShots? {
        Napier.d("Fetching player with shots for playerId: $playerId", tag = "GolfersRepository")
        return mRemoteDataSource.getGolfPlayerDetailsWithShots(playerId)
            .onSuccess { Napier.d("Successfully fetched player: ${it.name}", tag = "GolfersRepository") }
            .onFailure { Napier.e("Failed to fetch player with shots for playerId: $playerId", it, tag = "GolfersRepository") }
            .getOrNull()?.toPlayerWithShots()
    }
}