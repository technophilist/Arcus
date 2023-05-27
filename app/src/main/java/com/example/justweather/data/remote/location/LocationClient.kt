package com.example.justweather.data.remote.location

import retrofit2.http.GET
import retrofit2.http.Query
import androidx.annotation.IntRange
import com.example.justweather.data.remote.location.models.SuggestionsResponse
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
     * @param accessToken The access token for the API.
     * @param sessionToken The session token.
     * @param limit The maximum number of suggestions to return. Default is 10.
     */
    @GET(LocationClientConstants.EndPoints.GET_PLACES_SUGGESTIONS_FOR_QUERY)
    suspend fun getPlacesSuggestionsForQuery(
        @Query("q") query: String,
        @Query("access_token") accessToken: String,
        @Query("session_token") sessionToken: String,
        @Query("limit") @IntRange(1, 10) limit: Int = 10
    ): Response<SuggestionsResponse>

}
