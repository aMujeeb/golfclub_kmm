package com.mujapps.presentation.features.player_details

import com.mujapps.domain.models.PlayerWithShots

data class DetailsUiState(
    val mIsLoading: Boolean = false,
    val mErrorMessage: String? = null,
    val mPlayerDetail: PlayerWithShots? = null
)
