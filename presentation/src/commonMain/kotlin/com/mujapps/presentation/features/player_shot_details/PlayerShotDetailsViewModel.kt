package com.mujapps.presentation.features.player_shot_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mujapps.domain.models.GolfShot
import com.mujapps.domain.use_cases.GetPlayerDetailsShotsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PlayerShotDetailsViewModel(
    private val mPlayerDetailsShotsUseCase: GetPlayerDetailsShotsUseCase,
    private val mPlayerId: String
) : ViewModel() {

    val selectedClubName = MutableStateFlow("All clubs")

    val mShotsUiState = combine(
        mPlayerDetailsShotsUseCase.observePlayerShots(mPlayerId),
        selectedClubName
    ) { playerDetail, club ->
        if (playerDetail == null) {
            ShotsUiState(mIsLoading = true)
        } else {
            val player = playerDetail.mPlayer
            val originalShots = playerDetail.mShots
            
            // Sort ascending chronologically to assign absolute sequential shot numbers
            val chronologicalShots = originalShots.sortedBy { it.mTimestamp }
            val shotsWithNumber = chronologicalShots.mapIndexed { index, shot ->
                ShotWithNumber(shot, index + 1)
            }
            
            // Filter shots
            val filtered = if (club == "All clubs") {
                shotsWithNumber
            } else {
                shotsWithNumber.filter { it.shot.mClubName.equals(club, ignoreCase = true) }
            }

            // Default to chronological descending (newest first)
            val sorted = filtered.sortedByDescending { it.shot.mTimestamp }

            ShotsUiState(
                mIsLoading = false,
                mPlayerName = player.mName,
                mOriginalShots = originalShots,
                mFilteredShots = sorted
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShotsUiState(mIsLoading = true)
    )

    fun onClubSelected(clubName: String) {
        selectedClubName.value = clubName
    }
}
