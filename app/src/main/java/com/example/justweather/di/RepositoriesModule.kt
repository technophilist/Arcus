package com.example.justweather.di

import com.example.justweather.data.repositories.location.JustWeatherLocationServicesRepository
import com.example.justweather.data.repositories.location.LocationServicesRepository
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
}