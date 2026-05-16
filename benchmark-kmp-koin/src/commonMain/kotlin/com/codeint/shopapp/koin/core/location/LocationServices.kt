package com.codeint.shopapp.koin.core.location

import com.codeint.shopapp.koin.core.storage.PreferencesManager

class LocationManager constructor(private val prefs: PreferencesManager) {
    fun getLastKnownLocation(): Location? = prefs.getString("last_lat")?.let { lat -> prefs.getString("last_lng")?.let { lng -> Location(lat.toDouble(), lng.toDouble()) } }
    fun requestLocationUpdate() {}
}

class GeocodingService constructor() {
    fun getAddress(lat: Double, lng: Double): String = "123 Main St, San Francisco, CA"
    fun getCoordinates(address: String): Location = Location(37.7749, -122.4194)
}

class StoreLocator constructor(private val locationManager: LocationManager, private val geocoding: GeocodingService) {
    fun findNearbyStores(radiusKm: Double = 10.0): List<Store> = listOf(Store("Store 1", 37.78, -122.41, 1.2))
}

data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
