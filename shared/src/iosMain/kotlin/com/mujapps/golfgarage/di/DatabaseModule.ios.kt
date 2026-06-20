package com.mujapps.golfgarage.di

import androidx.room.Room
import com.mujapps.data.local.database.GolfDatabase
import com.mujapps.data.local.database.GolfDatabaseConstructor
import com.mujapps.data.local.database.getDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import io.github.aakira.napier.Napier

@OptIn(ExperimentalForeignApi::class)
actual val databaseModule = module {//Platform specific RoomDatabase builder
    single {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val path = requireNotNull(documentDirectory?.path)
        val file = "$path/golf_database.db"
        Napier.i("iOS DB path resolved: $file", tag = "DatabaseModule")
        val builder = Room.databaseBuilder<GolfDatabase>(
            name = file,
            factory = { GolfDatabaseConstructor.initialize() }
        )
        getDatabase(builder)
    }
}