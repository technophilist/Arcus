package com.example.justweather.data.repositories.location

import com.example.justweather.BuildConfig
import com.example.justweather.data.remote.location.LocationClient
import com.example.justweather.data.remote.location.models.coordinates
import com.example.justweather.data.remote.location.models.toLocationAutofillSuggestionList
import com.example.justweather.domain.models.LocationAutofillSuggestion
import kotlinx.coroutines.CancellationException
import java.util.UUID
import javax.inject.Inject

/**
 * A concrete implementation of [LocationServicesRepository].
 */
class JustWeatherLocationServicesRepository @Inject constructor(
    private val locationClient: LocationClient
) : LocationServicesRepository {

    override suspend fun fetchSuggestedPlacesForQuery(query: String): Result<List<LocationAutofillSuggestion>> {
        return if (query.isBlank()) Result.success(emptyList())
        else try {
            val sessionToken = UUID.randomUUID().toString()
            // non-null assertions to body() calls are fine here because, if the body is empty,
            // it essentially means that there was some error during the fetch operation. The
            // null pointer exception will get caught by the enclosing try catch block.
            val suggestionCoordinatePairs = locationClient.getPlacesSuggestionsForQuery(
                query = query,
                accessToken = BuildConfig.MAP_BOX_API_KEY,
                sessionToken = sessionToken,
            ).body()!!.suggestions.associateWith { suggestion ->
                locationClient.getCoordinatesForPlace(
                    placeId = suggestion.idOfPlace,
                    accessToken = BuildConfig.MAP_BOX_API_KEY,
                    sessionToken = sessionToken
                ).body()!!.coordinates
            }.toList()
            val autofillSuggestionList =
                suggestionCoordinatePairs.map { (suggestion, coordinates) ->
                    val autoFillSuggestionCoordinate = LocationAutofillSuggestion.Coordinates(
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude
                    )
                    suggestion.toLocationAutofillSuggestionList(autoFillSuggestionCoordinate)
                }
            Result.success(autofillSuggestionList)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }
}