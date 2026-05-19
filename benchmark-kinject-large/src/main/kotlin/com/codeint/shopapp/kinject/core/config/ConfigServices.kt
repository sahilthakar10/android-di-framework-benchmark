package com.codeint.shopapp.kinject.core.config

import com.codeint.shopapp.kinject.core.analytics.RemoteConfigManager
import com.codeint.shopapp.kinject.core.storage.PreferencesManager
import me.tatarka.inject.annotations.Inject

@Inject class FeatureFlagManager(private val rc: RemoteConfigManager) { fun isEnabled(flag: String) = rc.getBoolean("ff_$flag", false) }
@Inject class AppConfigProvider(private val rc: RemoteConfigManager) { fun getApiVersion() = rc.getString("api_version", "v3") }
@Inject class ThemeManager(private val prefs: PreferencesManager) { fun isDarkMode() = prefs.getBoolean("dark_mode", false) }
@Inject class LocaleManager(private val prefs: PreferencesManager) { fun getCurrentLocale() = prefs.getString("locale") ?: "en" }
@Inject class EnvironmentManager { fun getEnvironment() = "production" }
