package com.example.arcus.domain.models

import androidx.annotation.DrawableRes
import java.time.LocalDateTime

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
