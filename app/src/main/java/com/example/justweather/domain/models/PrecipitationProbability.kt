package com.example.justweather.domain.models

import androidx.annotation.FloatRange

/**
 * A data class that represents the precipitation probability.
 */
data class PrecipitationProbability(
    val hourString: String,
    @FloatRange(0.0, 1.0) val probability: Float
)

/**
 * Returns the precipitation probability as a percentage string.
 */
val PrecipitationProbability.probabilityPercentageString get() = "${probability * 100}%"
