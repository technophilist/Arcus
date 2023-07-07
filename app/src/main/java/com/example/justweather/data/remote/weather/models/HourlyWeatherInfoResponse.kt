package com.example.justweather.data.remote.weather.models

import com.example.justweather.domain.models.HourlyForecast
import com.example.justweather.domain.models.PrecipitationProbability
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * A data class representing the response of hourly weather information.
 * @property hourlyForecast The hourly forecast data.
 */
@JsonClass(generateAdapter = true)
data class HourlyWeatherInfoResponse(
    val latitude: String,
    val longitude: String,
    @Json(name = "hourly") val hourlyForecast: HourlyForecast
) {
    /**
     * A data class representing the hourly forecast data.
     * @property timestamps The list of timestamps representing the hours.
     * @property precipitationProbabilityPercentages A list of precipitation probability percentages
     * for the [timestamps].
     * @property weatherCodes A list of weather codes for the [timestamps].
     * @property temperatureForecasts A list of forecasted temperatures for the [timestamps].
     */
    @JsonClass(generateAdapter = true)
    data class HourlyForecast(
        @Json(name = "time") val timestamps: List<String>,
        @Json(name = "precipitation_probability") val precipitationProbabilityPercentages: List<Int> = emptyList(),
        @Json(name = "weathercode") val weatherCodes: List<Int> = emptyList(),
        @Json(name = "temperature_2m") val temperatureForecasts: List<Float> = emptyList()
    )
}


/**
 * Used to convert an instance of [HourlyWeatherInfoResponse] to a list of [HourlyForecast]'s.
 */
fun HourlyWeatherInfoResponse.toHourlyForecasts(): List<HourlyForecast> = hourlyForecast.run {
    val hourlyForecasts = mutableListOf<HourlyForecast>()
    for (i in timestamps.indices) {
        val epochSeconds = timestamps[i].toLong()
        val correspondingLocalTime =
            LocalTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault())
        val weatherIconResId = getWeatherIconResForCode(
            weatherCode = weatherCodes[i],
            isDay = correspondingLocalTime.hour < 19
        )
        val hourlyForecast = HourlyForecast(
            hour = correspondingLocalTime.formatTo12HourInt(),
            isAM = correspondingLocalTime.isAM,
            weatherIconResId = weatherIconResId,
            temperature = temperatureForecasts[i].roundToInt()
        )
        hourlyForecasts.add(hourlyForecast)
    }
    return@run hourlyForecasts
}

/**
 * Used to convert an instance of [HourlyWeatherInfoResponse] to a list of
 * PrecipitationProbabilities.
 */
fun HourlyWeatherInfoResponse.toPrecipitationProbabilities(): List<PrecipitationProbability> =
    hourlyForecast.run {
        val probabilitiesList = mutableListOf<PrecipitationProbability>()
        for (i in timestamps.indices) {
            val epochSeconds = timestamps[i].toLong()
            val correspondingLocalDateTime = LocalDateTime
                .ofInstant(
                    Instant.ofEpochSecond(epochSeconds),
                    ZoneId.systemDefault()
                )

            val precipitationProbability = PrecipitationProbability(
                dateTime = correspondingLocalDateTime,
                probabilityPercentage = precipitationProbabilityPercentages[i],
                latitude = latitude,
                longitude = longitude
            )
            probabilitiesList.add(precipitationProbability)
        }
        return@run probabilitiesList
    }

/**
 * Returns an [Int] representing the hour of the [LocalTime], in 12 hour format, removing any
 * leading zeros.
 */
private fun LocalTime.formatTo12HourInt(): Int {
    val hourOfLocalTime =
        format(DateTimeFormatter.ofPattern("hh")) // the hour in 12-hour format (which includes leading zeros)
    return if (hourOfLocalTime.first() == '0') hourOfLocalTime.last().digitToInt()
    else hourOfLocalTime.toInt()
}

/**
 * Returns true if this [LocalTime] is in the AM (before noon), false otherwise.
 */
private val LocalTime.isAM get() = hour < 12