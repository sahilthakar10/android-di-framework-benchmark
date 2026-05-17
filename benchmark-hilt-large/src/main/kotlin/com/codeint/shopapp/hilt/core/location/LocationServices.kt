package com.codeint.shopapp.hilt.core.location

import com.codeint.shopapp.hilt.core.storage.PreferencesManager
import javax.inject.Inject

class LocationManager @Inject constructor(private val prefs: PreferencesManager) {
    fun getLastKnownLocation(): Location? = null
    fun requestLocationUpdate() {}
}

class GeocodingService @Inject constructor() {
    fun getAddress(lat: Double, lng: Double) = "123 Main St, San Francisco, CA"
    fun getCoordinates(address: String) = Location(37.7749, -122.4194)
}

class StoreLocator @Inject constructor(private val locationManager: LocationManager, private val geocoding: GeocodingService) {
    fun findNearbyStores(radiusKm: Double = 10.0) = listOf(Store("Store 1", 37.78, -122.41, 1.2))
}

data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
