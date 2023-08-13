package com.example.arcus.data.repositories.textgenerator

import com.example.arcus.domain.models.weather.CurrentWeatherDetails

/**
 * A repository that contains methods that can be used to generate text based on the provided
 * parameters.
 */
interface GenerativeTextRepository {

    /**
     * Generates text based on the provided [weatherDetails]. Once text has been generated for
     * a particular instance of [CurrentWeatherDetails] with it's associated data, subsequent calls to
     * this method,with instances of [CurrentWeatherDetails], with the same values in it, would result
     * in the same text being returned.
     */
    suspend fun generateTextForWeatherDetails(weatherDetails: CurrentWeatherDetails): Result<String>
}
