package com.mujapps.domain.use_cases

import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.github.aakira.napier.Napier

class GetAllPlayersUseCase(private val mIGolferRepository: IGolferRepository) {

    fun execute() : Flow<List<GolfPlayer>> = flow {
        Napier.d("Executing GetAllPlayersUseCase", tag = "GetAllPlayersUseCase")
        val players = mIGolferRepository.getAllPlayers()
        Napier.d("Retrieved ${players.size} players", tag = "GetAllPlayersUseCase")
        emit(players)
    }
}