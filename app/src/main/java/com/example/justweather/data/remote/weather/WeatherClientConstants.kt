package com.example.justweather.data.remote.weather

import java.time.ZoneId

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
     * Contains constants that represent the different daily forecast items available in the API.
     */
    enum class DailyForecastItems(private val valueToBeSentToTheApi: String) {
        MAX_TEMPERATURE("temperature_2m_max"),
        MIN_TEMPERATURE("temperature_2m_min"),
        MAX_APPARENT_TEMPERATURE("apparent_temperature_max"),
        MIN_APPARENT_TEMPERATURE("apparent_temperature_min"),
        SUNRISE("sunrise"),
        SUNSET("sunset"),
        UV_INDEX("uv_index_max"),
        WIND_SPEED("windspeed_10m_max"),
        WIND_DIRECTION("winddirection_10m_dominant"),
        ALL(
            "${MAX_TEMPERATURE},$MIN_TEMPERATURE," +
                    "$MAX_APPARENT_TEMPERATURE,$MIN_APPARENT_TEMPERATURE," +
                    "$SUNRISE,$SUNSET,$UV_INDEX,$WIND_SPEED,${WIND_DIRECTION}"
        );

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

    /**
     * Contains supported timezone configurations.
     * WARNING: Try to avoid using the [LOCAL_DEVICE_TIMEZONE]. Using this will fetch all
     * information according to the device's current time zone. This will almost never
     * be needed because weather details such as the time of sunset and sunrise
     * should always be specified based off of the timezone of the given coordinates and
     * not the timezone of the device.
     */
    enum class TimeZoneConfiguration(private val valueToBeSentToTheApi: String) {
        DEFAULT_FOR_GIVEN_COORDINATES("auto"),
        LOCAL_DEVICE_TIMEZONE(ZoneId.systemDefault().toString());

        override fun toString(): String = valueToBeSentToTheApi
    }

}