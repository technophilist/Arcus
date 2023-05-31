package com.example.justweather.data.repositories.weather

import com.example.justweather.domain.models.WeatherDetails

/**
 * A repository that is responsible for containing all methods related with fetching weather
 * information.
 */
interface WeatherRepository {

    /**
     * Retrieves the weather details for the specified location.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A [Result] object containing the weather details if successful, or an exception if not.
     */
    suspend fun fetchWeatherForLocation(latitude: String, longitude: String): Result<WeatherDetails>
}
