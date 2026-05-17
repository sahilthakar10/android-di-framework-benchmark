package com.codeint.shopapp.metro.core.location

import com.codeint.shopapp.metro.core.storage.PreferencesManager
import dev.zacsweers.metro.Inject

class LocationManager @Inject constructor(private val prefs: PreferencesManager) { fun getLastKnownLocation(): Location? = null }
class GeocodingService @Inject constructor() { fun getAddress(lat: Double, lng: Double) = "123 Main St" }
class StoreLocator @Inject constructor(private val lm: LocationManager, private val gc: GeocodingService) { fun findNearbyStores() = listOf(Store("Store 1", 37.78, -122.41, 1.2)) }
data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
