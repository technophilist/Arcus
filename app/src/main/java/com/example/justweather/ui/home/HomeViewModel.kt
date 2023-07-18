package com.example.justweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.repositories.location.LocationServicesRepository
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.domain.models.BriefWeatherDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationServicesRepository: LocationServicesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val currentSearchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(UiState.IDLE)
    val uiState = _uiState as StateFlow<UiState>

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val currentSuggestions = currentSearchQuery.debounce(250)
        .distinctUntilChanged()
        .mapLatest { query ->
            if (query.isBlank()) return@mapLatest Result.success(emptyList())
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

    private var recentlyDeletedItem: BriefWeatherDetails? = null

    init {
        _uiState.value = UiState.LOADING_SAVED_LOCATIONS
        weatherRepository
            .getWeatherStreamForPreviouslySavedLocations()
            .onEach {
                if (_uiState.value == UiState.LOADING_SAVED_LOCATIONS) {
                    _uiState.value = UiState.IDLE
                }
                _weatherDetailsOfSavedLocations.value = it
            }
            .launchIn(viewModelScope)
    }

    /**
     * Used to set the [searchQuery] for which the suggestions should be generated.
     */
    fun setSearchQueryForSuggestionsGeneration(searchQuery: String) {
        currentSearchQuery.value = searchQuery
    }

    fun deleteSavedWeatherLocation(briefWeatherDetails: BriefWeatherDetails) {
        recentlyDeletedItem = briefWeatherDetails
        viewModelScope.launch {
            weatherRepository.deleteWeatherLocationFromSavedItems(briefWeatherDetails)
        }
    }

    fun restoreRecentlyDeletedItem() {
        recentlyDeletedItem?.let {
            viewModelScope.launch { weatherRepository.tryRestoringDeletedWeatherLocation(it.nameOfLocation) }
        }
    }

    /**
     * An enum that contains all possible UI states.
     */
    enum class UiState {
        IDLE,
        LOADING_SUGGESTIONS,
        LOADING_SAVED_LOCATIONS
    }
}