package com.example.justweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.repositories.location.LocationServicesRepository
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.toBriefWeatherDetails
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
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState as StateFlow<HomeScreenUiState>

    private var recentlyDeletedItem: BriefWeatherDetails? = null

    init {
        weatherRepository
            .getSavedLocationsListStream()
            .map { savedLocations ->
                _uiState.update { it.copy(isLoadingSavedLocations = true) }
                savedLocations.map { savedLocation ->
                    weatherRepository.fetchWeatherForLocation(
                        nameOfLocation = savedLocation.nameOfLocation,
                        latitude = savedLocation.coordinates.latitude,
                        longitude = savedLocation.coordinates.longitude
                    ).getOrThrow().toBriefWeatherDetails() // todo take care of exception
                }
            }
            .onEach { weatherDetailsOfSavedLocations ->
                _uiState.update {
                    it.copy(
                        isLoadingSavedLocations = false,
                        weatherDetailsOfSavedLocations = weatherDetailsOfSavedLocations
                    )
                }
            }
            .launchIn(viewModelScope)

        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        currentSearchQuery.debounce(250)
            .distinctUntilChanged()
            .mapLatest { query ->
                if (query.isBlank()) return@mapLatest Result.success(emptyList())
                _uiState.update { it.copy(isLoadingSuggestions = true) }
                locationServicesRepository.fetchSuggestedPlacesForQuery(query)
            }
            .filter { it.isSuccess }
            .map { it.getOrThrow() } // todo exception handling
            .onEach { autofillSuggestions ->
                _uiState.update {
                    it.copy(
                        isLoadingSuggestions = false,
                        autofillSuggestions = autofillSuggestions
                    )
                }
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
}