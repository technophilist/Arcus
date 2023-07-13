package com.example.justweather.data.repositories.weather

import androidx.core.util.toClosedRange
import androidx.core.util.toRange
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
import java.time.LocalDate
import java.time.LocalDateTime


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
            val result = weatherRepository.fetchWeatherForLocation("test", latitude, longitude)
            assert(result.isSuccess)
            assert(result.getOrNull() != null)
        }

    @Test
    fun `getWeatherForLocation should return an exception for an invalid coordinate`() = runTest {
        // This is an invalid coordinate because the latitude and longitude values are outside the
        // valid range of -90 to 90 and -180 to 180 degrees, respectively.
        val latitude = "1000.0"
        val longitude = "-2000.0"
        val result = weatherRepository.fetchWeatherForLocation("", latitude, longitude)
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

    @Test
    fun `Hourly precipitation probabilities must be fetched successfully for a valid date range & coordinate`() =
        runTest {
            // Given a set of valid test coordinates
            val testLatitude = "38.6275"
            val testLongitude = "-92.5666"
            val currentLocalDate = LocalDate.now().also(::println)
            val dateRange = currentLocalDate..currentLocalDate.plusDays(1) // 48 hours
            // when getting the hourly precipitation probabilities for a given date range (48 hours)
            val result =
                weatherRepository.getHourlyPrecipitationProbabilities(
                    latitude = testLatitude,
                    longitude = testLongitude,
                    dateRange = dateRange
                ).getOrNull()!! // the result must be successfully fetched
            // the result must contain the precipitation probabilities of only one coordinate
            val resultCoordinateCount = result.distinctBy { it.latitude }.count()
            assert(resultCoordinateCount == 1)
            // the coordinates of the response can differ by at most 1 degree
            // (the open-mateo api tends to return responses with coordinates that slightly
            // deviate from what is passed to the API)
            for (probability in result) {
                val latitudeDifference = probability.latitude.toDouble() - testLatitude.toDouble()
                val longitudeDifference =
                    probability.longitude.toDouble() - testLongitude.toDouble()
                assert(latitudeDifference <= 1 && longitudeDifference <= 1)
            }
            // the result must have exactly 48 hourly forecast items
            assert(result.size == 48)
        }
}

