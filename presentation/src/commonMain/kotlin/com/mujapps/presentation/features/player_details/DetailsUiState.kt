package com.mujapps.presentation.features.player_details

import com.mujapps.domain.models.GolfShot

data class DetailsUiState(
    val mIsLoading: Boolean = false,
    val mErrorMessage: String? = null,
    val mPlayerShots: List<GolfShot>? = null
)
