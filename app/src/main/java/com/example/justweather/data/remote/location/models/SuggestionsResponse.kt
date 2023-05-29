package com.example.justweather.data.remote.location.models

import com.example.justweather.domain.models.LocationAutofillSuggestion
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * This data class represents a response containing a list of place [suggestions] for a specific
 * query.
 */
@JsonClass(generateAdapter = true)
data class SuggestionsResponse(val suggestions: List<Suggestion>) {

    /**
     * This data class represents a single place suggestion for a specific query.
     *
     * @property idOfPlace The ID of the place.
     * @property nameOfPlace The name of the place.
     * @property addressOfPlace The address of the place.
     */
    @JsonClass(generateAdapter = true)
    data class Suggestion(
        @Json(name = "mapbox_id") val idOfPlace: String,
        @Json(name = "name") val nameOfPlace: String,
        @Json(name = "place_formatted") val addressOfPlace: String
    )
}


/**
 * A mapper function used to map an instance of [SuggestionsResponse] to an instance of
 * [LocationAutofillSuggestion].
 */
fun SuggestionsResponse.Suggestion.toLocationAutofillSuggestionList(
    coordinates: LocationAutofillSuggestion.Coordinates
): LocationAutofillSuggestion =
    LocationAutofillSuggestion(
        idOfLocation = idOfPlace,
        nameOfLocation = nameOfPlace,
        addressOfLocation = addressOfPlace,
        coordinatesOfLocation = coordinates
    )


