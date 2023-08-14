package com.example.arcus.data.remote.location.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * This data class represents a response containing a list of place [suggestions] for a specific
 * query.
 */
@JsonClass(generateAdapter = true)
data class SuggestionsResponse(@Json(name = "results") val suggestions: List<Suggestion> = emptyList()) {

    /**
     * This data class represents a single place suggestion for a specific query.
     *
     * @property idOfPlace The ID of the place.
     * @property nameOfPlace The name of the place.
     * @property country The country that the place is situated in.
     */
    @JsonClass(generateAdapter = true)
    data class Suggestion(
        @Json(name = "id") val idOfPlace: String,
        @Json(name = "name") val nameOfPlace: String,
        @Json(name = "country") val country: String?,
        @Json(name = "admin1") val state: String?,
        @Json(name = "country_code") val countryCode: String?,
        val latitude: String,
        val longitude: String
    )
}

/**
 * An extension property that contains a url pointing to an svg file containing a circular image
 * of a country's flag.
 */
val SuggestionsResponse.Suggestion.circularCountryFlagUrl: String?
    get() = countryCode?.let {
        "https://open-meteo.com/images/country-flags/$countryCode.svg"
    }

