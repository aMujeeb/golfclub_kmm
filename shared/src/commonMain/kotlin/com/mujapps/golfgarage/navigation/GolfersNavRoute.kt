package com.mujapps.golfgarage.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import com.mujapps.presentation.features.player_listing.PlayerListingViewModel
import com.mujapps.golfgarage.ui.components.PlayerDetailsHeader
import com.mujapps.golfgarage.ui.components.PlayersListHeader
import com.mujapps.golfgarage.ui.views.GolfPlayerDetailsView
import com.mujapps.golfgarage.ui.views.GolfersListView
import com.mujapps.golfgarage.ui.views.GolfPlayerShotsView
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun GolfersNavRoute(
    isDarkTheme: () -> Boolean,
    onToggleDarkTheme: () -> Unit,
) {
    val mListingViewModel: PlayerListingViewModel = koinViewModel()
    var topBarTitle by remember { mutableStateOf("Player Details") }

    val mBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule =
                SerializersModule {
                    polymorphic(baseClass = NavKey::class) {
                        subclass(NavRoutes.Listing::class, NavRoutes.Listing.serializer())
                        subclass(NavRoutes.Details::class, NavRoutes.Details.serializer())
                        subclass(NavRoutes.Shots::class, NavRoutes.Shots.serializer())
                    }
                }
        },
        elements = arrayOf(NavRoutes.Listing)
    )

    Scaffold(
        topBar = {
            val currentRoute = mBackStack.lastOrNull()
            when (currentRoute) {
                is NavRoutes.Listing -> {
                    val searchQuery by mListingViewModel.searchQuery.collectAsStateWithLifecycle()
                    val selectedClubs by mListingViewModel.selectedClubs.collectAsStateWithLifecycle()
                    PlayersListHeader(
                        searchQuery = searchQuery,
                        onSearchQueryChanged = { mListingViewModel.onSearchQueryChanged(it) },
                        selectedClubs = selectedClubs,
                        onClubFilterToggled = { mListingViewModel.onClubFilterToggled(it) },
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = onToggleDarkTheme,
                    )
                }
                is NavRoutes.Details, is NavRoutes.Shots -> {
                    PlayerDetailsHeader(
                        title = topBarTitle,
                        onBack = {
                            if (mBackStack.size > 1) {
                                mBackStack.removeAt(mBackStack.lastIndex)
                                if (mBackStack.lastOrNull() is NavRoutes.Listing) {
                                    topBarTitle = "Player Details"
                                }
                            }
                        },
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = onToggleDarkTheme,
                    )
                }
                else -> {}
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = mBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {

                    is NavRoutes.Listing -> NavEntry(key) {
                        GolfersListView(
                            backStack = mBackStack,
                            mViewModel = mListingViewModel,
                        )
                    }

                    is NavRoutes.Details -> NavEntry(key) {
                        GolfPlayerDetailsView(
                            mPlayerId = key.mPlayerId,
                            backStack = mBackStack,
                            onPlayerLoaded = { topBarTitle = it }
                        )
                    }

                    is NavRoutes.Shots -> NavEntry(key) {
                        GolfPlayerShotsView(
                            mPlayerId = key.mPlayerId,
                            backStack = mBackStack,
                            onPlayerLoaded = { topBarTitle = it }
                        )
                    }

                    else -> throw IllegalArgumentException("Invalid route: $key")
                }
            },
        )
    }
}