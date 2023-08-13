package com.example.arcus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcus.data.remote.location.ReverseGeocoder
import com.example.arcus.data.repositories.location.LocationServicesRepository
import com.example.arcus.data.repositories.weather.WeatherRepository
import com.example.arcus.data.repositories.weather.fetchHourlyForecastsForNext24Hours
import com.example.arcus.domain.location.CurrentLocationProvider
import com.example.arcus.domain.models.weather.BriefWeatherDetails
import com.example.arcus.domain.models.weather.CurrentWeatherDetails
import com.example.arcus.domain.models.location.SavedLocation
import com.example.arcus.domain.models.weather.toBriefWeatherDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val currentLocationProvider: CurrentLocationProvider,
    private val reverseGeocoder: ReverseGeocoder,
    private val locationServicesRepository: LocationServicesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val currentSearchQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState as StateFlow<HomeScreenUiState>

    // a cache that stores the CurrentWeatherDetails of a specific SavedLocation
    private var currentWeatherDetailsCache = mutableMapOf<SavedLocation, CurrentWeatherDetails>()
    private var recentlyDeletedItem: BriefWeatherDetails? = null

    init {
        // saved locations stream
        weatherRepository.getSavedLocationsListStream()
            .onEach { _uiState.update { it.copy(isLoadingSavedLocations = true) } }
            .map { savedLocations ->
                fetchCurrentWeatherDetailsWithCache(savedLocations.toSet()).sortedBy { it.nameOfLocation }
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

        // suggestions for current search query stream
        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        currentSearchQuery.debounce(250)
            .distinctUntilChanged()
            .mapLatest { query ->
                if (query.isBlank()) return@mapLatest emptyList()
                _uiState.update { it.copy(isLoadingSuggestions = true) }
                locationServicesRepository.fetchSuggestedPlacesForQuery(query)
                    .getOrThrow()// todo exception handling
            }
            .onEach { autofillSuggestions ->
                _uiState.update {
                    it.copy(isLoadingSuggestions = false, autofillSuggestions = autofillSuggestions)
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
     * Used to fetch a list of [BriefWeatherDetails] for all the [savedLocations] efficiently
     * using the [currentWeatherDetailsCache]
     */
    private suspend fun fetchCurrentWeatherDetailsWithCache(savedLocations: Set<SavedLocation>): List<BriefWeatherDetails> {
        // remove locations in the cache that have been deleted by the user
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
            ).getOrThrow().also { currentWeatherDetailsCache[savedLocationNotInCache] = it }
        }
        return currentWeatherDetailsCache.values.toList().map { it.toBriefWeatherDetails() }
    }

    fun fetchWeatherForCurrentUserLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingWeatherDetailsOfCurrentLocation = true) }
            val coordinates =
                currentLocationProvider.getCurrentLocation().getOrNull() ?: return@launch
            val nameOfLocation = reverseGeocoder.getLocationNameForCoordinates(
                coordinates.latitude.toDouble(),
                coordinates.longitude.toDouble()
            ).getOrNull() ?: return@launch // todo : exception handling

            coroutineScope {
                val weatherDetailsForCurrentLocation = async {
                    weatherRepository.fetchWeatherForLocation(
                        nameOfLocation = nameOfLocation,
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude
                    ).getOrNull()?.toBriefWeatherDetails() // todo : exception handling
                }

                val hourlyForecastsForCurrentLocation = async {
                    weatherRepository.fetchHourlyForecastsForNext24Hours(
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude
                    ).getOrNull() // todo : exception handling
                }
                _uiState.update {
                    it.copy(
                        isLoadingWeatherDetailsOfCurrentLocation = false,
                        weatherDetailsOfCurrentLocation = weatherDetailsForCurrentLocation.await(),
                        hourlyForecastsForCurrentLocation = hourlyForecastsForCurrentLocation.await()
                    )
                }
            }
        }
    }
}