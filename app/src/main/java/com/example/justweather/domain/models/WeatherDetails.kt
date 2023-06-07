package com.example.justweather.domain.models

import androidx.annotation.DrawableRes
import com.example.justweather.R

/**
 * A data class that holds weather details for a particular location.
 *
 * @param nameOfLocation The name of the location.
 * @param temperature The current temperature of the location.
 * @param wind The current wind conditions of the location.
 * @param weatherCondition The current weather condition of the location.
 * @param humidity The current humidity of the location.
 * @param pressure The current wind pressure in the location.
 */
data class WeatherDetails(
    val nameOfLocation: String,
    val temperature: Temperature,
    val wind: Wind,
    val weatherCondition: WeatherCondition,
    val humidity: String,
    val pressure: String,
    val latitude: String,
    val longitude: String
) {
    /**
     * A data class that holds the current temperature details of a location.
     *
     * @param currentTemp The current temperature of the location.
     * @param minTemperature The minimum temperature of the location.
     * @param maxTemperature The maximum temperature of the location.
     */
    data class Temperature(
        val currentTemp: String,
        val minTemperature: String,
        val maxTemperature: String,
    )

    /**
     * A data class that holds the current wind condition details of a location.
     *
     * @param speed The speed of the wind at the location.
     * @param direction The direction of the wind at the location.
     */
    data class Wind(val speed: String, val direction: String)

    /**
     * A data class that holds the current weather condition details of a location.
     *
     * @param oneWordDescription A one-word description of the weather condition at the location.
     * @param currentWeatherConditionIcon An integer representing the drawable resource ID of the
     * icon representing the current weather condition at the location.
     */
    data class WeatherCondition(
        val oneWordDescription: String,
        @DrawableRes val currentWeatherConditionIcon: Int,
    )

    companion object {
        /**
         * An instance of [WeatherDetails] with all string properties set to "--".
         */
        val EmptyWeatherDetails = WeatherDetails(
            nameOfLocation = "Loading",
            temperature = Temperature(
                currentTemp = "- -",
                minTemperature = "- -",
                maxTemperature = "- -"
            ),
            wind = Wind(
                speed = "- -",
                direction = "- -"
            ),
            weatherCondition = WeatherCondition(
                oneWordDescription = "- -",
                currentWeatherConditionIcon = R.drawable.ic_scattered_clouds
            ),
            humidity = "- -",
            pressure = "- -",
            latitude = "- -",
            longitude = "- -"
        )
    }
}

/**
 * Used to map an instance of [WeatherDetails] to an instance of [BriefWeatherDetails].
 */
fun WeatherDetails.toBriefWeatherDetails(): BriefWeatherDetails {
    return BriefWeatherDetails(
        nameOfLocation = this.nameOfLocation,
        currentTemperature = this.temperature.currentTemp,
        shortDescription = this.weatherCondition.oneWordDescription,
        shortDescriptionIcon = this.weatherCondition.currentWeatherConditionIcon,
        latitude = latitude,
        longitude = longitude
    )
}
