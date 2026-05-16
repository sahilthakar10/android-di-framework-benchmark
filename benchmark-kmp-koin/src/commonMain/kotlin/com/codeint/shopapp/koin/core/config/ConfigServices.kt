package com.codeint.shopapp.koin.core.config

import com.codeint.shopapp.koin.core.analytics.RemoteConfigManager
import com.codeint.shopapp.koin.core.storage.PreferencesManager

class FeatureFlagManager constructor(private val remoteConfig: RemoteConfigManager) {
    fun isEnabled(flag: String): Boolean = remoteConfig.getBoolean("ff_$flag", false)
    fun getValue(flag: String): String = remoteConfig.getString("ff_$flag", "")
}

class AppConfigProvider constructor(private val remoteConfig: RemoteConfigManager) {
    fun getApiVersion(): String = remoteConfig.getString("api_version", "v3")
    fun getMinAppVersion(): String = remoteConfig.getString("min_app_version", "1.0.0")
    fun getMaxCartItems(): Int = remoteConfig.getLong("max_cart_items", 50).toInt()
    fun getSupportEmail(): String = remoteConfig.getString("support_email", "support@shopapp.com")
}

class ThemeManager constructor(private val prefs: PreferencesManager) {
    fun isDarkMode(): Boolean = prefs.getBoolean("dark_mode", false)
    fun setDarkMode(enabled: Boolean) { prefs.putBoolean("dark_mode", enabled) }
    fun getAccentColor(): String = prefs.getString("accent_color") ?: "#FF6200EE"
}

class LocaleManager constructor(private val prefs: PreferencesManager) {
    fun getCurrentLocale(): String = prefs.getString("locale") ?: "en"
    fun setLocale(locale: String) { prefs.putString("locale", locale) }
    fun getSupportedLocales(): List<String> = listOf("en", "es", "fr", "de", "ja", "zh")
}

class EnvironmentManager constructor() {
    fun getEnvironment(): String = "production"
    fun getBaseUrl(): String = "https://api.shopapp.com"
    fun isDebug(): Boolean = false
}
