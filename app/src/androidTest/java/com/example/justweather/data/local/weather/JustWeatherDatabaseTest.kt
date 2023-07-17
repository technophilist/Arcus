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
import kotlin.concurrent.fixedRateTimer


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
            nameOfLocation = "Seattle",
            latitude = "47.6062",
            longitude = "-122.3321"
        )
        with(dao) {
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


    @Test
    fun markAndUnMarkEntityAsDeletedTest_savedEntity_isMarkedAndUnMarkedCorrectly() = runTest {
        // Given an entity saved in the database
        val weatherLocationEntity = SavedWeatherLocationEntity(
            nameOfLocation = "New York",
            latitude = "40.7128",
            longitude = "74.0060"
        ).also { dao.addSavedWeatherEntity(it) }

        // the isDeleted property must be initially set to false
        var savedWeatherEntitiesList: List<SavedWeatherLocationEntity> =
            dao.getAllSavedWeatherEntities().first()
        assert(!savedWeatherEntitiesList.first().isDeleted)

        // when the item is marked as deleted
        dao.markWeatherEntityAsDeleted(weatherLocationEntity.nameOfLocation)
        savedWeatherEntitiesList = dao.getAllSavedWeatherEntities().first()
        // the isDeleted property of the item must be set to true
        assert(savedWeatherEntitiesList.first().isDeleted)

        // when the item is unmarked as deleted
        dao.markWeatherEntityAsUnDeleted(weatherLocationEntity.nameOfLocation)
        savedWeatherEntitiesList = dao.getAllSavedWeatherEntities().first()
        // the isDeleted property of the item must be set back to false
        assert(!savedWeatherEntitiesList.first().isDeleted)
    }

}