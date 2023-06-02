package com.example.justweather.data.local.weather

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class represents a saved weather location entity with the specified [id],[nameOfLocation],
 * [latitude] and [longitude].
 */
@Entity(tableName = "SavedWeatherLocations")
data class SavedWeatherLocationEntity(
    @PrimaryKey val id: String,
    val nameOfLocation: String,
    val latitude: String,
    val longitude: String
)
