package com.example.justweather.data.remote.location


/**
 * An interface that provides a method to get the location name for the given latitude and longitude.
 */
fun interface ReverseGeocoder {

    /**
     * Used to return the name for the given [latitude] and [longitude], encapsulated in an instance
     * of [Result].
     */
    suspend fun getLocationNameForCoordinates(latitude: Double, longitude: Double): Result<String>
}

