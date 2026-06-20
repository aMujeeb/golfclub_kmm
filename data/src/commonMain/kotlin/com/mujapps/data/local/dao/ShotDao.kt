package com.mujapps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mujapps.data.local.entities.GolfShotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShotDao {

    @Query("DELETE FROM shot WHERE playerId = :playerId")
    suspend fun deleteShotsForPlayer(playerId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShots(shots: List<GolfShotEntity>)
}