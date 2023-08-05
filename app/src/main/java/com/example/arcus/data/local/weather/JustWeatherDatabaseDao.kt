package com.example.arcus.data.local.weather

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JustWeatherDatabaseDao {
    @Query("SELECT * FROM savedweatherlocations WHERE isDeleted == 0")
    fun getAllWeatherEntitiesMarkedAsNotDeleted(): Flow<List<SavedWeatherLocationEntity>>

    @Query("SELECT * FROM savedweatherlocations")
    fun getAllWeatherEntitiesIrrespectiveOfDeletedStatus(): Flow<List<SavedWeatherLocationEntity>>

    @Upsert
    suspend fun addSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)

    @Query("UPDATE savedweatherlocations SET isDeleted = 1 WHERE nameOfLocation = :nameOfWeatherLocationEntity")
    suspend fun markWeatherEntityAsDeleted(nameOfWeatherLocationEntity: String)

    @Query("UPDATE savedweatherlocations SET isDeleted = 0 WHERE nameOfLocation = :nameOfWeatherLocationEntity")
    suspend fun markWeatherEntityAsUnDeleted(nameOfWeatherLocationEntity: String)

    @Query("DELETE FROM savedweatherlocations WHERE isDeleted = 1")
    suspend fun deleteAllItemsMarkedAsDeleted()

    @Delete
    suspend fun deleteSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)
}