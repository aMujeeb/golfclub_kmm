package com.mujapps.golfgarage.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.mujapps.presentation.features.player_details.PlayerDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GolfPlayerDetailsView(
    mPlayerId: String, backStack: NavBackStack<NavKey>,
    mViewModel: PlayerDetailsViewModel = koinViewModel(parameters = { parametersOf(mPlayerId) })
) {

    val mDetailsUiState = mViewModel.mDetailsUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        mViewModel.getUserDetails()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Details View")

        if (mDetailsUiState.value.mPlayerDetail != null) {
            val details = mDetailsUiState.value.mPlayerDetail
            Text("Player: ${details?.mPlayer?.mName ?: "Unknown"}")
            Text("Total Shots: ${details?.mShots?.size ?: 0}")
        }

    }
}