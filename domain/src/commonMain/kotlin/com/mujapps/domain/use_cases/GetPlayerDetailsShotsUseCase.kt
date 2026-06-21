package com.mujapps.domain.use_cases

import com.mujapps.domain.models.GolfShot
import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import io.github.aakira.napier.Napier

class GetPlayerDetailsShotsUseCase(private val mIGolferRepository: IGolferRepository) {

    fun observePlayerShots(playerId: String): Flow<PlayerWithShots?> {
        Napier.d(
            "Observing for playerId: $playerId",
            tag = "GetPlayerDetailsShotsUseCase"
        )
        return mIGolferRepository.getPlayerShots(playerId)
    }

    suspend fun syncPlayerShots(playerId: String) {
        Napier.d("Syncing for playerId: $playerId", tag = "GetPlayerDetailsShotsUseCase")
        mIGolferRepository.syncPlayerShots(playerId)
    }

    fun observeAllShots(): Flow<List<GolfShot>> {
        Napier.d("Observing all shots", tag = "GetPlayerDetailsShotsUseCase")
        return mIGolferRepository.getAllShots()
    }
}