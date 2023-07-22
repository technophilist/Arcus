package com.example.justweather.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.justweather.ui.home.HomeScreen
import com.example.justweather.ui.home.HomeViewModel
import com.example.justweather.ui.weatherDetail.WeatherDetailViewModel
import com.example.justweather.ui.weatherDetail.WeatherDetailScreen
import kotlinx.coroutines.launch
import timber.log.Timber
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
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            weatherDetailsOfSavedLocations = weatherDetailsOfSavedLocations,
            suggestionsForSearchQuery = suggestionsForCurrentQuery,
            isSuggestionsListLoading = uiState == HomeViewModel.UiState.LOADING_SUGGESTIONS,
            isWeatherForSavedLocationsLoading = uiState == HomeViewModel.UiState.LOADING_SAVED_LOCATIONS,
            onSuggestionClick = onSuggestionClick,
            onSearchQueryChange = viewModel::setSearchQueryForSuggestionsGeneration,
            onSavedLocationItemClick = onSavedLocationItemClick,
            onSavedLocationDismissed = {
                viewModel.deleteSavedWeatherLocation(it)
                snackbarHostState.currentSnackbarData?.dismiss()
                coroutineScope.launch {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = "${it.nameOfLocation} has been deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) {
                        viewModel.restoreRecentlyDeletedItem()
                    }
                }
            },
            snackbarHostState = snackbarHostState
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
        val singleWeatherDetailList by viewModel.additionalWeatherDetailsList.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        WeatherDetailScreen(
            currentWeatherDetails = weatherDetails,
            onBackButtonClick = onBackButtonClick,
            isPreviouslySavedLocation = isSavedLocation,
            onSaveButtonClick = {
                viewModel.addLocationToSavedLocations()
                snackbarHostState.currentSnackbarData?.dismiss()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message = "Added to saved locations")
                }
            },
            singleWeatherDetails = singleWeatherDetailList,
            hourlyForecasts = hourlyForecastList,
            precipitationProbabilities = precipitationProbabilityList,
            snackbarHostState = snackbarHostState
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
