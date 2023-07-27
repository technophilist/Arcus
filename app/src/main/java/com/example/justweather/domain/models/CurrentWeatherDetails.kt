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
