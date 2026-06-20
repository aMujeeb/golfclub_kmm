package com.mujapps.golfgarage.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.mujapps.golfgarage.ui.views.GolfPlayerDetailsView
import com.mujapps.golfgarage.ui.views.GolfersListView
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun GolfersNavRoute() {
    val mBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule =
                SerializersModule {
                    polymorphic(baseClass = NavKey::class) {
                        subclass(NavRoutes.Listing::class, NavRoutes.Listing.serializer())
                        subclass(NavRoutes.Details::class, NavRoutes.Details.serializer())
                    }
                }
        },
        elements = arrayOf(NavRoutes.Listing)
    )

    NavDisplay(
        backStack = mBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {

                is NavRoutes.Listing -> NavEntry(key) {
                    GolfersListView(mBackStack)
                }

                is NavRoutes.Details -> NavEntry(key) {
                    GolfPlayerDetailsView(key.mPlayerId, mBackStack)
                }

                else -> throw IllegalArgumentException("Invalid route: $key")
            }
        },
    )
}