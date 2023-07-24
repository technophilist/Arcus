package com.example.justweather.domain.models

import androidx.annotation.DrawableRes

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
    val latitude: String,
    val longitude: String,
)

/**
 * Used to convert an instance of [CurrentWeatherDetails] to an instance of [BriefWeatherDetails].
 */
fun CurrentWeatherDetails.toBriefWeatherDetails(): BriefWeatherDetails = BriefWeatherDetails(
    nameOfLocation = nameOfLocation,
    currentTemperatureRoundedToInt = temperatureRoundedToInt,
    shortDescription = weatherCondition,
    shortDescriptionIcon = iconResId,
    latitude = latitude,
    longitude = longitude
)
