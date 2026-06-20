package com.mujapps.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shot",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playerId"])]
)
data class GolfShotEntity(
    @PrimaryKey val id: String,
    val playerId: String,
    val clubName: String,
    val ballSpeed: Double,
    val launchAngle: Double,
    val carryDistance: Double,
    val spinRate: Double,
    val timestamp: Long
)
