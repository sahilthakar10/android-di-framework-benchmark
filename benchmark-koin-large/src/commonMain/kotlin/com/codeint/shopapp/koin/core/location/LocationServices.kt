package com.codeint.shopapp.koin.core.location

import com.codeint.shopapp.koin.core.storage.PreferencesManager

class LocationManager(private val prefs: PreferencesManager) { fun getLastKnownLocation(): Location? = null }
class GeocodingService { fun getAddress(lat: Double, lng: Double) = "123 Main St"; fun getCoordinates(addr: String) = Location(37.77, -122.41) }
class StoreLocator(private val lm: LocationManager, private val gc: GeocodingService) { fun findNearbyStores() = listOf(Store("Store 1", 37.78, -122.41, 1.2)) }
data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
