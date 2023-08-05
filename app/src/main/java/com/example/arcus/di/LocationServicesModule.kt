package com.example.arcus.di

import com.example.arcus.data.remote.location.ArcusReverseGeocoder
import com.example.arcus.data.remote.location.ReverseGeocoder
import com.example.arcus.domain.location.CurrentLocationProvider
import com.example.arcus.domain.location.ArcusCurrentLocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class LocationServicesModule {

    @Binds
    abstract fun bindCurrentLocationProvider(impl: ArcusCurrentLocationProvider): CurrentLocationProvider

    @Binds
    abstract fun bindReverseGeocoder(impl: ArcusReverseGeocoder): ReverseGeocoder
}