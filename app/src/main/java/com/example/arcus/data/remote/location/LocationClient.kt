package com.example.arcus.data.remote.location

import retrofit2.http.GET
import retrofit2.http.Query
import androidx.annotation.IntRange
import com.example.arcus.data.remote.location.models.SuggestionsResponse
import retrofit2.Response

/**
 * An interface representing a network based client that provides location services such as
 * providing place suggestions for a specific query.
 */
interface LocationClient {

    /**
     * This method returns a list of place suggestions for a given [query] and returns a
     * [Response] object containing a [SuggestionsResponse] object, representing the suggestions.
     *
     * @param count The maximum number of suggestions to return. Default is 20.
     */
    @GET(LocationClientConstants.EndPoints.GET_PLACES_SUGGESTIONS_FOR_QUERY)
    suspend fun getPlacesSuggestionsForQuery(
        @Query("name") query: String,
        @Query("count") @IntRange(1, 100) count: Int = 20
    ): Response<SuggestionsResponse>

}
