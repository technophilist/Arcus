package com.example.justweather.data.remote.weather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A data class representing the response of hourly weather information.
 * @property hourlyForecast The hourly forecast data.
 */
@JsonClass(generateAdapter = true)
data class HourlyWeatherInfoResponse(@Json(name = "hourly") val hourlyForecast: HourlyForecast) {
    /**
     * A data class representing the hourly forecast data.
     * @property timestamps The list of timestamps representing the hours.
     * @property precipitationProbabilities A list of precipitation probabilities for the [timestamps].
     * @property weatherCodes A list of weather codes for the [timestamps].
     * @property temperatureForecasts A list of forecasted temperatures for the [timestamps].
     */
    @JsonClass(generateAdapter = true)
    data class HourlyForecast(
        @Json(name = "time") val timestamps: List<String>,
        @Json(name = "precipitation_probability") val precipitationProbabilities: List<Double> = emptyList(),
        @Json(name = "weathercode") val weatherCodes: List<Int> = emptyList(),
        @Json(name = "temperature_2m") val temperatureForecasts : List<Float> = emptyList()
    )
}
