package com.example.justweather.ui.weather

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.domain.models.CurrentWeatherDetails
import com.example.justweather.domain.models.HourlyForecast
import com.example.justweather.domain.models.PrecipitationProbability
import com.example.justweather.ui.navigation.JustWeatherNavigationDestinations.WeatherDetailScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
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

    private val _weatherDetailsOfChosenLocation = MutableStateFlow<CurrentWeatherDetails?>(null)
    val weatherDetailsOfChosenLocation =
        _weatherDetailsOfChosenLocation as StateFlow<CurrentWeatherDetails?>

    val isSavedLocation = weatherRepository.getWeatherStreamForPreviouslySavedLocations()
        .map { savedWeatherDetails ->
            savedWeatherDetails.any { it.nameOfLocation == weatherDetailsOfChosenLocation.value?.nameOfLocation }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 250L),
            initialValue = true
        )

    private val _precipitationProbabilityList =
        MutableStateFlow<List<PrecipitationProbability>>(emptyList())
    val precipitationProbabilityList =
        _precipitationProbabilityList as StateFlow<List<PrecipitationProbability>>

    private val _hourlyForecastList = MutableStateFlow<List<HourlyForecast>>(emptyList())
    val hourlyForecastList = _hourlyForecastList as StateFlow<List<HourlyForecast>>

    private val _uiState = MutableStateFlow(UiState.IDLE)
    val uiState = _uiState as StateFlow<UiState>

    init {
        viewModelScope.launch {
            fetchWeatherInfo()
            fetchAndAssignPrecipitationForecasts()
            fetchAndAssignHourlyForecasts()
        }
    }

    /**
     * Fetches weather information from the repository, ensuring that the [uiState] is
     * correctly updated.
     */
    private suspend fun fetchWeatherInfo() {
        _uiState.value = UiState.LOADING
        val weatherDetails = weatherRepository.fetchWeatherForLocation(
            nameOfLocation = nameOfLocation,
            latitude = latitude,
            longitude = longitude
        ).getOrNull()?.also { _weatherDetailsOfChosenLocation.value = it }

        _uiState.value = if (weatherDetails == null) UiState.ERROR
        else UiState.IDLE
    }

    private suspend fun fetchAndAssignPrecipitationForecasts() {
        val hourlyPrecipitationProbabilities =
            weatherRepository.getHourlyPrecipitationProbabilities(
                latitude = latitude,
                longitude = longitude,
                dateRange = LocalDate.now()..LocalDate.now().plusDays(1)
            ).getOrNull() ?: return
        val precipitationProbabilitiesForTheNext24hours = hourlyPrecipitationProbabilities.filter {
            val isSameDay = it.dateTime == LocalDateTime.now()
            if (isSameDay) it.dateTime.toLocalTime() >= LocalTime.now()
            else it.dateTime > LocalDateTime.now()
        }.take(24)
        _precipitationProbabilityList.value = precipitationProbabilitiesForTheNext24hours
    }

    private suspend fun fetchAndAssignHourlyForecasts() {
        val hourlyForecasts = weatherRepository.fetchHourlyForecasts(
            latitude = latitude,
            longitude = longitude,
            dateRange = LocalDate.now()..LocalDate.now().plusDays(1)
        ).getOrNull() ?: return
        val hourlyForecastsForTheNext24hours = hourlyForecasts.filter {
            val isSameDay = it.dateTime == LocalDateTime.now()
            if (isSameDay) it.dateTime.toLocalTime() >= LocalTime.now()
            else it.dateTime > LocalDateTime.now()
        }.take(24)
        _hourlyForecastList.value = hourlyForecastsForTheNext24hours
    }

    fun addLocationToSavedLocations() {
        if (weatherDetailsOfChosenLocation.value == null) return
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            weatherRepository.saveWeatherLocation(
                nameOfLocation = weatherDetailsOfChosenLocation.value!!.nameOfLocation,
                latitude = latitude,
                longitude = longitude
            )
            _uiState.value = UiState.IDLE
        }
    }

    /**
     * An enum class that contains all possible UI states.
     */
    enum class UiState {
        IDLE,
        LOADING,
        ERROR
    }
}