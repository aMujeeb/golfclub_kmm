package com.mujapps.presentation.features.player_shot_details

import com.mujapps.domain.models.GolfShot

data class ShotsUiState(
    val mIsLoading: Boolean = false,
    val mErrorMessage: String? = null,
    val mPlayerName: String = "",
    val mOriginalShots: List<GolfShot> = emptyList(),
    val mFilteredShots: List<ShotWithNumber> = emptyList()
)
