package com.example.justweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.repositories.location.LocationServicesRepository
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.domain.models.BriefWeatherDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationServicesRepository: LocationServicesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val currentSearchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(UiState.IDLE)
    val uiState = _uiState as StateFlow<UiState>

    @OptIn(FlowPreview::class)
    val currentSuggestions = currentSearchQuery.debounce(250)
        .map { query ->
            _uiState.value = UiState.LOADING_SUGGESTIONS
            locationServicesRepository.fetchSuggestedPlacesForQuery(query)
                .also { _uiState.value = UiState.IDLE }
        }
        .filter { it.isSuccess }
        .map { it.getOrThrow() }

    private val _weatherDetailsOfSavedLocations =
        MutableStateFlow<List<BriefWeatherDetails>>(emptyList())
    val weatherDetailsOfSavedLocations =
        _weatherDetailsOfSavedLocations as StateFlow<List<BriefWeatherDetails>>

    init {
        weatherRepository
            .getWeatherStreamForPreviouslySavedLocations()
            .onEach { _weatherDetailsOfSavedLocations.value = it }
            .launchIn(viewModelScope)
    }

    /**
     * Used to set the [searchQuery] for which the suggestions should be generated.
     */
    fun setSearchQueryForSuggestionsGeneration(searchQuery: String) {
        currentSearchQuery.value = searchQuery
    }

    /**
     * An enum that contains all possible UI states.
     */
    enum class UiState { IDLE, LOADING_SUGGESTIONS }
}