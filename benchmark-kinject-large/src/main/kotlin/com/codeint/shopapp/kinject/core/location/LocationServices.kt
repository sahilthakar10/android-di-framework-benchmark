package com.codeint.shopapp.kinject.core.location

import com.codeint.shopapp.kinject.core.storage.PreferencesManager
import me.tatarka.inject.annotations.Inject

@Inject class LocationManager(private val prefs: PreferencesManager) { fun getLastKnownLocation(): Location? = null }
@Inject class GeocodingService { fun getAddress(lat: Double, lng: Double) = "123 Main St" }
@Inject class StoreLocator(private val lm: LocationManager, private val gc: GeocodingService) { fun findNearbyStores() = listOf(Store("Store 1", 37.78, -122.41, 1.2)) }
data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
