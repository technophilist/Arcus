package com.example.justweather.data.remote.weather

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
        const val GET_WEATHER_ENDPOINT = "forecast" // todo rename
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

    /**
     * Contains strings that represent different hourly forecast items available in the API.
     */
    object HourlyForecastItems {
        const val PRECIPITATION_PROBABILITIES = "precipitation_probability"
        const val WEATHER_CODE = "weathercode"
        const val TEMPERATURE = "temperature_2m"
        const val DEFAULT_ITEMS = "$WEATHER_CODE,$PRECIPITATION_PROBABILITIES,$TEMPERATURE"
    }

    /**
     * Used to configure the time format returned by the weather API.
     */
    object TimeFormats {
        const val UNIX_EPOCH_TIME_IN_SECONDS = "unixtime"
        const val ISO_8601 = "iso8601"
    }
}