package com.mujapps.domain.use_cases

import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.repositories.IGolferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.github.aakira.napier.Napier
import androidx.paging.PagingData

class GetAllPlayersUseCase(private val mIGolferRepository: IGolferRepository) {

    fun executePaging(query: String? = null, clubs: List<String> = emptyList()): Flow<PagingData<GolfPlayer>> {
        Napier.d("Executing GetAllPlayersUseCase executePaging with query: $query, clubs: $clubs", tag = "GetAllPlayersUseCase")
        return mIGolferRepository.getPlayersPagingFlow(query, clubs)
    }
}