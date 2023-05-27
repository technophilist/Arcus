package com.example.justweather.data.remote.weather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A data class that models the response that would be received when a request to get the
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
data class WeatherResponse(
    @Json(name = "id") val cityId: Int,
    @Json(name = "name") val cityName: String,
    @Json(name = "main") val additionalInfo: AdditionalInfo,
    val weather: List<Weather>,
    val wind: Wind
) {
    @JsonClass(generateAdapter = true)
    data class AdditionalInfo(
        @Json(name = "feels_like") val feelsLike: String,
        val humidity: String,
        val pressure: String,
        @Json(name = "temp") val temperature: String,
        @Json(name = "temp_max") val currentMaxTempAroundLocation: String, // maximum observed temp around region
        @Json(name = "temp_min") val currentMinTempAroundLocation: String // minimum observed temp around region
    )

    @JsonClass(generateAdapter = true)
    data class Weather(
        @Json(name = "id") val weatherConditionId: Int,
        @Json(name = "main") val weatherCondition: String,
        val description: String,
        @Json(name = "icon") val weatherIconId: String
    )

    @JsonClass(generateAdapter = true)
    data class Wind(
        @Json(name = "deg") val directionInDegrees: String,
        val speed: String
    )
}
