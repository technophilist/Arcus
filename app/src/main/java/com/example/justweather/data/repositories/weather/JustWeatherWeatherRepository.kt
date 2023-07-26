package com.example.justweather.data.repositories.weather

import com.example.justweather.data.getBodyOrThrowException
import com.example.justweather.data.local.weather.JustWeatherDatabaseDao
import com.example.justweather.data.local.weather.SavedWeatherLocationEntity
import com.example.justweather.data.remote.weather.WeatherClient
import com.example.justweather.data.remote.weather.models.toCurrentWeatherDetails
import com.example.justweather.data.remote.weather.models.toHourlyForecasts
import com.example.justweather.data.remote.weather.models.toPrecipitationProbabilities
import com.example.justweather.data.remote.weather.models.toSingleWeatherDetailList
import com.example.justweather.domain.models.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

/**
 * The default concrete implementation of [WeatherRepository].
 */
class JustWeatherWeatherRepository @Inject constructor(
    private val weatherClient: WeatherClient,
    private val justWeatherDatabaseDao: JustWeatherDatabaseDao
) : WeatherRepository {

    override suspend fun fetchWeatherForLocation(
        nameOfLocation: String,
        latitude: String,
        longitude: String
    ): Result<CurrentWeatherDetails> = try {
        val response = weatherClient.getWeatherForCoordinates(
            latitude = latitude,
            longitude = longitude
        )
        Result.success(response.getBodyOrThrowException().toCurrentWeatherDetails(nameOfLocation))
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override fun fetchWeatherStreamForPreviouslySavedLocations(): Flow<List<BriefWeatherDetails>> {
        return justWeatherDatabaseDao.getAllWeatherEntitiesMarkedAsNotDeleted()
            .map { savedWeatherLocationEntities ->
                savedWeatherLocationEntities.map {
                    fetchWeatherForLocation(
                        nameOfLocation = it.nameOfLocation,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
            }.map { currentWeatherDetailResults ->
                currentWeatherDetailResults.mapNotNull { currentWeatherDetailResult ->
                    currentWeatherDetailResult.getOrNull()?.toBriefWeatherDetails()
                }
            }
    }

    override fun getNamesOfPreviouslySavedLocationsListStream(): Flow<List<String>> {
        return justWeatherDatabaseDao.getAllWeatherEntitiesMarkedAsNotDeleted()
            .map { savedLocations -> savedLocations.map { it.nameOfLocation } }
    }

    override suspend fun saveWeatherLocation(
        nameOfLocation: String,
        latitude: String,
        longitude: String
    ) {
        val savedWeatherEntity = SavedWeatherLocationEntity(
            nameOfLocation = nameOfLocation,
            latitude = latitude,
            longitude = longitude
        )
        justWeatherDatabaseDao.addSavedWeatherEntity(savedWeatherEntity)
    }

    override suspend fun deleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails) {
        val savedLocationEntity = briefWeatherLocation.toSavedWeatherLocationEntity()
        justWeatherDatabaseDao.markWeatherEntityAsDeleted(savedLocationEntity.nameOfLocation)
    }

    override suspend fun permanentlyDeleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails) {
        briefWeatherLocation.toSavedWeatherLocationEntity().run {
            justWeatherDatabaseDao.deleteSavedWeatherEntity(this)
        }
    }

    override suspend fun fetchHourlyPrecipitationProbabilities(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate>
    ): Result<List<PrecipitationProbability>> = try {
        val precipitationProbabilities = weatherClient.getHourlyForecast(
            latitude = latitude,
            longitude = longitude,
            startDate = dateRange.start,
            endDate = dateRange.endInclusive
        ).getBodyOrThrowException().toPrecipitationProbabilities()
        Result.success(precipitationProbabilities)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override suspend fun fetchHourlyForecasts(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate>
    ): Result<List<HourlyForecast>> = try {
        val hourlyForecasts = weatherClient.getHourlyForecast(
            latitude = latitude,
            longitude = longitude,
            startDate = dateRange.start,
            endDate = dateRange.endInclusive
        ).getBodyOrThrowException().toHourlyForecasts()
        Result.success(hourlyForecasts)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override suspend fun fetchAdditionalWeatherInfoItemsListForCurrentDay(
        latitude: String,
        longitude: String,
    ): Result<List<SingleWeatherDetail>> = try {
        val additionalWeatherInfoItemsList = weatherClient.getAdditionalDailyForecastVariables(
            latitude = latitude,
            longitude = longitude,
            startDate = LocalDate.now(),
            endDate = LocalDate.now()
        ).getBodyOrThrowException().toSingleWeatherDetailList()
        Result.success(additionalWeatherInfoItemsList)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override suspend fun tryRestoringDeletedWeatherLocation(nameOfLocation: String) {
        justWeatherDatabaseDao.markWeatherEntityAsUnDeleted(nameOfLocation)
    }
}