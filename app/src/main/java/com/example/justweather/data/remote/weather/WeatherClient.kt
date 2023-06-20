package com.example.justweather.data.remote.weather

import com.example.justweather.data.remote.weather.models.CurrentWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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
}
