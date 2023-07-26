package com.example.justweather.domain.models

/**
 * A domain object that represents a location saved by the user.
 */
data class SavedLocation(
    val nameOfLocation: String,
    val coordinates: Coordinates
)