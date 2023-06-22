package com.example.justweather.ui.navigation

/**
 * A sealed class that contains all the destinations of the app.
 */
sealed class JustWeatherNavigationDestinations(val route: String) {

    object HomeScreen : JustWeatherNavigationDestinations(route = "home_screen")

    object WeatherDetailScreen :
        JustWeatherNavigationDestinations(route = "weather_detail/{nameOfLocation}/{latitude}/{longitude}") {
        const val NAV_ARG_NAME_OF_LOCATION = "nameOfLocation"
        const val NAV_ARG_LATITUDE = "latitude"
        const val NAV_ARG_LONGITUDE = "longitude"

        /**
         * Builds the route string for the weather detail screen destination with the provided
         * [latitude] and [longitude].
         */
        fun buildRoute(
            nameOfLocation: String,
            latitude: String,
            longitude: String
        ) = "weather_detail/$nameOfLocation/$latitude/$longitude"
    }
}