package com.example.arcus.domain.models.weather

import androidx.annotation.DrawableRes
import com.example.arcus.R
import com.example.arcus.data.remote.weather.models.AdditionalDailyForecastVariablesResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * A data class that holds information about a single weather detail such as wind speed, pressure,
 * humidity, etc.
 */
data class SingleWeatherDetail(
    val name: String,
    val value: String,
    @DrawableRes val iconResId: Int
)


/**
 * Used to convert an instance of [AdditionalDailyForecastVariablesResponse] to a list of
 * [SingleWeatherDetail] items.
 */
fun AdditionalDailyForecastVariablesResponse.toSingleWeatherDetailList(
    timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("hh : mm a")
): List<SingleWeatherDetail> = additionalForecastedVariables.toSingleWeatherDetailList(
    timezone = timezone,
    timeFormat = timeFormat
)

/**
 * Used to convert an instance of [AdditionalDailyForecastVariablesResponse.AdditionalForecastedVariables]
 * to a list of [SingleWeatherDetail] items. All timestamps will be converted based on the
 * [timezone] passed to this method.
 *
 * Note: The best way to do this mapping would be to allow the caller to customize the units.
 * Since this is just a hobby project, this detail has been left out.
 */
private fun AdditionalDailyForecastVariablesResponse.AdditionalForecastedVariables.toSingleWeatherDetailList(
    timezone: String,
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
        ZoneId.of(timezone)
    ).toLocalTime().format(timeFormat)
    val sunsetTimeString = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(sunset.first()), // todo: change type of property to long
        ZoneId.of(timezone)
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