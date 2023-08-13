package com.example.arcus.data.repositories.weather

import com.example.arcus.domain.models.weather.HourlyForecast
import com.example.arcus.domain.models.weather.PrecipitationProbability
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Used to fetch a list of [PrecipitationProbability], encapsulated in an instance of type [Result]
 * for the next 24 hours, from the current time, for the location with the provided [latitude]
 * and [longitude].
 */
suspend fun WeatherRepository.fetchPrecipitationProbabilitiesForNext24hours(
    latitude: String,
    longitude: String,
): Result<List<PrecipitationProbability>> {
    return try {
        val probabilitiesForNext24hours = this.fetchHourlyPrecipitationProbabilities(
            latitude = latitude,
            longitude = longitude,
            dateRange = LocalDate.now()..LocalDate.now().plusDays(1)
        ).getOrThrow().filter {
            val isSameDay = it.dateTime == LocalDateTime.now()
            if (isSameDay) it.dateTime.toLocalTime() >= LocalTime.now()
            else it.dateTime > LocalDateTime.now()
        }.take(24)
        Result.success(probabilitiesForNext24hours)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }
}

/**
 * Used to fetch a list of [HourlyForecast]s, encapsulated in an instance of type [Result]
 * for the next 24 hours, from the current time, for the location with the provided
 * [latitude] and [longitude].
 */
suspend fun WeatherRepository.fetchHourlyForecastsForNext24Hours(
    latitude: String,
    longitude: String,
): Result<List<HourlyForecast>> {
    return try {
        val hourlyForecastsForNext24Hours = this.fetchHourlyForecasts(
            latitude = latitude,
            longitude = longitude,
            dateRange = LocalDate.now()..LocalDate.now().plusDays(1)
        ).getOrThrow().filter {
            val isSameDay = it.dateTime == LocalDateTime.now()
            if (isSameDay) it.dateTime.toLocalTime() >= LocalTime.now()
            else it.dateTime > LocalDateTime.now()
        }.take(24)
        Result.success(hourlyForecastsForNext24Hours)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }
}