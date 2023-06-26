package com.example.justweather.data.remote.weather.models

import com.example.justweather.R

// mainly clear, partly cloudy, and overcast
private val cloudyWeatherCodes = setOf(1, 2, 3)

// drizzle: Light, moderate, and dense intensity ,
// Freezing Drizzle: Light and dense intensity,
// Rain showers: Slight, moderate, and violent
private val rainyWeatherCodes = setOf(51, 53, 55, 56, 57, 80, 81, 82)

//Rain: Slight, moderate and heavy intensity,
// Freezing Rain: Light and heavy intensity,
// Thunderstorm: Slight or moderate,Thunderstorm with slight and heavy hail
private val thunderstormsWeatherCodes = setOf(61, 63, 65, 66, 67, 95, 96, 99)

// Snow fall: Slight, moderate, and heavy intensity, Snow grains, Snow showers slight and heavy
private val snowWeatherCodes = setOf(71, 73, 75, 77, 85, 86)

//Fog and depositing rime fog
private val fogWeatherCodes = setOf(45, 48)

fun getWeatherImageForCode(weatherCode: Int, isDay: Boolean): Int {
    // day icons
    if (isDay) {
        return when (weatherCode) {
            0 -> R.drawable.img_day_clear
            in cloudyWeatherCodes -> R.drawable.img_day_cloudy
            in rainyWeatherCodes -> R.drawable.img_day_rain
            in thunderstormsWeatherCodes -> R.drawable.img_day_rain
            in snowWeatherCodes -> R.drawable.img_day_snow
            in fogWeatherCodes -> R.drawable.img_day_fog
            else -> throw IllegalArgumentException("Unknown weatherCode $weatherCode")
        }
    }
    // night icons
    return when (weatherCode) {
        0 -> R.drawable.img_night_clear
        in cloudyWeatherCodes -> R.drawable.img_night_cloudy
        in rainyWeatherCodes -> R.drawable.img_night_rain
        in thunderstormsWeatherCodes -> R.drawable.img_night_rain
        in snowWeatherCodes -> R.drawable.img_night_snow
        in fogWeatherCodes -> R.drawable.img_night_fog
        else -> throw IllegalArgumentException("Unknown weatherCode $weatherCode")
    }
}

/**
 * Returns an appropriate resource ID for the corresponding [weatherCode]
 */
fun getWeatherIconResForCode(
    weatherCode: Int,
    isDay: Boolean
): Int { // todo check if it works properly
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