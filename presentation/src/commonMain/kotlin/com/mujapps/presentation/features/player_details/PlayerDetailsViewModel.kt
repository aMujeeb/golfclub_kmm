package com.mujapps.presentation.features.player_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mujapps.domain.use_cases.GetPlayerDetailsShotsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerDetailsViewModel(
    val mPlayerDetailsShotsUseCase: GetPlayerDetailsShotsUseCase,
    private val mPlayerId: String
) :
    ViewModel() {

    private val _detailsUiState = MutableStateFlow(DetailsUiState())
    var mDetailsUiState = _detailsUiState.asStateFlow()

    fun getUserDetails() {
        viewModelScope.launch {
            _detailsUiState.update {
                it.copy(mIsLoading = true, mErrorMessage = null, mPlayerShots = null)
            }

            try {
                mPlayerDetailsShotsUseCase.execute(mPlayerId).collect { details ->
                    _detailsUiState.update {
                        it.copy(
                            mIsLoading = false,
                            mErrorMessage = null,
                            mPlayerShots = details
                        )
                    }
                }
            } catch (ex: Exception) {
                _detailsUiState.update {
                    it.copy(
                        mIsLoading = false,
                        mErrorMessage = ex.message ?: "Unknown Error",
                        mPlayerShots = null
                    )
                }
            }
        }
    }
}