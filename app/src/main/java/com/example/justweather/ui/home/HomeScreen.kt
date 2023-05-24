package com.example.justweather.ui.home

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
import com.example.justweather.domain.models.BriefWeatherDetails

/**
 * A home screen composable that displays a search bar with a list containing the current weather for
 * saved locations.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param weatherDetailsOfSavedLocations The list of weather details of saved locations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherDetailsOfSavedLocations: List<BriefWeatherDetails>
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
                isSearchBarActive = isSearchBarActive,
                onSearchQueryChange = { currentQueryText = it },
                onSearchBarActiveChange = { isSearchBarActive = it },
                onSearch = {/* TODO: handle search */ },
                searchBarSuggestionsContent = { /* TODO: search bar suggestions content */ }
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
@ExperimentalMaterial3Api
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    currentSearchQuery: String,
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
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            placeholder = { Text(text = "Search for a location") },
            content = searchBarSuggestionsContent
        )
    }
}

