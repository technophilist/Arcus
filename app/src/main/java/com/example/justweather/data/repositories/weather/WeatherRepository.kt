package com.example.justweather.data.repositories.weather

import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.CurrentWeatherDetails
import com.example.justweather.domain.models.HourlyForecast
import com.example.justweather.domain.models.PrecipitationProbability
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * A repository that is responsible for containing all methods related with fetching weather
 * information.
 */
interface WeatherRepository {

    /**
     * Retrieves the [CurrentWeatherDetails] for the specified location.
     *
     * @param nameOfLocation The name of the location.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A [Result] object containing the weather details if successful, or an exception if not.
     */
    suspend fun fetchWeatherForLocation(
        nameOfLocation: String,
        latitude: String,
        longitude: String
    ): Result<CurrentWeatherDetails>

    /**
     * Used to get a a [Flow] of [BriefWeatherDetails] for each previously saved location.
     * If an error occurs while fetching the details of a single location, then, the
     * corresponding [BriefWeatherDetails] of that location would be [BriefWeatherDetails.EmptyBriefWeatherDetails].
     */
    fun getWeatherStreamForPreviouslySavedLocations(): Flow<List<BriefWeatherDetails>>

    /**
     * Saves the weather location with the provided [nameOfLocation], [latitude] and [longitude].
     */
    suspend fun saveWeatherLocation(nameOfLocation: String, latitude: String, longitude: String)

    /**
     * Deletes a weather location from the saved items.
     *
     * @param briefWeatherLocation The [BriefWeatherDetails] object representing the item
     * to delete.
     */
    suspend fun deleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails)

    /**
     * Returns a [Result] containing a list of [PrecipitationProbability] objects for the specified
     * location and date range.
     *
     * @param latitude The latitude of the location to retrieve precipitation probabilities for.
     * @param longitude The longitude of the location to retrieve precipitation probabilities for.
     * @param dateRange The date range to retrieve precipitation probabilities for. Defaults to
     * today and tomorrow.
     */
    suspend fun getHourlyPrecipitationProbabilities(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now().plusDays(1)
    ): Result<List<PrecipitationProbability>>

    /**
     * Fetches hourly forecasts for the given [latitude] and [longitude] within the specified
     * [dateRange]. It returns a [Result] object containing a list of [HourlyForecast] objects if
     * the fetch operation was successful, else an error message.
     */
    suspend fun fetchHourlyForecasts(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate>
    ): Result<List<HourlyForecast>>
}
