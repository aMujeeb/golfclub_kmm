package com.mujapps.presentation.features

import androidx.paging.PagingData
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockGolfRepository : IGolferRepository {
    
    var playerShotsResultFlow: Flow<PlayerWithShots?> = flowOf(null)
    var playersPagingFlowResult: Flow<PagingData<GolfPlayer>> = flowOf()
    
    var lastSyncedPlayerId: String? = null
    var syncPlayerShotsError: Throwable? = null
    var syncPlayerShotsCalled = false

    override fun getPlayerShots(playerId: String): Flow<PlayerWithShots?> {
        return playerShotsResultFlow
    }

    override suspend fun syncPlayerShots(playerId: String) {
        syncPlayerShotsCalled = true
        lastSyncedPlayerId = playerId
        syncPlayerShotsError?.let { throw it }
    }

    override fun getPlayersPagingFlow(query: String?, clubs: List<String>): Flow<PagingData<GolfPlayer>> {
        return playersPagingFlowResult
    }
}
