package com.example.arcus.data.remote.location


/**
 * This object contains constants used by the [LocationClient].
 */
object LocationClientConstants {

    /**
     * The base URL of the location client's API.
     */
    const val BASE_URL = "https://geocoding-api.open-meteo.com/v1/"

    /**
     * The endpoints used by the locations client's API.
     */
    object EndPoints {
        /**
         * The endpoint used to get place suggestions for a query.
         */
        const val GET_PLACES_SUGGESTIONS_FOR_QUERY = "search"
    }
}