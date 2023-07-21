package com.example.justweather.data.repositories.location

import com.example.justweather.data.getBodyOrThrowException
import com.example.justweather.data.remote.location.LocationClient
import com.example.justweather.data.remote.location.models.toLocationAutofillSuggestionList
import com.example.justweather.domain.models.LocationAutofillSuggestion
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * A concrete implementation of [LocationServicesRepository].
 */
class JustWeatherLocationServicesRepository @Inject constructor(
    private val locationClient: LocationClient
) : LocationServicesRepository {

    override suspend fun fetchSuggestedPlacesForQuery(query: String): Result<List<LocationAutofillSuggestion>> {
        return try {
            if (query.isBlank()) return Result.success(emptyList())
            val suggestions = locationClient.getPlacesSuggestionsForQuery(query = query)
                .getBodyOrThrowException()
                .suggestions
                .toLocationAutofillSuggestionList()
            Result.success(suggestions)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }
}