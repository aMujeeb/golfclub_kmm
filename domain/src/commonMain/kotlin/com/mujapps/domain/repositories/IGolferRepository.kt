package com.mujapps.domain.repositories

import androidx.paging.PagingData
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.GolfShot
import com.mujapps.domain.models.PlayerWithShots
import kotlinx.coroutines.flow.Flow

interface IGolferRepository {
    fun getPlayerShots(playerId: String): Flow<PlayerWithShots?>
    suspend fun syncPlayerShots(playerId: String)
    fun getPlayersPagingFlow(): Flow<PagingData<GolfPlayer>>
}