package com.example.arcus.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.arcus.domain.models.weather.BriefWeatherDetails
import com.example.arcus.domain.models.weather.HourlyForecast
import com.example.arcus.domain.models.location.LocationAutofillSuggestion
import com.example.arcus.ui.components.AutofillSuggestion
import com.example.arcus.ui.components.CompactWeatherCardWithHourlyForecast
import com.example.arcus.ui.components.SwipeToDismissCompactWeatherCard
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import com.google.accompanist.placeholder.placeholder


/**
 * Overload that uses [HomeScreenUiState].
 */
@Composable
fun HomeScreen(
    homeScreenUiState: HomeScreenUiState,
    snackbarHostState: SnackbarHostState,
    onSavedLocationDismissed: (BriefWeatherDetails) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit,
    onLocationPermissionGranted: () -> Unit,
    modifier: Modifier = Modifier,
    onRetryFetchingWeatherForCurrentLocation: () -> Unit = onLocationPermissionGranted
) {
    HomeScreen(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        weatherDetailsOfSavedLocations = homeScreenUiState.weatherDetailsOfSavedLocations,
        suggestionsForSearchQuery = homeScreenUiState.autofillSuggestions,
        isSuggestionsListLoading = homeScreenUiState.isLoadingSuggestions,
        isCurrentWeatherDetailsLoading = homeScreenUiState.isLoadingWeatherDetailsOfCurrentLocation,
        isWeatherForSavedLocationsLoading = homeScreenUiState.isLoadingSavedLocations,
        weatherOfCurrentUserLocation = homeScreenUiState.weatherDetailsOfCurrentLocation,
        hourlyForecastsOfCurrentUserLocation = homeScreenUiState.hourlyForecastsForCurrentLocation,
        errorFetchingWeatherForCurrentLocation = homeScreenUiState.errorFetchingWeatherForCurrentLocation,
        onRetryFetchingWeatherForCurrentLocation = onRetryFetchingWeatherForCurrentLocation,
        onSavedLocationDismissed = onSavedLocationDismissed,
        onSearchQueryChange = onSearchQueryChange,
        onSuggestionClick = onSuggestionClick,
        onSavedLocationItemClick = onSavedLocationItemClick,
        onLocationPermissionGranted = onLocationPermissionGranted
    )
}

