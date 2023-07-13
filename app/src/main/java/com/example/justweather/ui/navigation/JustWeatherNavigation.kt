package com.example.justweather.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.justweather.R
import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.LocationAutofillSuggestion
import com.example.justweather.domain.models.SingleWeatherDetail
import com.example.justweather.ui.home.HomeScreen
import com.example.justweather.ui.home.HomeViewModel
import com.example.justweather.ui.weather.WeatherDetailViewModel
import com.example.justweather.ui.weather.WeatherDetailScreen
import kotlin.math.roundToInt


@Composable
fun JustWeatherNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = JustWeatherNavigationDestinations.HomeScreen.route
    ) {

        homeScreen(
            route = JustWeatherNavigationDestinations.HomeScreen.route,
            onSuggestionClick = {
                navController.navigateToWeatherDetailScreen(
                    nameOfLocation = it.nameOfLocation,
                    latitude = it.coordinatesOfLocation.latitude,
                    longitude = it.coordinatesOfLocation.longitude
                )
            },
            onSavedLocationItemClick = {
                navController.navigateToWeatherDetailScreen(
                    nameOfLocation = it.nameOfLocation,
                    latitude = it.latitude,
                    longitude = it.longitude
                )
            }
        )

        weatherDetailScreen(
            route = JustWeatherNavigationDestinations.WeatherDetailScreen.route,
            onBackButtonClick = navController::popBackStack
        )
    }
}

private fun NavGraphBuilder.homeScreen(
    route: String,
    onSuggestionClick: (suggestion: LocationAutofillSuggestion) -> Unit,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit
) {
    composable(route = route) {
        val viewModel = hiltViewModel<HomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val suggestionsForCurrentQuery by viewModel.currentSuggestions
            .collectAsStateWithLifecycle(initialValue = emptyList())
        val weatherDetailsOfSavedLocations by viewModel.weatherDetailsOfSavedLocations
            .collectAsStateWithLifecycle()

        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            weatherDetailsOfSavedLocations = weatherDetailsOfSavedLocations,
            suggestionsForSearchQuery = suggestionsForCurrentQuery,
            isSuggestionsListLoading = uiState == HomeViewModel.UiState.LOADING_SUGGESTIONS,
            isWeatherForSavedLocationsLoading = uiState == HomeViewModel.UiState.LOADING_SAVED_LOCATIONS,
            onSuggestionClick = onSuggestionClick,
            onSearchQueryChange = viewModel::setSearchQueryForSuggestionsGeneration,
            onSavedLocationItemClick = onSavedLocationItemClick,
            onSavedLocationDismissed = viewModel::deleteSavedWeatherLocation
        )
    }
}

fun NavGraphBuilder.weatherDetailScreen(
    route: String,
    onBackButtonClick: () -> Unit
) {
    composable(route) {
        val viewModel = hiltViewModel<WeatherDetailViewModel>()
        val weatherDetails by viewModel.weatherDetailsOfChosenLocation.collectAsStateWithLifecycle()
        val isSavedLocation by viewModel.isSavedLocation.collectAsStateWithLifecycle()
        val precipitationProbabilityList by viewModel.precipitationProbabilityList.collectAsStateWithLifecycle()
        val hourlyForecastList by viewModel.hourlyForecastList.collectAsStateWithLifecycle()

        WeatherDetailScreen(
            nameOfLocation = weatherDetails?.nameOfLocation ?: "- -",
            weatherConditionImage = weatherDetails?.imageResId ?: R.drawable.ic_day_clear, // todo
            weatherConditionIconId = weatherDetails?.iconResId ?: R.drawable.ic_day_clear,
            weatherInDegrees = weatherDetails?.temperature?.toFloat()?.roundToInt() ?: 0,
            weatherCondition = weatherDetails?.weatherCondition ?: "- -",
            onBackButtonClick = onBackButtonClick,
            isPreviouslySavedLocation = isSavedLocation,
            onSaveButtonClick = viewModel::addLocationToSavedLocations,
            singleWeatherDetails = List(5) { //todo
                SingleWeatherDetail("Test", value = "1", R.drawable.ic_wind_pressure)
            },
            hourlyForecasts = hourlyForecastList,
            precipitationProbabilities = precipitationProbabilityList
        )
    }
}

private fun NavHostController.navigateToWeatherDetailScreen(
    nameOfLocation: String,
    latitude: String,
    longitude: String
) {
    val destination = JustWeatherNavigationDestinations.WeatherDetailScreen.buildRoute(
        nameOfLocation = nameOfLocation,
        latitude = latitude,
        longitude = longitude
    )
    navigate(destination)
}
