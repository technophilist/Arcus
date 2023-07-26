package com.example.justweather.ui.weatherDetail

import com.example.justweather.domain.models.CurrentWeatherDetails
import com.example.justweather.domain.models.HourlyForecast
import com.example.justweather.domain.models.PrecipitationProbability
import com.example.justweather.domain.models.SingleWeatherDetail

/**
 * A UI state class that represents the current UI state of the [WeatherDetailScreen].
 */
data class WeatherDetailScreenUiState(
    val isLoading: Boolean = false,
    val isPreviouslySavedLocation: Boolean = false,
    val weatherDetailsOfChosenLocation: CurrentWeatherDetails? = null,
    val errorMessage: String? = null,
    val precipitationProbabilities: List<PrecipitationProbability> = emptyList(),
    val hourlyForecasts: List<HourlyForecast> = emptyList(),
    val additionalWeatherInfoItems: List<SingleWeatherDetail> = emptyList()
)