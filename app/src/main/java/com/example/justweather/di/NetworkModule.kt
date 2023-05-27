package com.example.justweather.di

import com.example.justweather.data.remote.location.LocationClient
import com.example.justweather.data.remote.location.LocationClientConstants
import com.example.justweather.data.remote.weather.WeatherClient
import com.example.justweather.data.remote.weather.WeatherClientConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideWeatherClient(): WeatherClient = Retrofit.Builder()
        .baseUrl(WeatherClientConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(WeatherClient::class.java)

    @Provides
    fun provideLocationClient(): LocationClient = Retrofit.Builder()
        .baseUrl(LocationClientConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(LocationClient::class.java)

}