package com.example.arcus.data.remote.location.models

import com.squareup.moshi.JsonClass

/**
 * This class models a response that contains the coordinates of a particular location.
 */
@JsonClass(generateAdapter = true)
data class CoordinatesResponse(val features: List<Feature>) {
    /**
     * This class represents a single feature of the [CoordinatesResponse].
     * @param geometry Geometry object.
     */
    @JsonClass(generateAdapter = true)
    data class Feature(val geometry: Geometry) {
        /**
         * This class is used to hold the geometry of the [Feature] data class.
         * @param coordinates List of coordinates of a location.
         */
        @JsonClass(generateAdapter = true)
        data class Geometry(val coordinates: List<String>)
    }

    /**
     * This class is used to hold the coordinates of a location.
     *
     * @param longitude Longitude of the location.
     * @param latitude Latitude of the location.
     */
    data class Coordinates(val longitude: String, val latitude: String)
}

/**
 * An extension property that is used to get the [CoordinatesResponse.Coordinates] of an instance
 * of [CoordinatesResponse].
 */
val CoordinatesResponse.coordinates: CoordinatesResponse.Coordinates
    get() {
        val (longitude, latitude) = features.first().geometry.coordinates
        return CoordinatesResponse.Coordinates(longitude, latitude)
    }
