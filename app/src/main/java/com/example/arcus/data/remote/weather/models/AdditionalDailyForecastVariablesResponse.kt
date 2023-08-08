package com.example.arcus.data.remote.weather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A response object that contains [AdditionalForecastedVariables] for a specific location.
 */
@JsonClass(generateAdapter = true)
data class AdditionalDailyForecastVariablesResponse(
    @Json(name = "timezone") val timezone: String,
    @Json(name = "daily") val additionalForecastedVariables: AdditionalForecastedVariables
) {
    /**
     * Data class containing additional forecasted variables.
     *
     * @property minTemperatureForTheDay The minimum temperature for the day.
     * @property maxTemperatureForTheDay The maximum temperature for the day.
     * @property maxApparentTemperature The maximum apparent temperature.
     * @property minApparentTemperature The minimum apparent temperature.
     * @property sunrise The sunrise time.
     * @property sunset The sunset time.
     * @property maxUvIndex The maximum UV index.
     * @property dominantWindDirection The dominant wind direction.
     * @property windSpeed The wind speed.
     */
    @JsonClass(generateAdapter = true)
    data class AdditionalForecastedVariables(
        @Json(name = "temperature_2m_min") val minTemperatureForTheDay: List<Double>,
        @Json(name = "temperature_2m_max") val maxTemperatureForTheDay: List<Double>,
        @Json(name = "apparent_temperature_max") val maxApparentTemperature: List<Double>,
        @Json(name = "apparent_temperature_min") val minApparentTemperature: List<Double>,
        @Json(name = "sunrise") val sunrise: List<Long>,
        @Json(name = "sunset") val sunset: List<Long>,
        @Json(name = "uv_index_max") val maxUvIndex: List<Double>,
        @Json(name = "winddirection_10m_dominant") val dominantWindDirection: List<Int>,
        @Json(name = "windspeed_10m_max") val windSpeed: List<Double>
    )
}