package com.example.justweather.data.workers

import android.app.Application
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.justweather.data.local.weather.JustWeatherDatabaseDao
import com.example.justweather.di.IODispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * A worker that deletes all items marked as deleted in the database.
 */
@HiltWorker
class DeleteMarkedItemsWorker @AssistedInject constructor(
    @Assisted application: Application,
    @Assisted workerParameters: WorkerParameters,
    private val dao: JustWeatherDatabaseDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(application, workerParameters) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            dao.deleteAllItemsMarkedAsDeleted()
            Result.success()
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure()
        }
    }
}