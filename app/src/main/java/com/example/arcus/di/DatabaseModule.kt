package com.example.arcus.di

import android.content.Context
import androidx.room.Room
import com.example.arcus.data.local.weather.ArcusDatabase
import com.example.arcus.data.local.weather.ArcusDatabaseDao
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
    fun provideArcusDatabaseDao(
        @ApplicationContext context: Context
    ): ArcusDatabaseDao = Room.databaseBuilder(
        context = context,
        klass = ArcusDatabase::class.java,
        name = ArcusDatabase.DATABASE_NAME
    ).build().getDao()
}