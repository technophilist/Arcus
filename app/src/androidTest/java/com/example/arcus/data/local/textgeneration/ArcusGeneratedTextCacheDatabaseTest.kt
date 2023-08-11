package com.example.arcus.data.local.textgeneration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArcusGeneratedTextCacheDatabaseTest {
    private lateinit var database: ArcusGeneratedTextCacheDatabase
    private lateinit var dao: GeneratedTextCacheDatabaseDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ArcusGeneratedTextCacheDatabase::class.java
        ).build()
        dao = database.getDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addGeneratedTextForLocationTest_validInstance_successfullyInserted() = runTest {
        val entityToBeInserted = GeneratedTextForLocationEntity(
            nameOfLocation = "Seattle",
            temperature = 72,
            conciseWeatherDescription = "Sunny",
            generatedDescription = "Clear Day"
        )
        dao.addGeneratedTextForLocation(entityToBeInserted)
        assert(dao.getGeneratedTextForLocation("Seattle") == entityToBeInserted)
    }

    @Test
    fun deleteAllSavedTextTest_databaseWith2Entities_allEntitiesGetRemoved() = runTest {
        val generatedTextForLocationEntity1 = GeneratedTextForLocationEntity(
            nameOfLocation = "Seattle",
            temperature = 72,
            conciseWeatherDescription = "Sunny",
            generatedDescription = "Clear Day"
        )
        val generatedTextForLocationEntity2 = GeneratedTextForLocationEntity(
            nameOfLocation = "New York",
            temperature = 72,
            conciseWeatherDescription = "Sunny",
            generatedDescription = "Clear Night"
        )
        dao.addGeneratedTextForLocation(generatedTextForLocationEntity1)
        dao.addGeneratedTextForLocation(generatedTextForLocationEntity2)

        dao.deleteAllSavedText()

        assert(dao.getGeneratedTextForLocation("Seattle") == null)
        assert(dao.getGeneratedTextForLocation("New York") == null)
    }


}