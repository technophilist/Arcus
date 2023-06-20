package com.example.justweather.data.remote.weather

import com.example.justweather.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

/**
 * This object contains constants used by the [WeatherClient].
 */
object WeatherClientConstants {
    /**
     * The base URL of the weather API.
     */
    const val BASE_URL = "https://api.open-meteo.com/v1/"

    /**
     * The endpoints of the weather API.
     */
    object EndPoints {
        const val GET_WEATHER_ENDPOINT = "forecast"
    }

    /**
     * Used to configure the units returned by the weather API.
     */
    object Units {
        object TemperatureUnits {
            const val CELSIUS = "celsius"
            const val FAHRENHEIT = "fahrenheit"
        }

        object WindSpeedUnit {
            const val KILOMETERS_PER_HOUR = "kmh"
            const val MILES_PER_HOUR = "mph"
        }

        object PrecipitationUnit {
            const val MILLIMETERS = "mm"
            const val INCHES = "inch"
        }
    }
}