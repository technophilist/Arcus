package com.example.justweather.domain.models

import androidx.annotation.DrawableRes
import com.example.justweather.R
import com.example.justweather.data.local.weather.SavedWeatherLocationEntity
import java.util.*

/**
 * A data class that holds brief weather details of a particular location. It can be seen as data
 * class that contains a subset of all the properties of the [WeatherDetails] class.
 *
 * @param nameOfLocation The name of the location.
 * @param currentTemperature The current temperature (without superscript).
 * @param shortDescription A short description of the weather.
 * @param shortDescriptionIcon An icon representing the weather.
 * @param latitude The latitude of the location.
 * @param longitude The longitude of the location.
 */
data class BriefWeatherDetails(
    val nameOfLocation: String,
    val currentTemperature: String,
    val shortDescription: String,
    @DrawableRes val shortDescriptionIcon: Int,
    val latitude: String,
    val longitude: String
) {
    companion object {
        /**
         * An instance of [BriefWeatherDetails] with all string properties set to "--".
         */
        val EmptyBriefWeatherDetails = BriefWeatherDetails(
            nameOfLocation = "- -",
            currentTemperature = "- -",
            shortDescription = "- -",
            shortDescriptionIcon = R.drawable.ic_scattered_clouds, // todo
            latitude = "- -",
            longitude = "- -"
        )
    }
}

/**
 * Used to map an instance of [BriefWeatherDetails] to an instance of [SavedWeatherLocationEntity].
 */
fun BriefWeatherDetails.toSavedWeatherLocationEntity(): SavedWeatherLocationEntity =
    SavedWeatherLocationEntity(
        nameOfLocation = this.nameOfLocation,
        latitude = this.latitude,
        longitude = this.longitude
    )

