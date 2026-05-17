package com.codeint.shopapp.koin.core.notification

import com.codeint.shopapp.koin.core.auth.SessionManager
import com.codeint.shopapp.koin.core.storage.PreferencesManager
import com.codeint.shopapp.koin.core.config.FeatureFlagManager
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker

class NotificationManager(private val prefs: PreferencesManager) { fun isEnabled() = prefs.getBoolean("notif", true); fun setEnabled(e: Boolean) { prefs.putBoolean("notif", e) } }
class PushTokenManager(private val sm: SessionManager) { fun registerToken(t: String) {}; fun getToken(): String? = null }
class DeepLinkHandler { fun handle(uri: String) = DeepLinkResult(if (uri.contains("/product/")) "product" else "home", emptyMap()) }
class InAppMessageManager(private val ff: FeatureFlagManager, private val at: AnalyticsTracker) { fun showBanner(msg: String, type: String) { at.track("banner", mapOf("type" to type)) } }
data class DeepLinkResult(val destination: String, val params: Map<String, String>)
