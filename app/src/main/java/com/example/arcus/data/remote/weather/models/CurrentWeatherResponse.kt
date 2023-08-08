package com.example.arcus.data.remote.weather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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


