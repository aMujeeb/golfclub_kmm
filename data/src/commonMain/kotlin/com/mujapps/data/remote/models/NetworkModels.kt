package com.mujapps.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class GolfPlayerDto(
    val id: String,
    val name: String,
    val profPicUrl: String,
    val preferenceClub: String,
    val averageBallSpeed: Double,
    val averageDistance: Double,
)

@Serializable
data class GolfShotDto(
    val id: String,
    val playerId: String,
    val clubName: String,
    val ballSpeed: Double,
    val launchAngle: Double,
    val carryDistance: Double,
    val spinRate: Double,
    val createdAt: String
)