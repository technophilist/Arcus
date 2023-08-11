package com.example.arcus.data.local.textgeneration

import androidx.room.Entity

/**
 * An entity that contains the [generatedDescription] for a specific location based on a specific
 * set of weather conditions.
 */
@Entity(
    tableName = "GeneratedTextForLocationEntities",
    primaryKeys = ["nameOfLocation", "temperature", "conciseWeatherDescription", "generatedDescription"]
)
data class GeneratedTextForLocationEntity(
    val nameOfLocation: String,
    val temperature: Int,
    val conciseWeatherDescription: String,
    val generatedDescription: String,
)
