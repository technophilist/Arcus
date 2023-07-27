package com.example.justweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.repositories.location.LocationServicesRepository
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.CurrentWeatherDetails
import com.example.justweather.domain.models.SavedLocation
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

    private var currentWeatherDetailsCache = mutableMapOf<SavedLocation, CurrentWeatherDetails>()
    private var recentlyDeletedItem: BriefWeatherDetails? = null

    init {
        weatherRepository
            .getSavedLocationsListStream()
            .map { savedLocations ->
                _uiState.update { it.copy(isLoadingSavedLocations = true) }
                fetchCurrentWeatherDetailsWithCache(savedLocations.toSet())// todo handle exceptions
            }.map { currentWeatherDetails ->
                currentWeatherDetails.map { it.toBriefWeatherDetails() }
            }
            .onEach { weatherDetailsOfSavedLocations ->
                _uiState.update {
                    it.copy(
                        isLoadingSavedLocations = false,
                        weatherDetailsOfSavedLocations = weatherDetailsOfSavedLocations
                    )
                }
            }
            .launchIn(viewModelScope) // todo take care of exception

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

    /**
     * Used to fetch a list of [CurrentWeatherDetails] for all the [savedLocations] efficiently
     * using the [currentWeatherDetailsCache]
     */
    private suspend fun fetchCurrentWeatherDetailsWithCache(savedLocations: Set<SavedLocation>): List<CurrentWeatherDetails> {
        // remove locations in the cache that have been deleted
        val removedLocations = currentWeatherDetailsCache.keys subtract savedLocations
        for (removedLocation in removedLocations) {
            currentWeatherDetailsCache.remove(removedLocation)
        }
        // only fetch weather details of the items that are not in cache.
        val locationsNotInCache = savedLocations subtract currentWeatherDetailsCache.keys
        for (savedLocationNotInCache in locationsNotInCache) {
            weatherRepository.fetchWeatherForLocation(
                nameOfLocation = savedLocationNotInCache.nameOfLocation,
                latitude = savedLocationNotInCache.coordinates.latitude,
                longitude = savedLocationNotInCache.coordinates.longitude
            ).getOrThrow().also {
                currentWeatherDetailsCache[savedLocationNotInCache] = it
            }
        }
        return currentWeatherDetailsCache.values.toList()
    }
}