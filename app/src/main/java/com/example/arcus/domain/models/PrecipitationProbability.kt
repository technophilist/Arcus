package com.example.arcus.domain.models

import com.example.arcus.data.remote.weather.models.HourlyWeatherInfoResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * A data class that represents the precipitation probability.
 */
data class PrecipitationProbability(
    val latitude: String,
    val longitude: String,
    val dateTime: LocalDateTime,
    val probabilityPercentage: Int
)

/**
 * Used to convert an instance of [HourlyWeatherInfoResponse] to a list of
 * PrecipitationProbabilities.
 */
fun HourlyWeatherInfoResponse.toPrecipitationProbabilities(): List<PrecipitationProbability> {
    val probabilitiesList = mutableListOf<PrecipitationProbability>()
    for (i in hourlyForecast.timestamps.indices) {
        val epochSeconds = hourlyForecast.timestamps[i].toLong()
        val correspondingLocalDateTime = LocalDateTime
            .ofInstant(
                Instant.ofEpochSecond(epochSeconds),
                ZoneId.systemDefault()
            )

        val precipitationProbability = PrecipitationProbability(
            dateTime = correspondingLocalDateTime,
            probabilityPercentage = hourlyForecast.precipitationProbabilityPercentages[i],
            latitude = latitude,
            longitude = longitude
        )
        probabilitiesList.add(precipitationProbability)
    }
    return probabilitiesList
}


