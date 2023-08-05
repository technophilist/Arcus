package com.example.arcus.di

import com.example.arcus.data.repositories.location.ArcusLocationServicesRepository
import com.example.arcus.data.repositories.location.LocationServicesRepository
import com.example.arcus.data.repositories.weather.ArcusWeatherRepository
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
        impl: ArcusLocationServicesRepository
    ): LocationServicesRepository

    @Binds
    abstract fun bindWeatherRepository(
        impl: ArcusWeatherRepository
    ): WeatherRepository
}