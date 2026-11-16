package org.umn.where2go.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat

object DeviceLocationProvider {
    fun requestCurrentLocation(context: Context, onResult: (Location?) -> Unit) {
        val appContext = context.applicationContext
        val hasFineLocation = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            onResult(null)
            return
        }

        val locationManager =
            appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val preferredProviders = if (hasFineLocation) {
            listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        } else {
            listOf(LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
        }
        val provider = preferredProviders.firstOrNull { candidate ->
            runCatching { locationManager.isProviderEnabled(candidate) }.getOrDefault(false)
        }

        if (provider == null) {
            onResult(lastKnownLocation(locationManager, preferredProviders))
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                locationManager.getCurrentLocation(
                    provider,
                    null,
                    ContextCompat.getMainExecutor(appContext)
                ) { location ->
                    onResult(location ?: lastKnownLocation(locationManager, preferredProviders))
                }
            } catch (_: SecurityException) {
                onResult(lastKnownLocation(locationManager, preferredProviders))
            }
        } else {
            onResult(lastKnownLocation(locationManager, preferredProviders))
        }
    }

    private fun lastKnownLocation(
        locationManager: LocationManager,
        providers: List<String>
    ): Location? =
        providers.mapNotNull { provider ->
            runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
        }.maxByOrNull { it.time }
}
