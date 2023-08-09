package com.example.arcus.di

import com.example.arcus.BuildConfig
import com.example.arcus.data.remote.languagemodel.TextGeneratorClient
import com.example.arcus.data.remote.languagemodel.TextGeneratorClientConstants
import com.example.arcus.data.remote.location.LocationClient
import com.example.arcus.data.remote.location.LocationClientConstants
import com.example.arcus.data.remote.weather.WeatherClient
import com.example.arcus.data.remote.weather.WeatherClientConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideWeatherClient(): WeatherClient = Retrofit.Builder()
        .baseUrl(WeatherClientConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(WeatherClient::class.java)

    @Provides
    @Singleton
    fun provideLocationClient(): LocationClient = Retrofit.Builder()
        .baseUrl(LocationClientConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(LocationClient::class.java)

    @Provides
    @Singleton
    fun provideTextGeneratorClient(): TextGeneratorClient = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${BuildConfig.OPEN_AI_API_TOKEN}")
                        .build()
                    chain.proceed(newRequest)
                }
                .build()
        )
        .baseUrl(TextGeneratorClientConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(TextGeneratorClient::class.java)

}