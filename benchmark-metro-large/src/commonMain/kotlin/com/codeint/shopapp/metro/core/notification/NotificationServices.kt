package com.codeint.shopapp.metro.core.notification

import com.codeint.shopapp.metro.core.storage.PreferencesManager
import com.codeint.shopapp.metro.core.auth.SessionManager
import com.codeint.shopapp.metro.core.config.FeatureFlagManager
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject

class NotificationManager @Inject constructor(private val prefs: PreferencesManager) { fun isEnabled() = prefs.getBoolean("notif", true) }
class PushTokenManager @Inject constructor(private val sm: SessionManager) { fun getToken(): String? = null }
class DeepLinkHandler @Inject constructor() { fun handle(uri: String) = DeepLinkResult(if (uri.contains("/product/")) "product" else "home", emptyMap()) }
class InAppMessageManager @Inject constructor(private val ff: FeatureFlagManager, private val at: AnalyticsTracker) { fun showBanner(msg: String, type: String) { at.track("banner") } }
data class DeepLinkResult(val destination: String, val params: Map<String, String>)
