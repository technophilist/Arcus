package com.example.arcus.domain.models.weather

import androidx.annotation.DrawableRes
import com.example.arcus.data.remote.weather.models.CurrentWeatherResponse
import com.example.arcus.data.remote.weather.models.getWeatherIconResForCode
import com.example.arcus.data.remote.weather.models.getWeatherImageForCode
import com.example.arcus.domain.models.location.Coordinates
import kotlin.math.roundToInt

/**
 * A data class that holds the current weather details for a specific location.
 */
data class CurrentWeatherDetails(
    val nameOfLocation: String,
    val temperatureRoundedToInt: Int,
    val weatherCondition: String,
    val isDay: Int,
    @DrawableRes val iconResId: Int,
    @DrawableRes val imageResId: Int,
    val coordinates: Coordinates
) // todo make this class use the coordinates domain class

/**
 * Used to convert an instance of [CurrentWeatherDetails] to an instance of [BriefWeatherDetails].
 */
fun CurrentWeatherDetails.toBriefWeatherDetails(): BriefWeatherDetails = BriefWeatherDetails(
    nameOfLocation = nameOfLocation,
    currentTemperatureRoundedToInt = temperatureRoundedToInt,
    shortDescription = weatherCondition,
    shortDescriptionIcon = iconResId,
    coordinates = coordinates
)


/**
 * Used to map an instance of [CurrentWeatherResponse] to an instance of [CurrentWeatherDetails]
 */
fun CurrentWeatherResponse.toCurrentWeatherDetails(nameOfLocation: String): CurrentWeatherDetails =
    CurrentWeatherDetails(
        temperatureRoundedToInt = currentWeather.temperature.roundToInt(),
        nameOfLocation = nameOfLocation,
        weatherCondition = weatherCodeToDescriptionMap.getValue(currentWeather.weatherCode),
        isDay = currentWeather.isDay,
        iconResId = getWeatherIconResForCode(
            weatherCode = currentWeather.weatherCode,
            isDay = currentWeather.isDay == 1
        ),
        imageResId = getWeatherImageForCode(
            weatherCode = currentWeather.weatherCode,
            isDay = currentWeather.isDay == 1
        ),
        coordinates = Coordinates(
            latitude = latitude,
            longitude = longitude,
        )
    )


private val weatherCodeToDescriptionMap = mapOf(
    0 to "Clear sky",
    1 to "Mainly clear",
    2 to "Partly cloudy",
    3 to "Overcast",
    45 to "Fog",
    48 to "Depositing rime fog",
    51 to "Drizzle",
    53 to "Drizzle",
    55 to "Drizzle",
    56 to "Freezing drizzle",
    57 to "Freezing drizzle",
    61 to "Slight rain",
    63 to "Moderate rain",
    65 to "Heavy rain",
    66 to "Light freezing rain",
    67 to "Heavy freezing rain",
    71 to "Slight snow fall",
    73 to "Moderate snow fall",
    75 to "Heavy snow fall",
    77 to "Snow grains",
    80 to "Slight rain showers",
    81 to "Moderate rain showers",
    82 to "Violent rain showers",
    85 to "Slight snow showers",
    86 to "Heavy snow showers",
    95 to "Thunderstorms",
    96 to "Thunderstorms with slight hail",
    99 to "Thunderstorms with heavy hail",
)

