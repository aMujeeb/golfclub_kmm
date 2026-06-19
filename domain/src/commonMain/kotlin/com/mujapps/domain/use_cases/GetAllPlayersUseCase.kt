package com.mujapps.domain.use_cases

import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllPlayersUseCase(private val mIGolferRepository: IGolferRepository) {

    fun execute() : Flow<List<GolfPlayer>> = flow {
        emit(mIGolferRepository.getAllPlayers())
    }
}