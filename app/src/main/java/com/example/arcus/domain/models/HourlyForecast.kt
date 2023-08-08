package com.example.arcus.domain.models

import androidx.annotation.DrawableRes
import com.example.arcus.data.remote.weather.models.HourlyWeatherInfoResponse
import com.example.arcus.data.remote.weather.models.getWeatherIconResForCode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt

/**
 * A class that contains the forecasted temperature for a particular [dateTime].
 * @property dateTime an instance of [LocalDateTime] representing the corresponding date
 * and time for the forecasted weather.
 * @property weatherIconResId The resource ID of the weather icon.
 * @property temperature The forecasted temperature for the hour, rounded to an [Int].
 */
data class HourlyForecast(
    val dateTime: LocalDateTime,
    @DrawableRes val weatherIconResId: Int,
    val temperature: Int
)

/**
 * Used to convert an instance of [HourlyWeatherInfoResponse] to a list of [HourlyForecast]'s.
 */
fun HourlyWeatherInfoResponse.toHourlyForecasts(): List<HourlyForecast> {
    val hourlyForecastList = mutableListOf<HourlyForecast>()
    for (i in hourlyForecast.timestamps.indices) {
        val epochSeconds = hourlyForecast.timestamps[i].toLong()
        val correspondingLocalTime = LocalDateTime
            .ofInstant(
                Instant.ofEpochSecond(epochSeconds),
                ZoneId.systemDefault()
            )
        val weatherIconResId = getWeatherIconResForCode(
            weatherCode = hourlyForecast.weatherCodes[i],
            isDay = correspondingLocalTime.hour < 19
        )
        val hourlyForecast = HourlyForecast(
            dateTime = correspondingLocalTime,
            weatherIconResId = weatherIconResId,
            temperature = hourlyForecast.temperatureForecasts[i].roundToInt()
        )
        hourlyForecastList.add(hourlyForecast)
    }
    return hourlyForecastList
}

