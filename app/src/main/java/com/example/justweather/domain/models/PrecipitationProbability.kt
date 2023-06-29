package com.example.justweather.domain.models

import androidx.annotation.FloatRange

/**
 * A data class that represents the precipitation probability.
 * Note: It's better to have the time as an instance of LocalDateTime. This will allow for additional
 * features such as accommodating for 12/24 hr time format preference of the user. Since, the main motive
 * of this app, is not internationalization/user specific customization, LocalDateTime is not used here.
 */
data class PrecipitationProbability(
    val hour: Int,
    @FloatRange(0.0, 1.0) val probability: Float
)

/**
 * Returns the precipitation probability as a percentage string.
 */
val PrecipitationProbability.probabilityPercentageString get() = "${probability * 100}%"
