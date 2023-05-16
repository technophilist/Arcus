package com.example.justweather.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * todo stopship docs - @JsonClass makes use of compile code gen instead of depending on reflextion +
 * generates KOTLIN CODE USING KOTLIN POET
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
