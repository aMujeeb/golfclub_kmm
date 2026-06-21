package com.mujapps.presentation.features.player_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mujapps.domain.use_cases.GetPlayerDetailsShotsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import io.github.aakira.napier.Napier

class PlayerDetailsViewModel(
    val mPlayerDetailsShotsUseCase: GetPlayerDetailsShotsUseCase,
    private val mPlayerId: String
) :
    ViewModel() {

    private val _syncState = MutableStateFlow(DetailsUiState())

    // Combine the background sync state with the reactive Room database flow
    // This allows us to track network loading/error states separately from the actual database data
    val mDetailsUiState = combine(
        _syncState,
        mPlayerDetailsShotsUseCase.observePlayerShots(mPlayerId),
        mPlayerDetailsShotsUseCase.observeAllShots()
    ) { syncState, playerDetail, allShots ->
        // Napier log to verify when this merge happens
        Napier.d(
            "Ui state, DB state, and All shots updated",
            tag = "PlayerDetailsViewModel"
        )

        // We take the loading/error state from the API sync, but the data strictly from the DB!
        syncState.copy(
            mPlayerDetail = playerDetail,
            mAllShots = allShots
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsUiState(mIsLoading = true)
    )

    fun getUserDetails() {
        // Concurrently sync with the API
        viewModelScope.launch(Dispatchers.IO) {
            _syncState.update { it.copy(mIsLoading = true, mErrorMessage = null) }
            try {
                mPlayerDetailsShotsUseCase.syncPlayerShots(mPlayerId)
                // Note: We do NOT update the success state here. The DB observer will handle that automatically!
            } catch (ex: Exception) {
                _syncState.update {
                    it.copy(mErrorMessage = "Background sync failed: ${ex.message}")
                }
            } finally {
                _syncState.update { it.copy(mIsLoading = false) }
            }
        }
    }
}