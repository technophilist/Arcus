package com.example.justweather.data.remote.weather

import com.example.justweather.di.NetworkModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.*

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherClientTest {
    private val client = NetworkModule.provideWeatherClient()

    @Test
    fun `Given valid coordinates, the weather should be successfully fetched`() = runTest {
        val response = client.getWeatherForCoordinates(
            latitude = "37.333333",
            longitude = "-122.028033"
        ).body()
        assert(response != null)
        println(response)
    }

    @Test
    fun `Given a valid coordinate, the hourly forecast of that coordinate must be fetched successfully`() =
        runTest {
            // Given a start date and an end date meant to be used for fetching the hourly
            // forecast for 48 hours
            val startDate = LocalDate.now()
            val endDate = LocalDate.now().plusDays(1)
            // when fetching the hourly forecast
            val response = client.getHourlyForecast(
                latitude = "37.333333",
                longitude = "-122.028033",
                startDate = startDate.toString(),
                endDate = endDate.toString(),
            ).body()
            // the response must be successfully fetched
            assert(response != null)
            // there must be weather info for 48 hours
            assert(response!!.hourlyForecast.timestamps.size == 48)
            // there must only be two distinct timestamps - one for the current day, one of the next day
            val distinctResponseTimeStamps = response.hourlyForecast.timestamps
                .map { timestampSecondsString ->
                    LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(timestampSecondsString.toLong()),
                        ZoneId.systemDefault()
                    ).toLocalDate()
                }.distinct()
            assert(distinctResponseTimeStamps.size == 2)
            // those two timestamps must represent the current day and the next day
            val requiredTimeStamps = listOf(startDate, endDate)
            assert(distinctResponseTimeStamps.containsAll(requiredTimeStamps))
        }
}