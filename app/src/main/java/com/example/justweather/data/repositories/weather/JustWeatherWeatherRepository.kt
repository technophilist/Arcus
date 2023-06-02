package com.example.justweather.data.repositories.weather

import com.example.justweather.BuildConfig
import com.example.justweather.data.getBodyOrThrowException
import com.example.justweather.data.local.weather.JustWeatherDatabaseDao
import com.example.justweather.data.remote.weather.WeatherClient
import com.example.justweather.data.remote.weather.WeatherClientConstants
import com.example.justweather.data.remote.weather.models.toWeatherDetails
import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.WeatherDetails
import com.example.justweather.domain.models.toBriefWeatherDetails
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * The default concrete implementation of [WeatherRepository].
 */
class JustWeatherWeatherRepository @Inject constructor(
    private val weatherClient: WeatherClient,
    private val justWeatherDatabaseDao: JustWeatherDatabaseDao
) : WeatherRepository {

    override suspend fun fetchWeatherForLocation(
        latitude: String,
        longitude: String
    ): Result<WeatherDetails> = try {
        val response = weatherClient.getWeatherForCoordinates(
            latitude = latitude,
            longitude = longitude,
            units = WeatherClientConstants.Units.CELSIUS // todo
        )
        Result.success(response.getBodyOrThrowException().toWeatherDetails())
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    @OptIn(FlowPreview::class)
    override fun getWeatherStreamForPreviouslySavedLocations(): Flow<BriefWeatherDetails> {
        return justWeatherDatabaseDao.getAllSavedWeatherEntities()
            .flatMapConcat { it.asFlow() }
            .map {
                fetchWeatherForLocation(latitude = it.latitude, longitude = it.longitude)
            }.map {
                it.getOrNull()
                    ?.toBriefWeatherDetails() ?: BriefWeatherDetails.EmptyBriefWeatherDetails
            }
    }
}