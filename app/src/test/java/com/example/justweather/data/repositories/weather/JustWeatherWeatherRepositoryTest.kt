package com.example.justweather.data.repositories.weather

import com.example.justweather.data.local.weather.JustWeatherDatabaseDao
import com.example.justweather.data.local.weather.SavedWeatherLocationEntity
import com.example.justweather.di.NetworkModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doAnswer


@OptIn(ExperimentalCoroutinesApi::class)
class JustWeatherWeatherRepositoryTest {

    private lateinit var weatherRepository: JustWeatherWeatherRepository
    private val savedLocations = listOf(
        SavedWeatherLocationEntity(
            nameOfLocation = "Seattle",
            latitude = "47.6062",
            longitude = "-122.3321"
        ),
        SavedWeatherLocationEntity(
            nameOfLocation = "New York",
            latitude = "40.7128",
            longitude = "-74.0060"
        ),
        SavedWeatherLocationEntity(
            nameOfLocation = "Los Angeles",
            latitude = "34.0522",
            longitude = "-118.2437"
        )
    )

    @Before
    fun setup() {
        val daoMock = mock<JustWeatherDatabaseDao> {
            onBlocking { getAllSavedWeatherEntities() } doAnswer {
                flowOf(savedLocations)
            }
        }
        weatherRepository = JustWeatherWeatherRepository(
            weatherClient = NetworkModule.provideWeatherClient(),
            justWeatherDatabaseDao = daoMock
        )
    }

    @Test
    fun `getWeatherForLocation should successfully fetch weather details for a given valid coordinate`() =
        runTest {
            val latitude = "37.422131"
            val longitude = "-122.084801"
            val result = weatherRepository.fetchWeatherForLocation(latitude, longitude)
            assert(result.isSuccess)
            assert(result.getOrNull() != null)
        }

    @Test
    fun `getWeatherForLocation should return an exception for an invalid coordinate`() = runTest {
        // This is an invalid coordinate because the latitude and longitude values are outside the
        // valid range of -90 to 90 and -180 to 180 degrees, respectively.
        val latitude = "1000.0"
        val longitude = "-2000.0"
        val result = weatherRepository.fetchWeatherForLocation(latitude, longitude)
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
    }

    @Test
    fun `weather details for saved locations are successfully fetched`() = runTest {
        val weatherDetailsForSavedLocations = weatherRepository
            .getWeatherStreamForPreviouslySavedLocations()
            .first()
        assert(weatherDetailsForSavedLocations.isNotEmpty())
        for ((index, weatherDetail) in weatherDetailsForSavedLocations.withIndex()) {
            assert(weatherDetail.nameOfLocation == savedLocations[index].nameOfLocation)
            println(weatherDetail)
        }
    }
}

