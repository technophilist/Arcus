package com.example.justweather.domain.models

import androidx.annotation.DrawableRes

/**
 * A data class that holds the current weather details for a specific location.
 */
data class CurrentWeatherDetails(
    val nameOfLocation:String,
    val temperature: String,
    val isDay: Int,
    @DrawableRes val iconResId: Int,
    @DrawableRes val imageResId: Int,
    val latitude: String,
    val longitude: String,
)
