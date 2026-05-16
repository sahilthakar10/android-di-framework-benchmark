package com.codeint.shopapp.metro.core.notification

import com.codeint.shopapp.metro.core.auth.SessionManager
import com.codeint.shopapp.metro.core.storage.PreferencesManager
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class NotificationManager @Inject constructor(private val prefs: PreferencesManager) {
    fun isEnabled(): Boolean = prefs.getBoolean("notifications_enabled", true)
    fun setEnabled(enabled: Boolean) { prefs.putBoolean("notifications_enabled", enabled) }
    fun scheduleLocal(title: String, body: String, delayMs: Long) {}
    fun cancelAll() {}
}

@SingleIn(AppScope::class)
class PushTokenManager @Inject constructor(private val sessionManager: SessionManager) {
    fun registerToken(token: String) {}
    fun unregisterToken() {}
    fun getToken(): String? = null
}

@SingleIn(AppScope::class)
class DeepLinkHandler @Inject constructor() {
    fun handle(uri: String): DeepLinkResult = when {
        uri.contains("/product/") -> DeepLinkResult("product_detail", mapOf("id" to uri.substringAfterLast("/")))
        uri.contains("/order/") -> DeepLinkResult("order_detail", mapOf("id" to uri.substringAfterLast("/")))
        uri.contains("/chat") -> DeepLinkResult("chat", emptyMap())
        else -> DeepLinkResult("home", emptyMap())
    }
}

@SingleIn(AppScope::class)
class InAppMessageManager @Inject constructor(
    private val featureFlagManager: com.codeint.shopapp.metro.core.config.FeatureFlagManager,
    private val analyticsTracker: com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
) {
    fun showBanner(message: String, type: String) { analyticsTracker.track("banner_shown", mapOf("type" to type)) }
    fun showBottomSheet(title: String, body: String) {}
}

data class DeepLinkResult(val destination: String, val params: Map<String, String>)
