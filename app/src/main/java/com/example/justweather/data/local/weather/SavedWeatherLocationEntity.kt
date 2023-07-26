package com.example.justweather.data.local.weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.justweather.domain.models.Coordinates
import com.example.justweather.domain.models.SavedLocation

/**
 * This class represents a saved weather location entity with the specified [nameOfLocation],
 * [latitude] and [longitude].
 */
@Entity(tableName = "SavedWeatherLocations")
data class SavedWeatherLocationEntity(
    @PrimaryKey val nameOfLocation: String,
    val latitude: String,
    val longitude: String,
    val isDeleted: Boolean = false
)

/**
 * Used to map an instance of [SavedWeatherLocationEntity] to an instance of [SavedLocation].
 */
fun SavedWeatherLocationEntity.toSavedLocation() = SavedLocation(
    nameOfLocation = nameOfLocation,
    coordinates = Coordinates(latitude = latitude, longitude = longitude)
)