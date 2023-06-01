package com.example.justweather.data.remote.weather

import com.example.justweather.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

/**
 * This object contains constants used by the [WeatherClient].
 */
object WeatherClientConstants {
    /**
     * The base URL of the weather API.
     */
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    /**
     * The endpoints of the weather API.
     */
    object EndPoints {
        const val GET_WEATHER_ENDPOINT = "weather"
    }

    /**
     * Used to configure the units returned by the weather API.
     */
    object Units {
        const val CELSIUS = "metric"
        const val FAHRENHEIT = "imperial"
    }

    /**
     * An OkHttpClient instance that automatically adds the weather client's API key required for
     * all requests.
     */
    val AutoAddApiKeyClient: OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor { chain ->
            val url = chain.request().url().newBuilder()
                .addQueryParameter("appid", BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                .build()
            val request = chain.request().newBuilder().url(url).build()
            chain.proceed(request)
        }.build()


}