package com.example.arcus.data.local.textgeneration

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An entity that contains the [generatedDescription] for a specific location based on a specific
 * set of weather conditions.
 */
@Entity(tableName = "GeneratedTextForLocationEntities")
data class GeneratedTextForLocationEntity(
    @PrimaryKey val nameOfLocation: String,
    val temperature: Int,
    val conciseWeatherDescription: String,
    val generatedDescription: String,
)
