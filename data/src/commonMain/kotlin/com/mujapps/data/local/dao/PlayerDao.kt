package com.mujapps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mujapps.data.local.entities.PlayerEntity
import com.mujapps.data.local.entities.PlayerWithShots
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player")
    suspend fun getAllPlayersOffline(): List<PlayerEntity>

    @Transaction
    @Query("SELECT * FROM player WHERE id = :id")
    fun getPlayerWithShots(id: String): Flow<PlayerWithShots?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)
}