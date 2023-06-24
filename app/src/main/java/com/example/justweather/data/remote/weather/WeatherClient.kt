package com.example.justweather.data.remote.weather

import com.example.justweather.data.remote.weather.models.CurrentWeatherResponse
import com.example.justweather.data.remote.weather.models.HourlyWeatherInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.ZoneId

/**
 * An interface representing a network based weather client.
 */
interface WeatherClient {

    /**
     * Get the current weather for the given coordinates.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param temperatureUnit The unit of temperature to use.
     * @param windSpeedUnit The unit of wind speed to use.
     * @param precipitationUnit The unit of precipitation to use.
     * @param shouldIncludeCurrentWeatherInformation Whether or not to include current weather information.
     * **MUST ALWAYS BE SET TO TRUE!**
     * @return A [Response] object containing the current weather information.
     */
    @GET(WeatherClientConstants.EndPoints.GET_WEATHER_ENDPOINT)
    suspend fun getWeatherForCoordinates(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("temperature_unit") temperatureUnit: WeatherClientConstants.TemperatureUnits = WeatherClientConstants.TemperatureUnits.CELSIUS,
        @Query("windspeed_unit") windSpeedUnit: WeatherClientConstants.WindSpeedUnit = WeatherClientConstants.WindSpeedUnit.KILOMETERS_PER_HOUR,
        @Query("precipitation_unit") precipitationUnit: WeatherClientConstants.PrecipitationUnit = WeatherClientConstants.PrecipitationUnit.INCHES,
        @Query("current_weather") shouldIncludeCurrentWeatherInformation: Boolean = true // must always be set to true
    ): Response<CurrentWeatherResponse>

    /**
     * Get the [HourlyWeatherInfoResponse] for the given coordinates.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param startDate The start date for the hourly forecast in YYYY-MM-DD format.
     * @param endDate The end date for the hourly forecast in YYYY-MM-DD format.
     * @param timezone The timezone to use for the hourly forecast.
     * @param precipitationUnit The unit of precipitation to use.
     * @param timeFormat The time format to use for the hourly forecast.
     * @param hourlyForecastsToReturn The number of hourly forecasts to return.
     * @return A [Response] object containing the hourly forecast information.
     */
    @GET(WeatherClientConstants.EndPoints.GET_WEATHER_ENDPOINT)
    suspend fun getHourlyForecast(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("start_date") startDate: String, // YYYY-MM-DD
        @Query("end_date") endDate: String, // YYYY-MM-DD
        @Query("timezone") timezone: String = ZoneId.systemDefault().toString(),
        @Query("precipitation_unit") precipitationUnit: WeatherClientConstants.PrecipitationUnit = WeatherClientConstants.PrecipitationUnit.INCHES,
        @Query("timeformat") timeFormat: WeatherClientConstants.TimeFormats = WeatherClientConstants.TimeFormats.UNIX_EPOCH_TIME_IN_SECONDS,
        @Query("hourly") hourlyForecastsToReturn: WeatherClientConstants.HourlyForecastItems = WeatherClientConstants.HourlyForecastItems.ALL
    ): Response<HourlyWeatherInfoResponse>
}
