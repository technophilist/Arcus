package com.example.arcus.domain.models

import androidx.annotation.DrawableRes

/**
 * A data class that holds information about a single weather detail such as wind speed, pressure,
 * humidity, etc.
 */
data class SingleWeatherDetail(
    val name: String,
    val value: String,
    @DrawableRes val iconResId: Int
)
