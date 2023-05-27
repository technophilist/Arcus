package com.example.justweather.data.remote.weather

import com.example.justweather.BuildConfig
import com.example.justweather.data.remote.weather.WeatherClientConstants
import com.example.justweather.di.NetworkModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class WeatherClientTest {
    private val client = NetworkModule.provideWeatherClient()

    @Test
    fun `Given valid coordinates, the weather should be successfully fetched`() = runTest {
        val response = client.getWeatherForCoordinates(
            apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY,
            latitude = "37.333333",
            longitude = "-122.028033",
            units = WeatherClientConstants.Units.CELSIUS
        ).body()
        advanceUntilIdle()
        assert(response != null)
        assert(response!!.cityName == "Cupertino")
    }

}