package com.codeint.shopapp.metro.core.location

import com.codeint.shopapp.metro.core.storage.PreferencesManager
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class LocationManager @Inject constructor(private val prefs: PreferencesManager) {
    fun getLastKnownLocation(): Location? = prefs.getString("last_lat")?.let { lat -> prefs.getString("last_lng")?.let { lng -> Location(lat.toDouble(), lng.toDouble()) } }
    fun requestLocationUpdate() {}
}

@SingleIn(AppScope::class)
class GeocodingService @Inject constructor() {
    fun getAddress(lat: Double, lng: Double): String = "123 Main St, San Francisco, CA"
    fun getCoordinates(address: String): Location = Location(37.7749, -122.4194)
}

@SingleIn(AppScope::class)
class StoreLocator @Inject constructor(private val locationManager: LocationManager, private val geocoding: GeocodingService) {
    fun findNearbyStores(radiusKm: Double = 10.0): List<Store> = listOf(Store("Store 1", 37.78, -122.41, 1.2))
}

data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
