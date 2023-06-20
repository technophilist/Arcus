package com.example.justweather.domain.models

import androidx.annotation.DrawableRes
import com.example.justweather.data.remote.weather.models.CurrentWeatherResponse

/**
 * A data class that holds the current weather details for a specific location.
 */
data class CurrentWeatherDetails(
    val nameOfLocation: String,
    val temperature: String,
    val isDay: Int,
    @DrawableRes val iconResId: Int,
    @DrawableRes val imageResId: Int,
    val latitude: String,
    val longitude: String,
)

/**
 * Used to convert an instance of [CurrentWeatherDetails] to an instance of [toBriefWeatherDetails].
 */
fun CurrentWeatherDetails.toBriefWeatherDetails(): BriefWeatherDetails = BriefWeatherDetails(
    nameOfLocation = nameOfLocation,
    currentTemperature = temperature,
    shortDescription = "N/A", //todo
    shortDescriptionIcon = iconResId,
    latitude = latitude,
    longitude = longitude
)
