package com.example.arcus.data.remote.location

import android.content.Context
import android.location.Geocoder
import com.example.arcus.di.IODispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class ArcusReverseGeocoder @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ReverseGeocoder {
    override suspend fun getLocationNameForCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<String> = withContext(ioDispatcher) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            // geocoder#getFromLocation is a blocking method. Since it is called in the context
            // of the IO-Dispatcher, it is safe to call it.
            val address = geocoder.getFromLocation(latitude, longitude, 1)?.first()!!
            Result.success("${address.locality}, ${address.adminArea}")
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }
}