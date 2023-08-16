package com.example.arcus.ui.weatherdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcus.data.repositories.textgenerator.GenerativeTextRepository
import com.example.arcus.data.repositories.weather.WeatherRepository
import com.example.arcus.data.repositories.weather.fetchHourlyForecastsForNext24Hours
import com.example.arcus.data.repositories.weather.fetchPrecipitationProbabilitiesForNext24hours
import com.example.arcus.domain.models.weather.CurrentWeatherDetails
import com.example.arcus.ui.navigation.ArcusNavigationDestinations.WeatherDetailScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository,
    private val generativeTextRepository: GenerativeTextRepository
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
        weatherRepository.getSavedLocationsListStream()
            .map { namesOfSavedLocationsList ->
                namesOfSavedLocationsList.any { it.nameOfLocation == nameOfLocation }
            }
            .onEach { isPreviouslySavedLocation ->
                _uiState.update { it.copy(isPreviouslySavedLocation = isPreviouslySavedLocation) }
            }
            .launchIn(scope = viewModelScope)
        viewModelScope.launch { fetchWeatherDetailsAndUpdateState() }
    }

    // todo stopship - using supervisor scope doesnt reset the ui state is loading to false
    // and also using coroutineScope crashes the entire app. See why
    private suspend fun fetchWeatherDetailsAndUpdateState(): Unit = supervisorScope {
        _uiState.update { it.copy(isLoading = true) }
        val weatherDetailsOfChosenLocation = async {
            weatherRepository.fetchWeatherForLocation(
                nameOfLocation = nameOfLocation,
                latitude = latitude,
                longitude = longitude
            ).getOrThrow()
        }
        launch {
            // This request, relatively takes a while before returning.
            // Hence launch it in a separate coroutine.
            fetchAiGeneratedTextAndUpdateState(weatherDetailsOfChosenLocation.await())
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
                    errorMessage = DEFAULT_ERROR_MESSAGE
                )
            }
        }
    }

    private suspend fun fetchAiGeneratedTextAndUpdateState(weatherDetailsOfChosenLocation: CurrentWeatherDetails) {
        coroutineScope {
            _uiState.update { it.copy(isWeatherSummaryTextLoading = true) }
            val summaryMessage = async {
                generativeTextRepository.generateTextForWeatherDetails(
                    weatherDetails = weatherDetailsOfChosenLocation
                ).getOrThrow()
            }
            _uiState.update {
                it.copy(
                    isWeatherSummaryTextLoading = false,
                    weatherSummaryText = summaryMessage.await()
                )
            }
        }
    }

    fun addLocationToSavedLocations() {
        viewModelScope.launch {
            weatherRepository.saveWeatherLocation(
                nameOfLocation = nameOfLocation,
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    companion object {
        private const val DEFAULT_ERROR_MESSAGE =
            "Oops! An error occurred when trying to fetch the " +
                    "weather details. Please try again."
    }

}