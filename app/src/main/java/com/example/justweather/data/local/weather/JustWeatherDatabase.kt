package com.example.justweather.data.local.weather

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedWeatherLocationEntity::class], version = 1)
abstract class JustWeatherDatabase : RoomDatabase() {

    abstract fun getDao(): JustWeatherDatabaseDao

    companion object {
        const val DATABASE_NAME = "Just_Weather_Database"
    }
}