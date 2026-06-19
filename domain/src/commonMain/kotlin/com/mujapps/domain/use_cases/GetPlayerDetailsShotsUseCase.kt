package com.mujapps.domain.use_cases

import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPlayerDetailsShotsUseCase(private val mIGolferRepository: IGolferRepository) {

    fun execute(playerId: String): Flow<PlayerWithShots?> = flow {
        emit(mIGolferRepository.getPlayerWithShots(playerId))
    }
}