package com.example.justweather.data.repositories.weather

import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.WeatherDetails
import kotlinx.coroutines.flow.Flow

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

    /**
     * Used to get a a [Flow] of [BriefWeatherDetails] for each previously saved location.
     * If an error occurs while fetching the details of a single location, then, the
     * corresponding [BriefWeatherDetails] of that location would be [BriefWeatherDetails.EmptyBriefWeatherDetails].
     */
    fun getWeatherStreamForPreviouslySavedLocations(): Flow<BriefWeatherDetails>

    /**
     * Saves the weather location with the provided [nameOfLocation], [latitude] and [longitude].
     */
    suspend fun saveWeatherLocation(nameOfLocation: String, latitude: String, longitude: String)
}
