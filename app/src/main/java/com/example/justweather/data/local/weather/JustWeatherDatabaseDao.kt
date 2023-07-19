package com.example.justweather.data.local.weather

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JustWeatherDatabaseDao {
    @Query("SELECT * FROM savedweatherlocations WHERE isDeleted == 0 ORDER BY nameOfLocation ASC")
    fun getAllWeatherEntitiesMarkedAsNotDeleted(): Flow<List<SavedWeatherLocationEntity>>

    @Upsert
    suspend fun addSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)

    @Query("UPDATE savedweatherlocations SET isDeleted = 1 WHERE nameOfLocation = :nameOfWeatherLocationEntity")
    suspend fun markWeatherEntityAsDeleted(nameOfWeatherLocationEntity: String)

    @Query("UPDATE savedweatherlocations SET isDeleted = 0 WHERE nameOfLocation = :nameOfWeatherLocationEntity")
    suspend fun markWeatherEntityAsUnDeleted(nameOfWeatherLocationEntity: String)

    @Delete
    suspend fun deleteSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)
}