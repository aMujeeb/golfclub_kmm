package com.mujapps.presentation.features.player_listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mujapps.domain.use_cases.GetAllPlayersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerListingViewModel(val mGetAllPlayersUseCase: GetAllPlayersUseCase) : ViewModel() {

    private val _listUiState = MutableStateFlow(ListingUiState())
    val mListingState = _listUiState.asStateFlow()

    fun loadPlayerListings() {
        viewModelScope.launch {

            _listUiState.update {
                it.copy(mIsLoading = true)
            }

            try {
                mGetAllPlayersUseCase.execute().collect { list ->
                    _listUiState.update {
                        it.copy(
                            mIsLoading = false,
                            mErrorMessage = null,
                            mPlayerList = list
                        )
                    }
                }
            } catch (ex: Exception) {
                _listUiState.update {
                    it.copy(mIsLoading = false, mErrorMessage = ex.message ?: "Unknown Error")
                }
            }
        }
    }
}