/**
 * A home screen composable that displays a search bar with a list containing the current weather for
 * saved locations.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param weatherDetailsOfSavedLocations The list of weather details of saved locations.
 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherDetailsOfSavedLocations: List<BriefWeatherDetails>,
    suggestionsForSearchQuery: List<LocationAutofillSuggestion>,
    weatherOfCurrentUserLocation: BriefWeatherDetails?,
    hourlyForecastsOfCurrentUserLocation: List<HourlyForecast>?,
    isSuggestionsListLoading: Boolean = false,
    isWeatherForSavedLocationsLoading: Boolean = false,
    isCurrentWeatherDetailsLoading: Boolean,
    errorFetchingWeatherForCurrentLocation: Boolean,
    onRetryFetchingWeatherForCurrentLocation: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit,
    onSavedLocationDismissed: (BriefWeatherDetails) -> Unit,
    onLocationPermissionGranted: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var isSearchBarActive by remember { mutableStateOf(false) }
    var currentQueryText by remember { mutableStateOf("") }
    val clearQueryText = {
        currentQueryText = ""
        onSearchQueryChange("")
    }
    var shouldDisplayCurrentLocationWeatherSubHeader by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { isPermitted ->
            val isCoarseLocationPermitted =
                isPermitted.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false)
            val isFineLocationPermitted =
                isPermitted.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false)
            if (isCoarseLocationPermitted || isFineLocationPermitted) {
                shouldDisplayCurrentLocationWeatherSubHeader = true
                onLocationPermissionGranted()
            }
        }
    )
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
    Box {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            searchBarItem(
                currentSearchQuery = currentQueryText,
                onClearSearchQueryIconClick = clearQueryText,
                isSearchBarActive = isSearchBarActive,
                onSearchQueryChange = {
                    currentQueryText = it
                    onSearchQueryChange(it)
                },
                onSearchBarActiveChange = { isSearchBarActive = it },
                suggestionsForSearchQuery = suggestionsForSearchQuery,
                isSuggestionsListLoading = isSuggestionsListLoading,
                onSuggestionClick = onSuggestionClick
            )

            if (shouldDisplayCurrentLocationWeatherSubHeader) {
                subHeaderItem(
                    title = "Current Location",
                    isLoadingAnimationVisible = isCurrentWeatherDetailsLoading
                )
            }

            if (weatherOfCurrentUserLocation != null && hourlyForecastsOfCurrentUserLocation != null) {
                currentWeatherDetailCardItem(
                    weatherOfCurrentUserLocation = weatherOfCurrentUserLocation,
                    hourlyForecastsOfCurrentUserLocation = hourlyForecastsOfCurrentUserLocation,
                    onClick = { onSavedLocationItemClick(weatherOfCurrentUserLocation) }
                )
            }

            if (errorFetchingWeatherForCurrentLocation) {
                item {
                    CurrentWeatherForLocationErrorCard(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .animateItemPlacement(),
                        onRetryButtonClick = onRetryFetchingWeatherForCurrentLocation
                    )
                }
            }

            subHeaderItem(
                title = "Saved Locations",
                isLoadingAnimationVisible = isWeatherForSavedLocationsLoading
            )

            savedLocationItems(
                savedLocationItemsList = weatherDetailsOfSavedLocations,
                onSavedLocationItemClick = onSavedLocationItemClick,
                onSavedLocationDismissed = onSavedLocationDismissed
            )
        }
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            hostState = snackbarHostState
        )
    }
}

/**
 * A composable that contains a centered [SearchBar] meant to be used in the lazy column defined in
 * [HomeScreen].
 *
 * Note: In this composable the [SearchBar]'s max height and width are constrained to the max height
 * and width of the screen. Using it in a lazy column using [LazyListScope.item], will cause the app
 * to crash. This is because the width of the [SearchBar], when expanded is set to infinity. A composable
 * of width infinity, in a lazy column, will make the app crash. Hence, the size is explicitly
 * constrained. This might be a bug, and might be fixed in the future.
 */
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    currentSearchQuery: String,
    onClearSearchQueryIconClick: () -> Unit,
    isSearchBarActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchBarActiveChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    searchBarSuggestionsContent: @Composable (ColumnScope.() -> Unit)
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(modifier = modifier) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .sizeIn(
                    maxWidth = screenWidth, // see docs for explanation
                    maxHeight = screenHeight // see docs for explanation
                ),
            query = currentSearchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = onSearch,
            active = isSearchBarActive,
            onActiveChange = onSearchBarActiveChange,
            leadingIcon = {
                AnimatedSearchBarLeadingIcon(
                    isSearchBarActive = isSearchBarActive,
                    onSearchIconClick = { onSearchBarActiveChange(true) },
                    onBackIconClick = {
                        // clear search query text when clicking on the back button
                        onClearSearchQueryIconClick()
                        onSearchBarActiveChange(false)
                    }
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = isSearchBarActive,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it })
                ) {
                    val iconImageVector = Icons.Filled.Close
                    IconButton(
                        onClick = onClearSearchQueryIconClick,
                        content = { Icon(imageVector = iconImageVector, contentDescription = null) }
                    )
                }
            },
            placeholder = { Text(text = "Search for a location") },
            content = searchBarSuggestionsContent
        )
    }
}

/**
 * This composable creates an animated search bar leading icon, switching between a back button
 * icon and a search buttin icon based on the [isSearchBarActive] state.
 *
 * @param isSearchBarActive Indicates whether the search bar is active or not.
 * @param onSearchIconClick The callback that will be executed when the search icon is clicked.
 * @param onBackIconClick The callback that will be executed when the back icon is clicked.
 */
@ExperimentalAnimationApi
@Composable
private fun AnimatedSearchBarLeadingIcon(
    isSearchBarActive: Boolean,
    onSearchIconClick: () -> Unit,
    onBackIconClick: () -> Unit
) {
    AnimatedContent(
        targetState = isSearchBarActive,
        transitionSpec = {
            val isActive = this.targetState
            val slideIn = slideIntoContainer(
                if (isActive) AnimatedContentTransitionScope.SlideDirection.Start
                else AnimatedContentTransitionScope.SlideDirection.End
            )
            val slideOut = slideOutOfContainer(
                if (isActive) AnimatedContentTransitionScope.SlideDirection.Start
                else AnimatedContentTransitionScope.SlideDirection.End
            )
            slideIn togetherWith slideOut
        }, label = ""
    ) { isActive ->
        if (isActive) {
            IconButton(
                onClick = onBackIconClick,
                content = { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null) }
            )
        } else {
            IconButton(
                onClick = onSearchIconClick,
                content = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) }
            )
        }
    }
}

@Composable
private fun AutoFillSuggestionsList(
    suggestions: List<LocationAutofillSuggestion>,
    isSuggestionsListLoading: Boolean,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isSuggestionsListLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn {
                autofillSuggestionItems(
                    suggestions = suggestions,
                    onSuggestionClick = onSuggestionClick
                )
                item {
                    Spacer(modifier = Modifier.imePadding())
                }
            }
        }
    }
}

