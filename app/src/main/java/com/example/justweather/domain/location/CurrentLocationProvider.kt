package com.example.justweather.domain.location

import com.example.justweather.domain.models.Coordinates

/**
 * A provider that provides the current location of the device.
 */
fun interface CurrentLocationProvider {
    /**
     * Returns the current location of the device encapsulated in an instance of [Result].
     */
    suspend fun getCurrentLocation(): Result<Coordinates>
}
