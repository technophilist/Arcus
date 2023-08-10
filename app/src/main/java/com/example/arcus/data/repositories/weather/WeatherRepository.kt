package com.example.arcus.data.repositories.weather

import com.example.arcus.domain.models.BriefWeatherDetails
import com.example.arcus.domain.models.CurrentWeatherDetails
import com.example.arcus.domain.models.HourlyForecast
import com.example.arcus.domain.models.PrecipitationProbability
import com.example.arcus.domain.models.SavedLocation
import com.example.arcus.domain.models.SingleWeatherDetail
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
     * Used to get a a [Flow] of [List] of [SavedLocation]s that were previously saved by the user.
     */
    fun getSavedLocationsListStream(): Flow<List<SavedLocation>>

    /**
     * Saves the weather location with the provided [nameOfLocation], [latitude] and [longitude].
     */
    suspend fun saveWeatherLocation(nameOfLocation: String, latitude: String, longitude: String)

    /**
     * Deletes a weather location from the saved items. Deleting an item using this method will
     * allow it to be restored using [tryRestoringDeletedWeatherLocation]. If an item is needed to be
     * permanently deleted, use [permanentlyDeleteWeatherLocationFromSavedItems].
     * @param briefWeatherLocation The [BriefWeatherDetails] object representing the item
     * to delete.
     */
    suspend fun deleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails)

    /**
     * Used to permanently delete a weather location from the saved items. If an item is needed to be
     * deleted, with a chance of it getting restored using [tryRestoringDeletedWeatherLocation], then
     * use [deleteWeatherLocationFromSavedItems].
     *
     * @param briefWeatherLocation The [BriefWeatherDetails] object representing the item
     * to permanently delete.
     */
    suspend fun permanentlyDeleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails)

    /**
     * Used to try restoring a recently deleted weather location with the provided [nameOfLocation].
     * This method doesn't guarantee that the item would be restored. As the name indicates, it only
     * tries to restore the item.
     */
    suspend fun tryRestoringDeletedWeatherLocation(nameOfLocation: String)

    /**
     * Returns a [Result] containing a list of [PrecipitationProbability] objects for the specified
     * location and date range.
     *
     * @param latitude The latitude of the location to retrieve precipitation probabilities for.
     * @param longitude The longitude of the location to retrieve precipitation probabilities for.
     * @param dateRange The date range to retrieve precipitation probabilities for. Defaults to
     * today and tomorrow.
     */
    suspend fun fetchHourlyPrecipitationProbabilities(
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


    /**
     * Used to fetch a list of [SingleWeatherDetail] items for the current day encapsulated in a
     * [Result] type. These items represent additional weather information for the given location at
     * the specified [latitude] and [longitude].
     */
    suspend fun fetchAdditionalWeatherInfoItemsListForCurrentDay(
        latitude: String,
        longitude: String
    ): Result<List<SingleWeatherDetail>>

    /**
     * Used to fetch an AI generated summary based on the provided [currentWeatherDetails]. The
     * summary would be encapsulated in an instance of [Result].
     */
    suspend fun fetchGeneratedSummaryForWeatherDetails(currentWeatherDetails: CurrentWeatherDetails): Result<String>
}
