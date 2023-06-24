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
     * Different temperature units supported by the API.
     */
    enum class TemperatureUnits(private val valueToBeSentToTheApi: String) {
        CELSIUS("celsius"),
        FAHRENHEIT("fahrenheit");

        override fun toString(): String = valueToBeSentToTheApi

    }

    /**
     * Different wind speed units supported by the API.
     */
    enum class WindSpeedUnit(private val valueToBeSentToTheApi: String) {
        KILOMETERS_PER_HOUR("kmh"),
        MILES_PER_HOUR("mph");

        override fun toString(): String = valueToBeSentToTheApi
    }

    /**
     * Different precipitation units supported by the API.
     */
    enum class PrecipitationUnit(private val valueToBeSentToTheApi: String) {
        MILLIMETERS("mm"),
        INCHES("inch");

        override fun toString(): String = valueToBeSentToTheApi

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