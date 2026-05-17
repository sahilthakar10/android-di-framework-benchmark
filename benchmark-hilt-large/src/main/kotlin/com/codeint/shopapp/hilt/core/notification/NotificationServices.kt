package com.codeint.shopapp.hilt.core.notification

import com.codeint.shopapp.hilt.core.auth.SessionManager
import com.codeint.shopapp.hilt.core.storage.PreferencesManager
import com.codeint.shopapp.hilt.core.config.FeatureFlagManager
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import javax.inject.Inject

class NotificationManager @Inject constructor(private val prefs: PreferencesManager) {
    fun isEnabled() = prefs.getBoolean("notifications_enabled", true)
    fun setEnabled(enabled: Boolean) { prefs.putBoolean("notifications_enabled", enabled) }
    fun scheduleLocal(title: String, body: String, delayMs: Long) {}
    fun cancelAll() {}
}

class PushTokenManager @Inject constructor(private val sessionManager: SessionManager) {
    fun registerToken(token: String) {}
    fun unregisterToken() {}
    fun getToken(): String? = null
}

class DeepLinkHandler @Inject constructor() {
    fun handle(uri: String) = DeepLinkResult(
        when {
            uri.contains("/product/") -> "product_detail"
            uri.contains("/order/") -> "order_detail"
            else -> "home"
        },
        emptyMap()
    )
}

class InAppMessageManager @Inject constructor(
    private val featureFlagManager: FeatureFlagManager,
    private val analyticsTracker: AnalyticsTracker
) {
    fun showBanner(message: String, type: String) { analyticsTracker.track("banner_shown", mapOf("type" to type)) }
}

data class DeepLinkResult(val destination: String, val params: Map<String, String>)
