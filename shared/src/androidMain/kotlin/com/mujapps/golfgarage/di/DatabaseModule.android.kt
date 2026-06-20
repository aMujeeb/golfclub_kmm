package com.mujapps.golfgarage.di

import android.content.Context
import androidx.room.Room
import com.mujapps.data.local.database.GolfDatabase
import com.mujapps.data.local.database.getDatabase
import org.koin.dsl.module
import io.github.aakira.napier.Napier

actual val databaseModule = module {//Platform specific RoomDatabase builder
    single {
        val context = get<Context>()
        val dbFile = context.getDatabasePath("golf_database.db")
        Napier.i("Android DB path resolved: ${dbFile.absolutePath}", tag = "DatabaseModule")
        val builder = Room.databaseBuilder<GolfDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
        getDatabase(builder)
    }
}