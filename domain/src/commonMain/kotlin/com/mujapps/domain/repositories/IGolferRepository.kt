package com.mujapps.domain.repositories

import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import kotlinx.coroutines.flow.Flow

interface IGolferRepository {
    suspend fun getAllPlayers(): List<GolfPlayer>
    suspend fun getPlayerWithShots(playerId: String): PlayerWithShots?
}