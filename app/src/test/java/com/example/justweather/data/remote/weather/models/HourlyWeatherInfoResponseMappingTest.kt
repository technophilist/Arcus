package com.example.justweather.data.remote.weather.models

import org.junit.Test
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

class HourlyWeatherInfoResponseMappingTest {

    private val startEpochSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    private val endEpochSecond = LocalDateTime.now().plusHours(9).toEpochSecond(ZoneOffset.UTC)
    private val timeStampsFor10hours =
        (startEpochSecond..endEpochSecond step 3600).map { it.toString() }
    private val precipitationProbabilities = List(10) { Random.nextInt(from = 0, until = 101) }
    private val weatherCodes = List(10) { 1 }
    private val temperatureForecasts = List(10) {
        Random.nextDouble(from = 0.0, until = 100.0).toFloat()
    }

    private val hourlyForecast = HourlyWeatherInfoResponse.HourlyForecast(
        timestamps = timeStampsFor10hours,
        precipitationProbabilityPercentages = precipitationProbabilities,
        weatherCodes = weatherCodes,
        temperatureForecasts = temperatureForecasts
    )

    private val hourlyWeatherInfoResponse = HourlyWeatherInfoResponse(
        latitude = "",
        longitude = "",
        hourlyForecast = hourlyForecast
    )

    @Test
    fun `HourlyWeatherInfoResponse to HourlyForecasts mapping test`() {
        // Given a valid instance of HourlyWeatherInfoResponse, when converting it to a list of
        // hourly forecast items
        val hourlyForecasts = hourlyWeatherInfoResponse.toHourlyForecasts()
        for (i in hourlyForecasts.indices) {
            val mappedHourlyForecast = hourlyForecasts[i]
            val correspondingLocalTime = LocalTime.ofInstant(
                Instant.ofEpochSecond(timeStampsFor10hours[i].toLong()),
                ZoneId.systemDefault()
            )
            // the 'isAm' property must be assigned true if the current time is before noon
            // or false (indicating that it's PM) if the current time is after-noon.
            val correspondingTimeAM = correspondingLocalTime.hour < 12 //
            if (correspondingTimeAM) assert(mappedHourlyForecast.isAM)
            else assert(!mappedHourlyForecast.isAM)

            // the mapped hour should match the expected hour (based on the timestamps passed
            // to the constructor of HourlyWeatherInfoResponse in this test)
            val corresponding12hourTime = correspondingLocalTime
                .format(DateTimeFormatter.ofPattern("hh"))
                .let {
                    // 12 hour time in int, with leading zeros removed
                    if (it.first() == '0') it.last().digitToInt() else it.toInt()
                }
            assert(mappedHourlyForecast.hour == corresponding12hourTime)
            // the temperature must be rounded to an Int and match the expected temperature(based on
            // the timestamps passed to the constructor of HourlyWeatherInfoResponse in this test)
            assert(temperatureForecasts[i].roundToInt() == mappedHourlyForecast.temperature)
        }
    }

    @Test
    fun `HourlyWeatherInfoResponse to PrecipitationProbabilities mapping test`() {
        // Given a valid instance of HourlyWeatherInfoResponse, when converting it to a list of
        // precipitation probabilities
        val mappedPrecipitationProbabilities =
            hourlyWeatherInfoResponse.toPrecipitationProbabilities()
        for (i in precipitationProbabilities.indices) {
            val mappedPrecipitationProbability = mappedPrecipitationProbabilities[i]
            val correspondingLocalTime = LocalTime.ofInstant(
                Instant.ofEpochSecond(timeStampsFor10hours[i].toLong()),
                ZoneId.systemDefault()
            )
            // the mapped hour should match the expected hour (based on the timestamps passed
            // to the constructor of HourlyWeatherInfoResponse in this test)
            val corresponding12hourTime = correspondingLocalTime
                .format(DateTimeFormatter.ofPattern("hh"))
                .let {
                    // 12 hour time in int, with leading zeros removed
                    if (it.first() == '0') it.last().digitToInt() else it.toInt()
                }
            assert(mappedPrecipitationProbability.hour == corresponding12hourTime)
            // the probability must match the expected probability (based on
            // the probabilities passed to the constructor of HourlyWeatherInfoResponse in this test)
            assert(precipitationProbabilities[i] == mappedPrecipitationProbability.probabilityPercentage)
        }
    }
}


