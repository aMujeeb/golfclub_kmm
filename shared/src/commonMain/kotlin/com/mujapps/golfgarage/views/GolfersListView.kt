package com.mujapps.golfgarage.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.mujapps.presentation.features.player_listing.PlayerListingViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GolfersListView(
    backStack: NavBackStack<NavKey>,
    mViewModel: PlayerListingViewModel = koinViewModel()
) {

    val mUiState = mViewModel.mListingState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        mViewModel.loadPlayerListings()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (mUiState.value.mIsLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            }
        }

        if (mUiState.value.mErrorMessage.isNullOrEmpty().not()) {
            Text(text = mUiState.value.mErrorMessage.toString())
        }

        if (mUiState.value.mPlayerList.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(mUiState.value.mPlayerList) { player ->
                    Text(text = player.mName)
                }
            }
        } else {
            Text(text = "No Players Available")
        }
    }
}