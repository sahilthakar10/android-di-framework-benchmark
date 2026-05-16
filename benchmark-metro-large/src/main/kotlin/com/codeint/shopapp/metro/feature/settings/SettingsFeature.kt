package com.codeint.shopapp.metro.feature.settings

import com.codeint.shopapp.metro.core.config.*
import com.codeint.shopapp.metro.core.notification.NotificationManager
import com.codeint.shopapp.metro.core.analytics.ConsentManager
import com.codeint.shopapp.metro.core.storage.CacheManager
import dev.zacsweers.metro.Inject

class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val localeManager: LocaleManager,
    private val notificationManager: NotificationManager,
    private val consentManager: ConsentManager,
    private val cacheManager: CacheManager
) {
    fun loadSettings(): SettingsState = SettingsState(
        isDarkMode = themeManager.isDarkMode(),
        locale = localeManager.getCurrentLocale(),
        notificationsEnabled = notificationManager.isEnabled(),
        analyticsEnabled = consentManager.hasAnalyticsConsent()
    )
    fun toggleDarkMode(enabled: Boolean) { themeManager.setDarkMode(enabled) }
    fun setLocale(locale: String) { localeManager.setLocale(locale) }
    fun toggleNotifications(enabled: Boolean) { notificationManager.setEnabled(enabled) }
    fun clearCache() { cacheManager.clear() }
}

class PrivacySettingsPresenter @Inject constructor(private val consentManager: ConsentManager) {
    fun getConsentStatus(): Map<String, Boolean> = mapOf("analytics" to consentManager.hasAnalyticsConsent(), "ads" to consentManager.hasAdsConsent())
    fun updateConsent(analytics: Boolean, ads: Boolean) { consentManager.updateConsent(analytics, ads) }
}

data class SettingsState(val isDarkMode: Boolean, val locale: String, val notificationsEnabled: Boolean, val analyticsEnabled: Boolean)
