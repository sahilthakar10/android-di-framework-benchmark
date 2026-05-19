package com.codeint.shopapp.kinject.core.notification

import com.codeint.shopapp.kinject.core.storage.PreferencesManager
import com.codeint.shopapp.kinject.core.auth.SessionManager
import com.codeint.shopapp.kinject.core.config.FeatureFlagManager
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import me.tatarka.inject.annotations.Inject

@Inject class NotificationManager(private val prefs: PreferencesManager) { fun isEnabled() = prefs.getBoolean("notif", true) }
@Inject class PushTokenManager(private val sm: SessionManager) { fun getToken(): String? = null }
@Inject class DeepLinkHandler { fun handle(uri: String) = DeepLinkResult(if (uri.contains("/product/")) "product" else "home", emptyMap()) }
@Inject class InAppMessageManager(private val ff: FeatureFlagManager, private val at: AnalyticsTracker) { fun showBanner(msg: String, type: String) { at.track("banner") } }
data class DeepLinkResult(val destination: String, val params: Map<String, String>)
