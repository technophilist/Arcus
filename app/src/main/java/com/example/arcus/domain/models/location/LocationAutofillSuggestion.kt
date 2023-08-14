package com.example.arcus.domain.models.location

import com.example.arcus.data.remote.location.models.SuggestionsResponse
import com.example.arcus.data.remote.location.models.circularCountryFlagUrl

/**
 * This is a data class that models an auto-fill suggestion for a location query.
 * @param idOfLocation The id of the location.
 * @param nameOfLocation The name of the location.
 * @param addressOfLocation The address of the location.
 * @param coordinatesOfLocation The [Coordinates] of the location.
 */
data class LocationAutofillSuggestion(
    val idOfLocation: String,
    val nameOfLocation: String,
    val addressOfLocation: String,
    val coordinatesOfLocation: Coordinates,
    val countryFlagUrl: String,
)

/**
 * A mapper function used to map a list of type [SuggestionsResponse.Suggestion] to a list of
 * of type [LocationAutofillSuggestion].
 *
 * Note: This method **filters out all instances of [SuggestionsResponse.Suggestion] that have
 * the [SuggestionsResponse.Suggestion.state] set to null.**
 */
fun List<SuggestionsResponse.Suggestion>.toLocationAutofillSuggestionList(): List<LocationAutofillSuggestion> =
    this.filter { it.state != null && it.country != null }
        .map {
            LocationAutofillSuggestion(
                idOfLocation = it.idOfPlace,
                nameOfLocation = it.nameOfPlace,
                addressOfLocation = "${it.state}, ${it.country}",
                coordinatesOfLocation = Coordinates(
                    latitude = it.latitude,
                    longitude = it.longitude
                ),
                countryFlagUrl = it.circularCountryFlagUrl
            )
        }
