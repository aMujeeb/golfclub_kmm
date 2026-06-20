package com.mujapps.domain.use_cases

import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import io.github.aakira.napier.Napier

class GetPlayerDetailsShotsUseCase(private val mIGolferRepository: IGolferRepository) {

    fun execute(playerId: String): Flow<PlayerWithShots?> {
        Napier.d(
            "Executing GetPlayerDetailsShotsUseCase for playerId: $playerId",
            tag = "GetPlayerDetailsShotsUseCase"
        )
        return mIGolferRepository.getPlayerShots(playerId)
    }
}