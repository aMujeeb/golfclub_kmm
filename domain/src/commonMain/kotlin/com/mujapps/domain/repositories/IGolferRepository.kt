package com.mujapps.domain.repositories

import androidx.paging.PagingData
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import kotlinx.coroutines.flow.Flow

interface IGolferRepository {
    suspend fun getPlayerWithShots(playerId: String): PlayerWithShots?
    fun getPlayersPagingFlow(): Flow<PagingData<GolfPlayer>>
}