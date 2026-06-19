package com.mujapps.domain.repositories

import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import kotlinx.coroutines.flow.Flow

interface IGolferRepository {
    fun getAllPlayers(): Flow<List<GolfPlayer>>
    suspend fun getPlayerById(playerId: String): GolfPlayer?
    fun getPlayerWithShots(playerId: String): Flow<PlayerWithShots?>
}