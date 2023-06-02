package com.example.justweather.domain.models

import androidx.annotation.DrawableRes
import com.example.justweather.R

/**
 * A data class that holds brief weather details of a particular location. It can be seen as data
 * class that contains a subset of all the properties of the [WeatherDetails] class.
 *
 * @param nameOfLocation The name of the location.
 * @param currentTemperature The current temperature (without superscript).
 * @param shortDescription A short description of the weather.
 * @param shortDescriptionIcon An icon representing the weather.
 */
data class BriefWeatherDetails(
    val nameOfLocation: String,
    val currentTemperature: String,
    val shortDescription: String,
    @DrawableRes val shortDescriptionIcon: Int,
) {
    companion object {
        /**
         * An instance of [BriefWeatherDetails] with all string properties set to "--".
         */
        val EmptyBriefWeatherDetails = BriefWeatherDetails(
            nameOfLocation = "- -",
            currentTemperature = "- -",
            shortDescription = "- -",
            shortDescriptionIcon = R.drawable.ic_scattered_clouds // todo
        )
    }
}