private fun LazyListScope.autofillSuggestionItems(
    suggestions: List<LocationAutofillSuggestion>,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
) {
    items(items = suggestions, key = { it.idOfLocation }) {
        AutofillSuggestion(
            title = it.nameOfLocation,
            subText = it.addressOfLocation,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = { onSuggestionClick(it) },
            leadingIcon = { AutofillSuggestionLeadingIcon(countryFlagUrl = it.countryFlagUrl) }
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
private fun LazyListScope.savedLocationItems(
    savedLocationItemsList: List<BriefWeatherDetails>,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit,
    onSavedLocationDismissed: (BriefWeatherDetails) -> Unit
) {
    items(
        items = savedLocationItemsList,
        key = { it.nameOfLocation } // swipeable cards will be buggy without keys
    ) {
        // The default "rememberDismissState" uses "rememberSaveable" under the hood.
        // This is an issue because the swiped state gets restored when the item is removed
        // and added back to the list.
        // If an item gets removed (after getting swiped) and is added back to the list,
        // the item's state would still be set to "swiped" because the state got saved in
        // savedInstanceState by rememberSaveable.
        val dismissState = remember {
            DismissState(
                initialValue = DismissValue.Default,
                confirmValueChange = { dismissValue ->
                    if (dismissValue == DismissValue.DismissedToStart) {
                        onSavedLocationDismissed(it)
                        true
                    } else {
                        false
                    }
                }
            )
        }
        SwipeToDismissCompactWeatherCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .animateItemPlacement(),
            nameOfLocation = it.nameOfLocation,
            shortDescription = it.shortDescription,
            shortDescriptionIcon = it.shortDescriptionIcon,
            weatherInDegrees = it.currentTemperatureRoundedToInt.toString(),
            onClick = { onSavedLocationItemClick(it) },
            dismissState = dismissState
        )
    }
}

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
private fun LazyListScope.searchBarItem(
    currentSearchQuery: String,
    isSearchBarActive: Boolean,
    isSuggestionsListLoading: Boolean,
    suggestionsForSearchQuery: List<LocationAutofillSuggestion>,
    onClearSearchQueryIconClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchBarActiveChange: (Boolean) -> Unit,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit
) {
    item {
        Header(
            modifier = Modifier.fillMaxWidth(),
            currentSearchQuery = currentSearchQuery,
            onClearSearchQueryIconClick = onClearSearchQueryIconClick,
            isSearchBarActive = isSearchBarActive,
            onSearchQueryChange = onSearchQueryChange,
            onSearchBarActiveChange = onSearchBarActiveChange,
            onSearch = {/* TODO: handle search */ },
            searchBarSuggestionsContent = {
                AutoFillSuggestionsList(
                    suggestions = suggestionsForSearchQuery,
                    onSuggestionClick = onSuggestionClick,
                    isSuggestionsListLoading = isSuggestionsListLoading
                )
            }
        )
    }
}

private fun LazyListScope.subHeaderItem(title: String, isLoadingAnimationVisible: Boolean) {
    item {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(end = 8.dp),
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal
            )
            if (isLoadingAnimationVisible) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@ExperimentalFoundationApi
private fun LazyListScope.currentWeatherDetailCardItem(
    weatherOfCurrentUserLocation: BriefWeatherDetails,
    hourlyForecastsOfCurrentUserLocation: List<HourlyForecast>,
    onClick: () -> Unit,
) {
    item {
        CompactWeatherCardWithHourlyForecast(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .animateItemPlacement(),
            nameOfLocation = weatherOfCurrentUserLocation.nameOfLocation,
            shortDescription = weatherOfCurrentUserLocation.shortDescription,
            shortDescriptionIcon = weatherOfCurrentUserLocation.shortDescriptionIcon,
            weatherInDegrees = weatherOfCurrentUserLocation.currentTemperatureRoundedToInt.toString(),
            onClick = onClick,
            hourlyForecasts = hourlyForecastsOfCurrentUserLocation
        )
    }
}

@Composable
private fun AutofillSuggestionLeadingIcon(countryFlagUrl: String) {
    val context = LocalContext.current
    val imageRequest = remember(countryFlagUrl) {
        ImageRequest.Builder(context)
            .data(countryFlagUrl)
            .decoderFactory(SvgDecoder.Factory())
            .build()
    }
    var isLoadingAnimationVisible by remember { mutableStateOf(false) }
    AsyncImage(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .placeholder(
                visible = isLoadingAnimationVisible,
                highlight = PlaceholderHighlight.shimmer()
            ),
        model = imageRequest,
        contentDescription = null,
        onState = { asyncPainterState ->
            isLoadingAnimationVisible = asyncPainterState is AsyncImagePainter.State.Loading
        }
    )
}

@Composable
private fun CurrentWeatherForLocationErrorCard(
    modifier: Modifier = Modifier,
    onRetryButtonClick: () -> Unit
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "An error occurred when fetching the weather for the current location.",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedButton(onClick = onRetryButtonClick, content = { Text(text = "Retry") })
        }
    }
}
