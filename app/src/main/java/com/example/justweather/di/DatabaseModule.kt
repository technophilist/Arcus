package com.example.justweather.di

import android.content.Context
import androidx.room.Room
import com.example.justweather.data.local.weather.JustWeatherDatabase
import com.example.justweather.data.local.weather.JustWeatherDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideJustWeatherDatabaseDao(
        @ApplicationContext context: Context
    ): JustWeatherDatabaseDao = Room.databaseBuilder(
        context = context,
        klass = JustWeatherDatabase::class.java,
        name = JustWeatherDatabase.DATABASE_NAME
    ).build().getDao()
}