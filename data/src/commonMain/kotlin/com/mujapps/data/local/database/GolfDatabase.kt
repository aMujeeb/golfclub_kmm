package com.mujapps.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mujapps.data.local.dao.PlayerDao
import com.mujapps.data.local.dao.ShotDao
import com.mujapps.data.local.entities.GolfShotEntity
import com.mujapps.data.local.entities.PlayerEntity
import io.github.aakira.napier.Napier

@Database(
    entities = [PlayerEntity::class, GolfShotEntity::class],
    version = 1
)
@ConstructedBy(GolfDatabaseConstructor::class)
abstract class GolfDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun shotDao(): ShotDao
}

fun getDatabase(
    builder: RoomDatabase.Builder<GolfDatabase>
): GolfDatabase {
    Napier.i("Building Room GolfDatabase instance", tag = "GolfDatabase")
    return builder
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver()) //Required to Room multiplatform
        .build()
}

// Room KMP requires a constructor class
expect object GolfDatabaseConstructor : RoomDatabaseConstructor<GolfDatabase> {
    override fun initialize(): GolfDatabase
}