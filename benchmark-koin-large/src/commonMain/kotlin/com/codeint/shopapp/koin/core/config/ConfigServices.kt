package com.codeint.shopapp.koin.core.config

import com.codeint.shopapp.koin.core.analytics.RemoteConfigManager
import com.codeint.shopapp.koin.core.storage.PreferencesManager

class FeatureFlagManager(private val rc: RemoteConfigManager) { fun isEnabled(flag: String) = rc.getBoolean("ff_$flag", false) }
class AppConfigProvider(private val rc: RemoteConfigManager) { fun getApiVersion() = rc.getString("api_version", "v3") }
class ThemeManager(private val prefs: PreferencesManager) { fun isDarkMode() = prefs.getBoolean("dark_mode", false); fun setDarkMode(e: Boolean) { prefs.putBoolean("dark_mode", e) } }
class LocaleManager(private val prefs: PreferencesManager) { fun getCurrentLocale() = prefs.getString("locale") ?: "en"; fun setLocale(l: String) { prefs.putString("locale", l) } }
class EnvironmentManager { fun getEnvironment() = "production"; fun getBaseUrl() = "https://api.shopapp.com" }
