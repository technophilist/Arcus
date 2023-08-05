package com.example.arcus.di

import com.example.arcus.data.repositories.location.JustWeatherLocationServicesRepository
import com.example.arcus.data.repositories.location.LocationServicesRepository
import com.example.arcus.data.repositories.weather.JustWeatherWeatherRepository
import com.example.arcus.data.repositories.weather.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindLocationServicesRepository(
        impl: JustWeatherLocationServicesRepository
    ): LocationServicesRepository

    @Binds
    abstract fun bindWeatherRepository(
        impl: JustWeatherWeatherRepository
    ): WeatherRepository
}