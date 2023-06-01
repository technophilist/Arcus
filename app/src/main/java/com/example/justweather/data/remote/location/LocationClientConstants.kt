package com.example.justweather.data.remote.location

import com.example.justweather.BuildConfig
import okhttp3.OkHttpClient


/**
 * This object contains constants used by the [LocationClient].
 */
object LocationClientConstants {

    /**
     * The base URL of the location client's API.
     */
    const val BASE_URL = " https://api.mapbox.com/search/searchbox/v1/"

    /**
     * The endpoints used by the locations client's API.
     */
    object EndPoints {
        /**
         * The endpoint used to get place suggestions for a query.
         */
        const val GET_PLACES_SUGGESTIONS_FOR_QUERY = "suggest"

        /**
         * The endpoint used to get the coordinates of a place with a specific id.
         */
        const val GET_COORDINATES_FOR_PLACE_ID = "retrieve/{id}"
    }

    /**
     * An OkHttpClient instance that automatically adds the location client's API key required for
     * all requests.
     */
    val AutoAddApiKeyClient: OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor { chain ->
            val url = chain.request().url().newBuilder()
                .addQueryParameter("access_token", BuildConfig.MAP_BOX_API_KEY)
                .build()
            val request = chain.request().newBuilder().url(url).build()
            chain.proceed(request)
        }.build()
}