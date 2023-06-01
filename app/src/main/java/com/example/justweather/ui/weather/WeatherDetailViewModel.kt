package com.example.justweather.ui.weather

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.domain.models.WeatherDetails
import com.example.justweather.ui.navigation.JustWeatherNavigationDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val latitude: String =
        savedStateHandle[JustWeatherNavigationDestinations.WeatherDetailScreen.NAV_ARG_LATITUDE]!!
    private val longitude: String =
        savedStateHandle[JustWeatherNavigationDestinations.WeatherDetailScreen.NAV_ARG_LONGITUDE]!!

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState as StateFlow<UiState>

    init {
        viewModelScope.launch { fetchWeatherInfo() }
    }

    /**
     * Fetches weather information from the repository, ensuring that the [uiState] is
     * correctly updated.
     */
    private suspend fun fetchWeatherInfo() {
        _uiState.value = UiState.Loading
        val weatherDetails = weatherRepository.fetchWeatherForLocation(
            latitude = latitude,
            longitude = longitude
        ).getOrNull()
        _uiState.value = if (weatherDetails == null) UiState.Error
        else UiState.SuccessfullyLoaded(weatherDetails)
    }

    /**
     * A sealed class that contains all possible UI states.
     */
    sealed class UiState(val weatherDetails: WeatherDetails = WeatherDetails.EmptyWeatherDetails) {
        object Idle : UiState()
        object Loading : UiState()
        class SuccessfullyLoaded(weatherDetails: WeatherDetails) : UiState(weatherDetails)
        object Error : UiState()
    }
}