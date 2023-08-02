package com.example.justweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.remote.location.ReverseGeocoder
import com.example.justweather.data.repositories.location.LocationServicesRepository
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.data.repositories.weather.fetchHourlyForecastsForNext24Hours
import com.example.justweather.domain.location.CurrentLocationProvider
import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.Coordinates
import com.example.justweather.domain.models.CurrentWeatherDetails
import com.example.justweather.domain.models.HourlyForecast
import com.example.justweather.domain.models.LocationAutofillSuggestion
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
    private val currentLocationProvider: CurrentLocationProvider,
    private val reverseGeocoder: ReverseGeocoder,
    private val locationServicesRepository: LocationServicesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val currentSearchQuery = MutableStateFlow("")
    private val isLoadingAutofillSuggestions = MutableStateFlow(false)
    private val isLoadingSavedLocations = MutableStateFlow(false)
    private val coordinatesOfCurrentLocation = MutableStateFlow<Coordinates?>(null)

    val weatherDetailsOfCurrentLocation: StateFlow<BriefWeatherDetails?> =
        coordinatesOfCurrentLocation.filterNotNull()
            .map { coordinates ->
                val nameOfLocation = reverseGeocoder.getLocationNameForCoordinates(
                    coordinates.latitude.toDouble(),
                    coordinates.longitude.toDouble()
                ).getOrNull() ?: return@map null // todo : exception handling
                weatherRepository.fetchWeatherForLocation(
                    nameOfLocation = nameOfLocation,
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ).getOrNull()?.toBriefWeatherDetails() // todo : exception handling
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(300),
                initialValue = null
            )

    val hourlyForecastsForCurrentLocation: StateFlow<List<HourlyForecast>?> =
        coordinatesOfCurrentLocation.filterNotNull()
            .map { coordinates ->
                weatherRepository.fetchHourlyForecastsForNext24Hours(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ).getOrNull() // todo : exception handling
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(300),
                initialValue = null
            )

    private val weatherDetailsOfSavedLocationsResults: Flow<Result<List<CurrentWeatherDetails>>> =
        weatherRepository.getSavedLocationsListStream()
            .map { savedLocations ->
                isLoadingSavedLocations.value = true
                fetchCurrentWeatherDetailsWithCache(savedLocations.toSet())
                    .also { isLoadingSavedLocations.value = false }
            }

    // to understand why this flow is converted into a state flow, see the explanation
    // above the uiState property below.
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val autofillSuggestionResults: Flow<Result<List<LocationAutofillSuggestion>>> =
        currentSearchQuery.debounce(250)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .mapLatest { query ->
                isLoadingAutofillSuggestions.value = true
                locationServicesRepository.fetchSuggestedPlacesForQuery(query)
                    .also { isLoadingAutofillSuggestions.value = false }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(300),
                initialValue = Result.success(emptyList())
            )

    // IMPORTANT NOTE ABOUT THE COMBINE OPERATOR
    // By default, the combine operator waits for all flows to emit at least one value before it
    // starts combining them. So, the first call of the combine operator's transform block will happen
    // only when all the flows passed to the combine block have emitted at least a single value.
    //
    // For StateFlows, the initial value would be taken as the first emission. Since a normal
    // flow doesn't store a value in it, the combine block waits for the first emission before
    // calling the transform block for the first time. This implies that any update to either
    // state flows (marked below) will not get passed to the transform block unless the
    // other normal flows (marked below) emit at least one value.
    val uiState = combine(
        isLoadingSavedLocations, // state flow
        isLoadingAutofillSuggestions, // state flow
        weatherDetailsOfSavedLocationsResults, // flow
        autofillSuggestionResults // flow converted to stateflow because this flow doesn't emit until the user starts searching
    ) { isLoadingSavedLocations, isLoadingAutofillSuggestions, weatherDetailsOfSavedLocationsResults, autofillSuggestionResults ->
        val autofillSuggestions = autofillSuggestionResults.getOrNull() ?: emptyList()
        val savedLocations = weatherDetailsOfSavedLocationsResults.getOrNull()
            ?.map { it.toBriefWeatherDetails() }
            ?.sortedBy { it.nameOfLocation } ?: emptyList()
        HomeScreenUiState(
            isLoadingSuggestions = isLoadingAutofillSuggestions,
            isLoadingSavedLocations = isLoadingSavedLocations,
            autofillSuggestions = autofillSuggestions,
            weatherDetailsOfSavedLocations = savedLocations
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(300),
        initialValue = HomeScreenUiState(isLoadingSavedLocations = true)
    )


    // a cache that stores the CurrentWeatherDetails of a specific SavedLocation
    private var currentWeatherDetailsCache = mutableMapOf<SavedLocation, CurrentWeatherDetails>()
    private var recentlyDeletedItem: BriefWeatherDetails? = null

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
    private suspend fun fetchCurrentWeatherDetailsWithCache(savedLocations: Set<SavedLocation>): Result<List<CurrentWeatherDetails>> {
        // remove locations in the cache that have been deleted by the user
        val removedLocations = currentWeatherDetailsCache.keys subtract savedLocations
        for (removedLocation in removedLocations) {
            currentWeatherDetailsCache.remove(removedLocation)
        }
        // only fetch weather details of the items that are not in cache.
        val locationsNotInCache = savedLocations subtract currentWeatherDetailsCache.keys
        for (savedLocationNotInCache in locationsNotInCache) {
            currentWeatherDetailsCache[savedLocationNotInCache] =
                weatherRepository.fetchWeatherForLocation(
                    nameOfLocation = savedLocationNotInCache.nameOfLocation,
                    latitude = savedLocationNotInCache.coordinates.latitude,
                    longitude = savedLocationNotInCache.coordinates.longitude
                ).getOrElse { return Result.failure(it) }
        }
        return Result.success(currentWeatherDetailsCache.values.toList())
    }

    fun fetchWeatherForCurrentUserLocation() {
        viewModelScope.launch {
            val coordinatesResult =
                currentLocationProvider.getCurrentLocation().getOrNull() ?: return@launch
            coordinatesOfCurrentLocation.value = coordinatesResult
        }

    }
}