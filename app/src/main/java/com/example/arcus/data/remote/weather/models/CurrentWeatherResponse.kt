package com.example.arcus.data.remote.weather.models

import com.example.arcus.domain.models.Coordinates
import com.example.arcus.domain.models.CurrentWeatherDetails
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.math.roundToInt

/**
 * A data class that models the response that would be received when a request to get the current
 * weather of a location is made.
 *
 * Note: All response classes are annotated with @JsonClass in order to make Moshi use code-gen.
 * Making Moshi use code-gen is useful for mainly two reasons:
 * 1) Moshi, by default uses reflection. This creates overhead during runtime. Using code-gen
 * makes moshi take advantage of kapt during compile time. This removes the overhead that is
 * associated with reflection.
 *
 * 2) Using code-gen makes moshi use Kotlin Poet under the hood. This makes moshi generate
 * actual Kotlin classes instead of the Java equivalents of Kotlin classes. This ensures that
 * Moshi fully understands Kotlin constructs. The best example of this, is nullability.
 * When Moshi doesn't use codegen, then non-null properties in Kotlin can be assigned null values!
 * This is because, Moshi generates the Java equivalent of the Kotlin data class.
 */
@JsonClass(generateAdapter = true)
data class CurrentWeatherResponse(
    @Json(name = "current_weather") val currentWeather: CurrentWeather,
    val latitude: String,
    val longitude: String
) {
    @JsonClass(generateAdapter = true)
    data class CurrentWeather(
        val temperature: Double,
        @Json(name = "is_day") val isDay: Int,
        @Json(name = "weathercode") val weatherCode: Int,
    )
}

/**
 * Used to map an instance of [CurrentWeatherResponse] to an instance of [CurrentWeatherDetails]
 */
fun CurrentWeatherResponse.toCurrentWeatherDetails(nameOfLocation: String): CurrentWeatherDetails =
    CurrentWeatherDetails(
        temperatureRoundedToInt = currentWeather.temperature.roundToInt(),
        nameOfLocation = nameOfLocation,
        weatherCondition = weatherCodeToDescriptionMap.getValue(currentWeather.weatherCode),
        isDay = currentWeather.isDay,
        iconResId = getWeatherIconResForCode(
            weatherCode = currentWeather.weatherCode,
            isDay = currentWeather.isDay == 1
        ),
        imageResId = getWeatherImageForCode(
            weatherCode = currentWeather.weatherCode,
            isDay = currentWeather.isDay == 1
        ),
        coordinates = Coordinates(
            latitude = latitude,
            longitude = longitude,
        )
    )


private val weatherCodeToDescriptionMap = mapOf(
    0 to "Clear sky",
    1 to "Mainly clear",
    2 to "Partly cloudy",
    3 to "Overcast",
    45 to "Fog",
    48 to "Depositing rime fog",
    51 to "Drizzle",
    53 to "Drizzle",
    55 to "Drizzle",
    56 to "Freezing drizzle",
    57 to "Freezing drizzle",
    61 to "Slight rain",
    63 to "Moderate rain",
    65 to "Heavy rain",
    66 to "Light freezing rain",
    67 to "Heavy freezing rain",
    71 to "Slight snow fall",
    73 to "Moderate snow fall",
    75 to "Heavy snow fall",
    77 to "Snow grains",
    80 to "Slight rain showers",
    81 to "Moderate rain showers",
    82 to "Violent rain showers",
    85 to "Slight snow showers",
    86 to "Heavy snow showers",
    95 to "Thunderstorms",
    96 to "Thunderstorms with slight hail",
    99 to "Thunderstorms with heavy hail",
)



