package com.example.justweather.ui.home

import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.HourlyForecast
import com.example.justweather.domain.models.LocationAutofillSuggestion

/**
 * A UI state class that represents the current UI state of the [HomeScreen].
 */
data class HomeScreenUiState(
    val isLoadingSuggestions: Boolean = false,
    val isLoadingSavedLocations: Boolean = false,
    val weatherDetailsOfCurrentLocation: BriefWeatherDetails? = null,
    val hourlyForecastsOfCurrentLocation: List<HourlyForecast>? = null,
    val autofillSuggestions: List<LocationAutofillSuggestion> = emptyList(),
    val weatherDetailsOfSavedLocations: List<BriefWeatherDetails> = emptyList()
)