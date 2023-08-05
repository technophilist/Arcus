package com.example.arcus.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.arcus.data.local.weather.ArcusDatabaseDao
import com.example.arcus.di.IODispatcher
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
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val dao: ArcusDatabaseDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParameters) {

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