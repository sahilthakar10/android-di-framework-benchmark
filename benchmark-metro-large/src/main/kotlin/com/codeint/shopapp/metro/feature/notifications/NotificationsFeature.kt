package com.codeint.shopapp.metro.feature.notifications

import com.codeint.shopapp.metro.core.notification.*
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject

class NotificationsViewModel @Inject constructor(
    private val notificationManager: NotificationManager,
    private val deepLinkHandler: DeepLinkHandler,
    private val analytics: AnalyticsTracker
) {
    fun handleNotificationTap(deepLink: String): DeepLinkResult { analytics.track("notification_tap"); return deepLinkHandler.handle(deepLink) }
    fun markAllRead() { analytics.track("notifications_mark_all_read") }
}

class NotificationPreferencesPresenter @Inject constructor(
    private val pushTokenManager: PushTokenManager,
    private val notificationManager: NotificationManager
) {
    fun isRegistered(): Boolean = pushTokenManager.getToken() != null
    fun registerForPush(token: String) { pushTokenManager.registerToken(token) }
    fun unregister() { pushTokenManager.unregisterToken() }
}
