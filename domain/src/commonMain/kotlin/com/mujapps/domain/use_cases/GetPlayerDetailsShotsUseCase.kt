package com.mujapps.domain.use_cases

import com.mujapps.domain.models.GolfShot
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.github.aakira.napier.Napier

class GetPlayerDetailsShotsUseCase(private val mIGolferRepository: IGolferRepository) {

    fun execute(playerId: String): Flow<List<GolfShot>?> = flow {
        Napier.d("Executing GetPlayerDetailsShotsUseCase for playerId: $playerId", tag = "GetPlayerDetailsShotsUseCase")
        val shots = mIGolferRepository.getPlayerShots(playerId)
        Napier.d("Retrieved ${shots?.size ?: 0} shots for player", tag = "GetPlayerDetailsShotsUseCase")
        emit(shots)
    }
}