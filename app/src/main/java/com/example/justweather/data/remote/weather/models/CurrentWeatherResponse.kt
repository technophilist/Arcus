package com.example.justweather.data.remote.weather.models

import com.example.justweather.R
import com.example.justweather.domain.models.WeatherDetails
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
        val temperature: String,
        @Json(name = "is_day") val isDay: Int,
        @Json(name = "weathercode") val weatherCode: Int,
    )
}

/**
 * Used to map an instance of [CurrentWeatherResponse] to an instance of [WeatherDetails]
 */
fun CurrentWeatherResponse.toWeatherDetails(nameOfLocation: String): WeatherDetails {
    return WeatherDetails(
        nameOfLocation = nameOfLocation,
        temperature = WeatherDetails.Temperature(
            currentTemp = currentWeather.temperature,
            minTemperature = "U/A", // todo
            maxTemperature = "U/A"// todo
        ),
        wind = WeatherDetails.Wind(
            speed = "U/A",// todo
            direction = "U/A"// todo
        ),
        weatherCondition = WeatherDetails.WeatherCondition(
            oneWordDescription = "",// todo
            currentWeatherConditionIcon = getWeatherIconResForCode(
                weatherCode = currentWeather.weatherCode,
                isDay = currentWeather.isDay == 1
            ),
        ),
        humidity = "U/A",// todo
        pressure = "U/A",// todo
        latitude = latitude,
        longitude = longitude
    )
}


/**
 * Returns an appropriate resource ID for the corresponding [weatherCode]
 */
private fun getWeatherIconResForCode(weatherCode: Int, isDay: Boolean): Int { // todo check if it works properly
    // mainly clear, partly cloudy, and overcast
    val cloudyWeatherCodes = setOf(1, 2, 3)
    // drizzle: Light, moderate, and dense intensity ,
    // Freezing Drizzle: Light and dense intensity,
    // Rain showers: Slight, moderate, and violent
    val rainyWeatherCodes = setOf(51, 53, 55, 56, 57, 80, 81, 82)
    //Rain: Slight, moderate and heavy intensity,
    // Freezing Rain: Light and heavy intensity,
    // Thunderstorm: Slight or moderate,Thunderstorm with slight and heavy hail
    val thunderstormsWeatherCodes = setOf(61, 63, 65, 66, 67, 95, 96, 99)
    // Snow fall: Slight, moderate, and heavy intensity, Snow grains, Snow showers slight and heavy
    val snowWeatherCodes = setOf(71, 73, 75, 77, 85, 86)
    //Fog and depositing rime fog
    val fogWeatherCodes = setOf(45, 48)
    // day icons
    if (isDay) {
        return when (weatherCode) {
            0 -> R.drawable.ic_day_clear // clear sky
            in cloudyWeatherCodes -> R.drawable.ic_day_few_clouds
            in rainyWeatherCodes -> R.drawable.ic_day_rain
            in thunderstormsWeatherCodes -> R.drawable.ic_day_thunderstorms
            in snowWeatherCodes -> R.drawable.ic_day_snow
            in fogWeatherCodes -> R.drawable.ic_mist
            else -> throw IllegalArgumentException("Unknown weatherCode $weatherCode")
        }
    }
    // night icons
    return when (weatherCode) {
        0 -> R.drawable.ic_night_clear // clear sky night
        in cloudyWeatherCodes -> R.drawable.ic_night_few_clouds // few clouds night, scattered clouds night, broken clouds night
        in rainyWeatherCodes -> R.drawable.ic_night_rain // shower rain night, rain night
        in thunderstormsWeatherCodes -> R.drawable.ic_night_thunderstorms // thunderstorm night
        in snowWeatherCodes -> R.drawable.ic_night_snow // snow night
        in fogWeatherCodes -> R.drawable.ic_mist // mist day/night
        else -> throw IllegalArgumentException("Unknown weatherCode $weatherCode")
    }
}

