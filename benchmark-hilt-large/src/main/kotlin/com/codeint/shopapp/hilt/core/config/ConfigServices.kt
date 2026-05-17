package com.codeint.shopapp.hilt.core.config

import com.codeint.shopapp.hilt.core.analytics.RemoteConfigManager
import com.codeint.shopapp.hilt.core.storage.PreferencesManager
import javax.inject.Inject

class FeatureFlagManager @Inject constructor(private val remoteConfig: RemoteConfigManager) {
    fun isEnabled(flag: String) = remoteConfig.getBoolean("ff_$flag", false)
    fun getValue(flag: String) = remoteConfig.getString("ff_$flag", "")
}

class AppConfigProvider @Inject constructor(private val remoteConfig: RemoteConfigManager) {
    fun getApiVersion() = remoteConfig.getString("api_version", "v3")
    fun getMinAppVersion() = remoteConfig.getString("min_app_version", "1.0.0")
    fun getMaxCartItems() = remoteConfig.getLong("max_cart_items", 50).toInt()
}

class ThemeManager @Inject constructor(private val prefs: PreferencesManager) {
    fun isDarkMode() = prefs.getBoolean("dark_mode", false)
    fun setDarkMode(enabled: Boolean) { prefs.putBoolean("dark_mode", enabled) }
}

class LocaleManager @Inject constructor(private val prefs: PreferencesManager) {
    fun getCurrentLocale() = prefs.getString("locale") ?: "en"
    fun setLocale(locale: String) { prefs.putString("locale", locale) }
    fun getSupportedLocales() = listOf("en", "es", "fr", "de", "ja", "zh")
}

class EnvironmentManager @Inject constructor() {
    fun getEnvironment() = "production"
    fun getBaseUrl() = "https://api.shopapp.com"
    fun isDebug() = false
}
