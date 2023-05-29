package com.example.justweather.data.remote.location

import com.example.justweather.BuildConfig
import com.example.justweather.di.NetworkModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class LocationClientTest {

    private val locationClient = NetworkModule.provideLocationClient()

    @Test
    fun `Get Suggested places for a valid query`() = runTest {
        val response = locationClient.getPlacesSuggestionsForQuery(
            "Apple Park",
            BuildConfig.MAP_BOX_API_KEY,
            "session_token"
        )
        advanceUntilIdle()
        assert(response.isSuccessful)
        assert(response.body()!!.suggestions.isNotEmpty())
    }

    @Test
    fun `Get empty list of suggested places for an invalid query`() = runTest {
        val response = locationClient.getPlacesSuggestionsForQuery(
            "123jnfdjijnfijnijndsf",
            BuildConfig.MAP_BOX_API_KEY,
            "session_token"
        )
        advanceUntilIdle()
        assert(response.isSuccessful)
        assert(response.body()!!.suggestions.isEmpty())
    }

    @Test
    fun `Get coordinates of a place with a valid place id`() = runTest {
        val commonSessionToken = "sessionToken"
        // given a valid placeId of a valid location
        val placeId = locationClient.getPlacesSuggestionsForQuery(
            query = "GooglePlex",
            accessToken = BuildConfig.MAP_BOX_API_KEY,
            sessionToken = commonSessionToken
        ).body()!!.suggestions.first().idOfPlace
        advanceUntilIdle()
        // the coordinates of the location must be successfully fetched
        val response = locationClient.getCoordinatesForPlace(
            placeId = placeId,
            accessToken = BuildConfig.MAP_BOX_API_KEY,
            sessionToken = commonSessionToken
        )
        advanceUntilIdle()
        assert(response.isSuccessful)
        println(response.body())
    }
}
