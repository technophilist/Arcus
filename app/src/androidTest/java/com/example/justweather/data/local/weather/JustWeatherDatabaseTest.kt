package com.example.justweather.data.local.weather

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
internal class JustWeatherDatabaseTest {
    private lateinit var database: JustWeatherDatabase
    private lateinit var dao: JustWeatherDatabaseDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            JustWeatherDatabase::class.java
        ).build()
        dao = database.getDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addSavedWeatherEntityTest_ValidEntity_isSuccessfullySaved() = runTest {
        val weatherLocationEntity = SavedWeatherLocationEntity(
            id = "1",
            nameOfLocation = "New York",
            latitude = "40.7128",
            longitude = "74.0060"
        )
        dao.addSavedWeatherEntity(weatherLocationEntity)
        with(dao.getAllSavedWeatherEntities().first()) {
            assert(size == 1)
            assert(first() == weatherLocationEntity)
        }
    }

    @Test
    fun deleteSavedWeatherEntityTest_ValidExistingEntity_isSuccessfullyDeleted() = runTest {
        val weatherLocationEntity = SavedWeatherLocationEntity(
            id = "1",
            nameOfLocation = "Seattle",
            latitude = "47.6062",
            longitude = "-122.3321"
        )
        with(dao){
            // add item to database
            addSavedWeatherEntity(weatherLocationEntity)
            // the item must be inserted
            assert(getAllSavedWeatherEntities().first().contains(weatherLocationEntity))
            // delete item from database
            deleteSavedWeatherEntity(weatherLocationEntity)
            // the item must not exist in the database
            assert(!getAllSavedWeatherEntities().first().contains(weatherLocationEntity))
        }
    }

}