package com.example.justweather.ui.home

import androidx.lifecycle.ViewModel
import com.example.justweather.data.repositories.location.LocationServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationServicesRepository: LocationServicesRepository
) : ViewModel() {

    private val currentSearchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(UiState.IDLE)
    val uiState = _uiState as StateFlow<UiState>

    @OptIn(FlowPreview::class)
    val currentSuggestions = currentSearchQuery.debounce(250)
        .map { query ->
            _uiState.value = UiState.LOADING_SUGGESTIONS
            locationServicesRepository.fetchSuggestedPlacesForQuery(query)
                .also { _uiState.value = UiState.IDLE }
        }
        .filter { it.isSuccess }
        .map { it.getOrThrow() }

    /**
     * Used to set the [searchQuery] for which the suggestions should be generated.
     */
    fun setSearchQueryForSuggestionsGeneration(searchQuery: String) {
        currentSearchQuery.value = searchQuery
    }

    /**
     * An enum that contains all possible UI states.
     */
    enum class UiState { IDLE, LOADING_SUGGESTIONS }
}