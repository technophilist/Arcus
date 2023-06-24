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
     * Contains constants that represent the different hourly forecast items available in the API.
     */
    enum class HourlyForecastItems(private val valueToBeSentToTheApi: String) {
        PRECIPITATION_PROBABILITIES("precipitation_probability"),
        WEATHER_CODE("weathercode"),
        TEMPERATURE("temperature_2m"),
        ALL("$WEATHER_CODE,$PRECIPITATION_PROBABILITIES,$TEMPERATURE");

        override fun toString(): String = valueToBeSentToTheApi
    }

    /**
     * Contains supported return type time formats by the API.
     */
    enum class TimeFormats(private val valueToBeSentToTheApi: String) {
        UNIX_EPOCH_TIME_IN_SECONDS("unixtime"),
        ISO_8601("iso8601");

        override fun toString(): String = valueToBeSentToTheApi
    }
}