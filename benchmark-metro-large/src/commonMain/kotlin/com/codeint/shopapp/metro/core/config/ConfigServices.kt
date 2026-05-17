package com.codeint.shopapp.metro.core.config

import com.codeint.shopapp.metro.core.analytics.RemoteConfigManager
import com.codeint.shopapp.metro.core.storage.PreferencesManager
import dev.zacsweers.metro.Inject

class FeatureFlagManager @Inject constructor(private val rc: RemoteConfigManager) { fun isEnabled(flag: String) = rc.getBoolean("ff_$flag", false) }
class AppConfigProvider @Inject constructor(private val rc: RemoteConfigManager) { fun getApiVersion() = rc.getString("api_version", "v3") }
class ThemeManager @Inject constructor(private val prefs: PreferencesManager) { fun isDarkMode() = prefs.getBoolean("dark_mode", false) }
class LocaleManager @Inject constructor(private val prefs: PreferencesManager) { fun getCurrentLocale() = prefs.getString("locale") ?: "en" }
class EnvironmentManager @Inject constructor() { fun getEnvironment() = "production" }
