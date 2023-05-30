package com.example.justweather.ui.home

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import com.example.justweather.domain.models.BriefWeatherDetails
import com.example.justweather.domain.models.LocationAutofillSuggestion
import com.example.justweather.ui.components.AutofillSuggestion

/**
 * A home screen composable that displays a search bar with a list containing the current weather for
 * saved locations.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param weatherDetailsOfSavedLocations The list of weather details of saved locations.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherDetailsOfSavedLocations: List<BriefWeatherDetails>,
    suggestionsForSearchQuery: List<LocationAutofillSuggestion>,
    isSuggestionsListLoading: Boolean = false,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    var isSearchBarActive by remember { mutableStateOf(false) }
    var currentQueryText by remember { mutableStateOf("") }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(
                modifier = Modifier.fillMaxWidth(),
                currentSearchQuery = currentQueryText,
                onClearSearchQueryIconClick = {
                    currentQueryText = ""
                    onSearchQueryChange("")
                },
                isSearchBarActive = isSearchBarActive,
                onSearchQueryChange = {
                    currentQueryText = it
                    onSearchQueryChange(it)
                },
                onSearchBarActiveChange = { isSearchBarActive = it },
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

        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Saved Locations",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal
            )
        }

        items(weatherDetailsOfSavedLocations) {
            CompactWeatherCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                nameOfLocation = it.nameOfLocation,
                shortDescription = it.shortDescription,
                shortDescriptionIcon = it.shortDescriptionIcon,
                weatherInDegrees = it.currentTemperature,
                onClick = { /*TODO*/ }
            )
        }
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
                    onBackIconClick = { onSearchBarActiveChange(false) }
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
                if (isActive) AnimatedContentScope.SlideDirection.Start
                else AnimatedContentScope.SlideDirection.End
            )
            val slideOut = slideOutOfContainer(
                if (isActive) AnimatedContentScope.SlideDirection.Start
                else AnimatedContentScope.SlideDirection.End
            )
            slideIn with slideOut
        }
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
            onClick = { onSuggestionClick(it) }
        )
    }
}

