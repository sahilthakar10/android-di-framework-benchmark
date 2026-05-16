package com.codeint.shopapp.koin.core.notification

import com.codeint.shopapp.koin.core.auth.SessionManager
import com.codeint.shopapp.koin.core.storage.PreferencesManager

class NotificationManager constructor(private val prefs: PreferencesManager) {
    fun isEnabled(): Boolean = prefs.getBoolean("notifications_enabled", true)
    fun setEnabled(enabled: Boolean) { prefs.putBoolean("notifications_enabled", enabled) }
    fun scheduleLocal(title: String, body: String, delayMs: Long) {}
    fun cancelAll() {}
}

class PushTokenManager constructor(private val sessionManager: SessionManager) {
    fun registerToken(token: String) {}
    fun unregisterToken() {}
    fun getToken(): String? = null
}

class DeepLinkHandler constructor() {
    fun handle(uri: String): DeepLinkResult = when {
        uri.contains("/product/") -> DeepLinkResult("product_detail", mapOf("id" to uri.substringAfterLast("/")))
        uri.contains("/order/") -> DeepLinkResult("order_detail", mapOf("id" to uri.substringAfterLast("/")))
        uri.contains("/chat") -> DeepLinkResult("chat", emptyMap())
        else -> DeepLinkResult("home", emptyMap())
    }
}

class InAppMessageManager constructor(
    private val featureFlagManager: com.codeint.shopapp.koin.core.config.FeatureFlagManager,
    private val analyticsTracker: com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
) {
    fun showBanner(message: String, type: String) { analyticsTracker.track("banner_shown", mapOf("type" to type)) }
    fun showBottomSheet(title: String, body: String) {}
}

data class DeepLinkResult(val destination: String, val params: Map<String, String>)
