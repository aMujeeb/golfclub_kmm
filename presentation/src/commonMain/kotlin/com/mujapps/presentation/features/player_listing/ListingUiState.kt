package com.mujapps.presentation.features.player_listing

import com.mujapps.domain.models.GolfPlayer

data class ListingUiState(
    val mIsLoading: Boolean = false,
    val mErrorMessage: String? = null,
    val mPlayerList: List<GolfPlayer> = emptyList()
)
