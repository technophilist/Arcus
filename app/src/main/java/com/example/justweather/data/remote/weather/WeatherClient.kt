package com.example.justweather.data.remote.weather

import com.example.justweather.data.remote.weather.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface representing a network based weather client.
 */
interface WeatherClient {

    /**
     * This method is used to get weather data for the given [latitude] and [longitude].The [units]
     * parameter determines the units in which the weather data should be returned. Default is Celsius.
     */
    @GET(WeatherClientConstants.EndPoints.GET_WEATHER_ENDPOINT)
    suspend fun getWeatherForCoordinates(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String = WeatherClientConstants.Units.CELSIUS
    ): Response<WeatherResponse>
}
