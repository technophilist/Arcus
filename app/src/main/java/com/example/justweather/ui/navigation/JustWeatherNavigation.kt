package com.example.justweather.ui.navigation

import android.util.Log.d
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
import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.LocationAutofillSuggestion
import com.example.justweather.ui.home.HomeScreen
import com.example.justweather.ui.home.HomeViewModel
import com.example.justweather.ui.weather.WeatherDetailScreen
import com.example.justweather.ui.weather.WeatherDetailViewModel
import timber.log.Timber


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
                    latitude = it.coordinatesOfLocation.latitude,
                    longitude = it.coordinatesOfLocation.longitude,
                    wasLocationPreviouslySaved = false
                )
            },
            onSavedLocationItemClick = {
                navController.navigateToWeatherDetailScreen(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    wasLocationPreviouslySaved = true
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
    composable(route) { backStackEntry ->
        val viewModel = hiltViewModel<WeatherDetailViewModel>()
        val weatherDetails by viewModel.weatherDetailsOfChosenLocation.collectAsStateWithLifecycle()
        val argumentKey =
            JustWeatherNavigationDestinations.WeatherDetailScreen.NAV_ARG_WAS_LOCATION_PREVIOUSLY_SAVED
        val wasLocationPreviouslySaved =
            backStackEntry.arguments!!.getString(argumentKey).toBoolean()
        WeatherDetailScreen(
            background = { }, // todo
            weatherDetails = weatherDetails,
            modifier = Modifier.fillMaxSize(),
            onBackButtonClick = onBackButtonClick,
            onAddButtonClick = viewModel::addLocationToSavedLocations,
            wasLocationPreviouslySaved = wasLocationPreviouslySaved
        )
    }
}

private fun NavHostController.navigateToWeatherDetailScreen(
    latitude: String,
    longitude: String,
    wasLocationPreviouslySaved: Boolean
) {
    val destination = JustWeatherNavigationDestinations.WeatherDetailScreen.buildRoute(
        latitude = latitude,
        longitude = longitude,
        wasLocationPreviouslySaved = wasLocationPreviouslySaved
    )
    navigate(destination)
}
