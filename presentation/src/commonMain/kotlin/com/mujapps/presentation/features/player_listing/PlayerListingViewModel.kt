package com.mujapps.presentation.features.player_listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mujapps.domain.use_cases.GetAllPlayersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mujapps.domain.models.GolfPlayer
import kotlinx.coroutines.flow.Flow

class PlayerListingViewModel(val mGetAllPlayersUseCase: GetAllPlayersUseCase) : ViewModel() {
    val mPlayersPagingFlow: Flow<PagingData<GolfPlayer>> =
        mGetAllPlayersUseCase.executePaging()
            .cachedIn(viewModelScope)
}