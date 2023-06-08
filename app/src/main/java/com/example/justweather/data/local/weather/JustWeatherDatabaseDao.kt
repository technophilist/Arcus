package com.example.justweather.data.local.weather

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

@Dao
interface JustWeatherDatabaseDao {
    @Query("SELECT * FROM savedweatherlocations ORDER BY nameOfLocation ASC")
    fun getAllSavedWeatherEntities(): Flow<List<SavedWeatherLocationEntity>>

    @Upsert
    suspend fun addSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)

    @Delete
    suspend fun deleteSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)
}