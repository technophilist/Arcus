package com.example.arcus.data.repositories.location

import com.example.arcus.data.getBodyOrThrowException
import com.example.arcus.data.remote.location.LocationClient
import com.example.arcus.domain.models.LocationAutofillSuggestion
import com.example.arcus.domain.models.toLocationAutofillSuggestionList
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * A concrete implementation of [LocationServicesRepository].
 */
class ArcusLocationServicesRepository @Inject constructor(
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