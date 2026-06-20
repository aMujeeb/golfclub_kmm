package com.mujapps.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Player")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val profPicUrl: String,
    val preferenceClub: String,
    val averageBallSpeed: Double,
    val averageDistance: Double
)
