package com.example.justweather.data.remote.weather.models

import com.example.justweather.R
import com.example.justweather.domain.models.WeatherDetails
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
    val wind: Wind,
    @Json(name = "coord") val coordinates: Coordinates
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

    @JsonClass(generateAdapter = true)
    data class Coordinates(
        @Json(name = "lat") val latitude: String,
        @Json(name = "lon") val longitude: String
    )
}

/**
 * Maps an instance of [WeatherResponse] to an instance of [WeatherDetails].
 */
fun WeatherResponse.toWeatherDetails(): WeatherDetails = WeatherDetails(
    nameOfLocation = cityName,
    temperature = WeatherDetails.Temperature(
        currentTemp = additionalInfo.temperature,
        minTemperature = additionalInfo.currentMinTempAroundLocation,
        maxTemperature = additionalInfo.currentMaxTempAroundLocation
    ),
    wind = WeatherDetails.Wind(speed = wind.speed, direction = wind.directionInDegrees),
    weatherCondition = WeatherDetails.WeatherCondition(
        oneWordDescription = weather[0].description,
        currentWeatherConditionIcon = getWeatherIconResForId(weather[0].weatherIconId)
    ),
    humidity = additionalInfo.humidity,
    pressure = additionalInfo.pressure,
    latitude = coordinates.latitude,
    longitude = coordinates.longitude
)

/**
 * Returns an appropriate resource ID for the corresponding [weatherIconId]
 */
private fun getWeatherIconResForId(weatherIconId: String): Int = when (weatherIconId) {
    "01d" -> R.drawable.ic_day_clear // clear sky day
    "02d", "03d", "04d" -> R.drawable.ic_day_few_clouds // few clouds day, scattered clouds day, broken clouds day
    "09d", "10d" -> R.drawable.ic_day_rain // shower rain day, rain day
    "11d" -> R.drawable.ic_day_thunderstorms // thunderstorm day
    "13d" -> R.drawable.ic_day_snow // snow day
    "50d" -> R.drawable.ic_mist // mist day
    "01n" -> R.drawable.ic_night_clear // clear sky night
    "02n", "03n", "04n" -> R.drawable.ic_night_few_clouds // few clouds night, scattered clouds night, broken clouds night
    "09n", "10n" -> R.drawable.ic_night_rain // shower rain night, rain night
    "11n" -> R.drawable.ic_night_thunderstorms // thunderstorm night
    "13n" -> R.drawable.ic_night_snow // snow night
    else -> R.drawable.ic_mist // mist day/night
}


