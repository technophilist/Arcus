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
import com.example.justweather.R
import com.example.justweather.domain.models.BriefWeatherDetails

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

@ExperimentalMaterial3Api
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    currentSearchQuery: String,
    isSearchBarActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchBarActiveChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    searchBarSuggestionsContent: @Composable() (ColumnScope.() -> Unit)
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(modifier = modifier) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .sizeIn(
                    maxWidth = screenWidth,
                    maxHeight = screenHeight // todo explain
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

