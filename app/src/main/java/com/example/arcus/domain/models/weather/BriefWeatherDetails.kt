package com.example.arcus.domain.models.weather

import androidx.annotation.DrawableRes
import com.example.arcus.data.local.weather.SavedWeatherLocationEntity
import com.example.arcus.domain.models.location.Coordinates

/**
 * A data class that holds brief weather details of a particular location.
 *
 * @param nameOfLocation The name of the location.
 * @param currentTemperatureRoundedToInt The current temperature (without superscript).
 * @param shortDescription A short description of the weather.
 * @param shortDescriptionIcon An icon representing the weather.
 * @param coordinates The [Coordinates] of the location.
 */
data class BriefWeatherDetails(
    val nameOfLocation: String,
    val currentTemperatureRoundedToInt: Int,
    val shortDescription: String,
    @DrawableRes val shortDescriptionIcon: Int,
    val coordinates: Coordinates
)

/**
 * Used to map an instance of [BriefWeatherDetails] to an instance of [SavedWeatherLocationEntity].
 */
fun BriefWeatherDetails.toSavedWeatherLocationEntity(): SavedWeatherLocationEntity =
    SavedWeatherLocationEntity(
        nameOfLocation = this.nameOfLocation,
        latitude = this.coordinates.latitude,
        longitude = this.coordinates.longitude
    )

