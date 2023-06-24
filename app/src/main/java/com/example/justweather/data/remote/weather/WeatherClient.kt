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

    // todo docs
    @GET(WeatherClientConstants.EndPoints.GET_WEATHER_ENDPOINT)
    suspend fun getWeatherForCoordinates(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("temperature_unit") temperatureUnit: String = WeatherClientConstants.Units.TemperatureUnits.CELSIUS,
        @Query("windspeed_unit") windSpeedUnit: String = WeatherClientConstants.Units.WindSpeedUnit.KILOMETERS_PER_HOUR,
        @Query("precipitation_unit") precipitationUnit: String = WeatherClientConstants.Units.PrecipitationUnit.INCHES,
        @Query("current_weather") shouldIncludeCurrentWeatherInformation: Boolean = true // must always be set to true
    ): Response<CurrentWeatherResponse>

    @GET(WeatherClientConstants.EndPoints.GET_WEATHER_ENDPOINT)
    suspend fun getHourlyForecast(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("start_date") startDate: String, // YYYY-MM-DD
        @Query("end_date") endDate: String, // YYYY-MM-DD
        @Query("timezone") timezone: String = ZoneId.systemDefault().toString(),
        @Query("precipitation_unit") precipitationUnit: String = WeatherClientConstants.Units.PrecipitationUnit.INCHES,
        @Query("timeformat") timeFormat: String = WeatherClientConstants.TimeFormats.UNIX_EPOCH_TIME_IN_SECONDS,
        @Query("hourly") hourlyForecastsToReturn: String = WeatherClientConstants.HourlyForecastItems.DEFAULT_ITEMS
    ): Response<HourlyWeatherInfoResponse>

}
