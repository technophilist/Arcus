package com.example.arcus.data.local.textgeneration

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GeneratedTextCacheDatabaseDao {

    @Upsert
    suspend fun addGeneratedTextForLocation(generatedTextForLocationEntity: GeneratedTextForLocationEntity)

    @Query("SELECT * FROM GeneratedTextForLocationEntities where nameOfLocation = :nameOfLocation")
    suspend fun getGeneratedTextForLocation(nameOfLocation: String): GeneratedTextForLocationEntity?

    @Query("DELETE from GeneratedTextForLocationEntities")
    suspend fun deleteAllSavedText()
}