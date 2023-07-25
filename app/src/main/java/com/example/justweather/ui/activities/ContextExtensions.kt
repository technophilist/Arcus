package com.example.justweather.ui.activities

import android.Manifest
import android.content.Context
import androidx.core.content.ContextCompat

/**
 * Returns true if either [Manifest.permission.ACCESS_COARSE_LOCATION] or [Manifest.permission.ACCESS_FINE_LOCATION]
 * permission is granted. Otherwise, returns false.
 */
fun Context.hasLocationPermission(): Boolean {
    val isCoarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == 1
    val isFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == 1
    return isCoarseLocationPermissionGranted || isFineLocationPermissionGranted
}
