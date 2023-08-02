package com.example.justweather.di

import androidx.lifecycle.ViewModel
import com.example.justweather.data.remote.location.JustWeatherReverseGeocoder
import com.example.justweather.data.remote.location.ReverseGeocoder
import com.example.justweather.domain.location.CurrentLocationProvider
import com.example.justweather.domain.location.JustWeatherCurrentLocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class LocationServicesModule {

    @Binds
    abstract fun bindCurrentLocationProvider(impl: JustWeatherCurrentLocationProvider): CurrentLocationProvider

    @Binds
    abstract fun bindReverseGeocoder(impl: JustWeatherReverseGeocoder): ReverseGeocoder
}