package com.example.justweather.data.remote.weather

import com.example.justweather.data.remote.weather.models.AdditionalDailyForecastVariablesResponse
import com.example.justweather.data.remote.weather.models.CurrentWeatherResponse
import com.example.justweather.data.remote.weather.models.HourlyWeatherInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
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
     * @param timezoneConfiguration The [WeatherClientConstants.TimeZoneConfiguration] to use for the hourly forecast.
     * @param precipitationUnit The unit of precipitation to use.
     * @param timeFormat The time format to use for the hourly forecast.
     * @param hourlyForecastsToReturn The number of hourly forecasts to return.
     * @return A [Response] object containing the hourly forecast information.
     */
    @GET(WeatherClientConstants.EndPoints.GET_WEATHER_ENDPOINT)
    suspend fun getHourlyForecast(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate,
        @Query("timezone") timezoneConfiguration: WeatherClientConstants.TimeZoneConfiguration = WeatherClientConstants.TimeZoneConfiguration.LOCAL_DEVICE_TIMEZONE,
        @Query("precipitation_unit") precipitationUnit: WeatherClientConstants.PrecipitationUnit = WeatherClientConstants.PrecipitationUnit.INCHES,
        @Query("timeformat") timeFormat: WeatherClientConstants.TimeFormats = WeatherClientConstants.TimeFormats.UNIX_EPOCH_TIME_IN_SECONDS,
        @Query("hourly") hourlyForecastsToReturn: WeatherClientConstants.HourlyForecastItems = WeatherClientConstants.HourlyForecastItems.ALL
    ): Response<HourlyWeatherInfoResponse>


    /**
     * Used to get additional daily forecast variables for the given coordinates.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param startDate The start date of the forecast.
     * @param endDate The end date of the forecast.
     * @param timezoneConfiguration The [WeatherClientConstants.TimeZoneConfiguration] to be used while
     * fetching the information of the location. Default is
     * [WeatherClientConstants.TimeZoneConfiguration.DEFAULT_FOR_GIVEN_COORDINATES].
     * @param timeFormat The time format of the forecast. Default is UNIX epoch time in seconds.
     * @param dailyForecastsToReturn The daily forecasts to return. Default is all forecasts.
     *
     * @return Response<AdditionalDailyForecastVariablesResponse>
     */
    @GET(WeatherClientConstants.EndPoints.GET_WEATHER_ENDPOINT)
    suspend fun getAdditionalDailyForecastVariables(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate,
        @Query("timezone") timezoneConfiguration: WeatherClientConstants.TimeZoneConfiguration = WeatherClientConstants.TimeZoneConfiguration.DEFAULT_FOR_GIVEN_COORDINATES,
        @Query("timeformat") timeFormat: WeatherClientConstants.TimeFormats = WeatherClientConstants.TimeFormats.UNIX_EPOCH_TIME_IN_SECONDS,
        @Query("daily") dailyForecastsToReturn: WeatherClientConstants.DailyForecastItems = WeatherClientConstants.DailyForecastItems.ALL
    ): Response<AdditionalDailyForecastVariablesResponse>

}
