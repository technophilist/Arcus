package com.example.justweather.data.remote.weather

/**
 * This object contains constants used by the [WeatherClient].
 */
object WeatherClientConstants {
    /**
     * The base URL of the weather API.
     */
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    /**
     * The endpoints of the weather API.
     */
    object EndPoints {
        const val GET_WEATHER_ENDPOINT = "weather"
    }

    /**
     * Used to configure the units returned by the weather API.
     */
    object Units {
        const val CELSIUS = "metric"
        const val FAHRENHEIT = "imperial"
    }
}