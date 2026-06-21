package com.mujapps.golfgarage.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoutes : NavKey {

    @Serializable
    @SerialName("Listing")
    data object Listing : NavRoutes

    @Serializable
    @SerialName("Details")
    data class Details(val mPlayerId: String) : NavRoutes

    @Serializable
    @SerialName("Shots")
    data class Shots(val mPlayerId: String) : NavRoutes
}