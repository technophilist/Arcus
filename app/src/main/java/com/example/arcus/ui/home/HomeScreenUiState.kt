package com.example.arcus.ui.home

import com.example.arcus.domain.models.weather.BriefWeatherDetails
import com.example.arcus.domain.models.weather.HourlyForecast
import com.example.arcus.domain.models.location.LocationAutofillSuggestion

/**
 * A UI state class that represents the current UI state of the [HomeScreen].
 */
data class HomeScreenUiState(
    val isLoadingAutofillSuggestions: Boolean = false,
    val isLoadingSavedLocations: Boolean = false,
    val isLoadingWeatherDetailsOfCurrentLocation: Boolean = false,
    val errorFetchingWeatherForCurrentLocation: Boolean = false,
    val errorFetchingWeatherForSavedLocations: Boolean = false,
    val errorFetchingAutofillSuggestions: Boolean = false,
    val weatherDetailsOfCurrentLocation: BriefWeatherDetails? = null,
    val hourlyForecastsForCurrentLocation: List<HourlyForecast>? = null,
    val autofillSuggestions: List<LocationAutofillSuggestion> = emptyList(),
    val weatherDetailsOfSavedLocations: List<BriefWeatherDetails> = emptyList(),
)