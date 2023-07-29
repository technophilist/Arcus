package com.example.justweather.data.remote.weather.models

import com.example.justweather.R
import com.example.justweather.domain.models.SingleWeatherDetail
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * A response object that contains [AdditionalForecastedVariables] for a specific location.
 */
@JsonClass(generateAdapter = true)
data class AdditionalDailyForecastVariablesResponse(
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


/**
 * Used to convert an instance of [AdditionalDailyForecastVariablesResponse] to a list of
 * [SingleWeatherDetail] items.
 */
fun AdditionalDailyForecastVariablesResponse.toSingleWeatherDetailList(
    timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("hh : mm a")
): List<SingleWeatherDetail> = additionalForecastedVariables.toSingleWeatherDetailList(timeFormat)

/**
 * Used to convert an instance of [AdditionalDailyForecastVariablesResponse.AdditionalForecastedVariables]
 * to a list of [SingleWeatherDetail] items.
 *
 * Note: The best way to do this mapping would be to allow the caller to customize the units.
 * Since this is just a hobby project, this detail has been left out.
 */
private fun AdditionalDailyForecastVariablesResponse.AdditionalForecastedVariables.toSingleWeatherDetailList(
    timeFormat: DateTimeFormatter
): List<SingleWeatherDetail> {
    require(minTemperatureForTheDay.size == 1) {
        "This mapper method will only consider the first value of each list" +
                "Make sure you request the details for only one day."
    }
    val apparentTemperature =
        (minApparentTemperature.first().roundToInt() + maxApparentTemperature.first()
            .roundToInt()) / 2
    val sunriseTimeString = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(sunrise.first()), // todo: change type of property to long
        ZoneId.systemDefault()
    ).toLocalTime().format(timeFormat)
    val sunsetTimeString = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(sunset.first()), // todo: change type of property to long
        ZoneId.systemDefault()
    ).format(timeFormat)
    // Since these single weather detail items are displayed in smaller cards, the default mac OS
    // 'º' character is used instead of the other degree superscript used in other parts of the app.
    return listOf(
        SingleWeatherDetail(
            name = "Min Temp",
            value = "${minTemperatureForTheDay.first().roundToInt()}º",
            iconResId = R.drawable.ic_thermometer
        ),
        SingleWeatherDetail(
            name = "Max Temp",
            value = "${maxTemperatureForTheDay.first().roundToInt()}º",
            iconResId = R.drawable.ic_thermometer
        ),
        SingleWeatherDetail(
            name = "Sunrise",
            value = sunriseTimeString,
            iconResId = R.drawable.ic_sunrise
        ),
        SingleWeatherDetail(
            name = "Sunset",
            value = sunsetTimeString,
            iconResId = R.drawable.ic_sunset
        ),
        SingleWeatherDetail(
            name = "Feels Like",
            value = "${apparentTemperature}º",
            iconResId = R.drawable.ic_thermometer //
        ),
        SingleWeatherDetail(
            name = "Max UV Index",
            value = maxUvIndex.first().toString(),
            iconResId = R.drawable.ic_uv_index
        ),
        SingleWeatherDetail(
            name = "Wind Direction",
            value = "${dominantWindDirection.first()}º",
            iconResId = R.drawable.ic_wind_direction
        ),
        SingleWeatherDetail(
            name = "Wind Speed",
            value = "${windSpeed.first()} Km/h",
            iconResId = R.drawable.ic_wind
        )
    )
}
