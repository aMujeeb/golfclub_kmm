package com.mujapps.golfgarage.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.mujapps.golfgarage.ui.components.PlayerRowItem
import com.mujapps.presentation.features.player_listing.PlayerListingViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.mujapps.golfgarage.navigation.NavRoutes
@Composable
fun GolfersListView(
    backStack: NavBackStack<NavKey>,
    mViewModel: PlayerListingViewModel = koinViewModel()
) {

    val mPagingItems = mViewModel.mPlayersPagingFlow.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Initial loading state
        if (mPagingItems.loadState.refresh is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            }
        }

        // Show refresh error
        val refreshError = mPagingItems.loadState.refresh as? LoadState.Error
        if (refreshError != null) {
            Text(text = refreshError.error.message ?: "Unknown Error")
        }

        // List
        if (mPagingItems.itemCount > 0) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    count = mPagingItems.itemCount,
                    key = mPagingItems.itemKey { it.mId },
                    contentType = mPagingItems.itemContentType { "Player" }
                ) { index ->
                    val player = mPagingItems[index]
                    if (player != null) {
                        PlayerRowItem(player) {
                            backStack.add(NavRoutes.Details(player.mId))
                        }
                    }
                }

                // Show loading at the bottom for appending pages
                if (mPagingItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                }

                // Show error at the bottom for appending pages
                val appendError = mPagingItems.loadState.append as? LoadState.Error
                if (appendError != null) {
                    item {
                        Text(
                            text = appendError.error.message ?: "Failed to load more players",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        } else if (mPagingItems.loadState.refresh !is LoadState.Loading && refreshError == null) {
            Text(text = "No Players Available")
        }
    }
}