package com.example.arcus.ui.navigation

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
import com.example.arcus.domain.models.weather.BriefWeatherDetails
import com.example.arcus.domain.models.location.LocationAutofillSuggestion
import com.example.arcus.ui.home.HomeScreen
import com.example.arcus.ui.home.HomeViewModel
import com.example.arcus.ui.weatherdetail.WeatherDetailViewModel
import com.example.arcus.ui.weatherdetail.WeatherDetailScreen
import kotlinx.coroutines.launch


@Composable
fun ArcusNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = ArcusNavigationDestinations.HomeScreen.route
    ) {

        homeScreen(
            route = ArcusNavigationDestinations.HomeScreen.route,
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
                    latitude = it.coordinates.latitude,
                    longitude = it.coordinates.longitude
                )
            }
        )

        weatherDetailScreen(
            route = ArcusNavigationDestinations.WeatherDetailScreen.route,
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
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val showSnackbar = { briefWeatherDetails: BriefWeatherDetails ->
            coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = "${briefWeatherDetails.nameOfLocation} has been deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    viewModel.restoreRecentlyDeletedItem()
                }
            }
        }

        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            homeScreenUiState = uiState,
            snackbarHostState = snackbarHostState,
            onSavedLocationDismissed = {
                viewModel.deleteSavedWeatherLocation(it)
                showSnackbar(it)
            },
            onSearchQueryChange = viewModel::setSearchQueryForSuggestionsGeneration,
            onSuggestionClick = onSuggestionClick,
            onSavedLocationItemClick = onSavedLocationItemClick,
            onLocationPermissionGranted = viewModel::fetchWeatherForCurrentUserLocation,
            onRetryFetchingWeatherForSavedLocations = viewModel::retryFetchingSavedLocations
        )
    }
}

fun NavGraphBuilder.weatherDetailScreen(
    route: String,
    onBackButtonClick: () -> Unit
) {
    composable(route) {
        val viewModel = hiltViewModel<WeatherDetailViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        WeatherDetailScreen(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackButtonClick = onBackButtonClick,
            onSaveButtonClick = {
                viewModel.addLocationToSavedLocations()
                snackbarHostState.currentSnackbarData?.dismiss()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message = "Added to saved locations")
                }
            }
        )
    }
}

private fun NavHostController.navigateToWeatherDetailScreen(
    nameOfLocation: String,
    latitude: String,
    longitude: String
) {
    val destination = ArcusNavigationDestinations.WeatherDetailScreen.buildRoute(
        nameOfLocation = nameOfLocation,
        latitude = latitude,
        longitude = longitude
    )
    navigate(destination) { launchSingleTop = true }
}
