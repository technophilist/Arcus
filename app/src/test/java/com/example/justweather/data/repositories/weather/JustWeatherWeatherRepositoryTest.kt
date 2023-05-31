package com.example.justweather.data.repositories.weather

import com.example.justweather.di.NetworkModule
import com.example.justweather.domain.models.WeatherDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class JustWeatherWeatherRepositoryTest {

    private val weatherRepository =
        JustWeatherWeatherRepository(NetworkModule.provideWeatherClient())

    @Test
    fun `getWeatherForLocation should successfully fetch weather details for a given valid coordinate`() =
        runTest {
            val latitude = "37.422131"
            val longitude = "-122.084801"
            val result = weatherRepository.getWeatherForLocation(latitude, longitude)
            assert(result.isSuccess)
            assert(result.getOrNull() != null)
        }

    @Test
    fun `getWeatherForLocation should return an exception for an invalid coordinate`() = runTest {
        // This is an invalid coordinate because the latitude and longitude values are outside the
        // valid range of -90 to 90 and -180 to 180 degrees, respectively.
        val latitude = "1000.0"
        val longitude = "-2000.0"
        val result = weatherRepository.getWeatherForLocation(latitude, longitude)
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
    }
}

