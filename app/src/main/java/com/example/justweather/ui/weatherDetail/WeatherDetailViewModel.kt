package com.example.justweather.ui.weatherDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.data.repositories.weather.fetchHourlyForecastsForNext24Hours
import com.example.justweather.data.repositories.weather.fetchPrecipitationProbabilitiesForNext24hours
import com.example.justweather.ui.navigation.JustWeatherNavigationDestinations.WeatherDetailScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val latitude: String =
        savedStateHandle[WeatherDetailScreen.NAV_ARG_LATITUDE]!!
    private val longitude: String =
        savedStateHandle[WeatherDetailScreen.NAV_ARG_LONGITUDE]!!
    private val nameOfLocation: String =
        savedStateHandle[WeatherDetailScreen.NAV_ARG_NAME_OF_LOCATION]!!


    private val _uiState = MutableStateFlow(WeatherDetailScreenUiState())
    val uiState = _uiState as StateFlow<WeatherDetailScreenUiState>

    init {
        viewModelScope.launch { fetchWeatherDetailsAndUpdateState() }
        weatherRepository.getWeatherStreamForPreviouslySavedLocations()
            .map { savedWeatherDetails ->
                savedWeatherDetails.any { it.nameOfLocation == nameOfLocation }
            }
            .distinctUntilChanged()
            .onEach { isSavedLocation ->
                _uiState.update { it.copy(isPreviouslySavedLocation = isSavedLocation) }
            }
            .launchIn(scope = viewModelScope)
    }

    private suspend fun fetchWeatherDetailsAndUpdateState(): Unit = coroutineScope {
        _uiState.update { it.copy(isLoading = true) }
        val weatherDetailsOfChosenLocation = async {
            weatherRepository.fetchWeatherForLocation(
                nameOfLocation = nameOfLocation,
                latitude = latitude,
                longitude = longitude
            ).getOrThrow()
        }
        val precipitationProbabilities =
            async {
                weatherRepository.fetchPrecipitationProbabilitiesForNext24hours(
                    latitude = latitude,
                    longitude = longitude
                ).getOrThrow()
            }
        val hourlyForecasts = async {
            weatherRepository.fetchHourlyForecastsForNext24Hours(
                latitude = latitude,
                longitude = longitude
            ).getOrThrow()
        }
        val additionalWeatherInfoItems = async {
            weatherRepository.fetchAdditionalWeatherInfoItemsListForCurrentDay(
                latitude = latitude,
                longitude = longitude
            ).getOrThrow()
        }

        try {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    weatherDetailsOfChosenLocation = weatherDetailsOfChosenLocation.await(),
                    precipitationProbabilities = precipitationProbabilities.await(),
                    hourlyForecasts = hourlyForecasts.await(),
                    additionalWeatherInfoItems = additionalWeatherInfoItems.await()
                )
            }
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Oops! An error occurred while fetching the weather details."
                )
            }
        }
    }

    fun addLocationToSavedLocations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            weatherRepository.saveWeatherLocation(
                nameOfLocation = nameOfLocation,
                latitude = latitude,
                longitude = longitude
            )
            _uiState.update { it.copy(isLoading = false) }
        }
    }

}