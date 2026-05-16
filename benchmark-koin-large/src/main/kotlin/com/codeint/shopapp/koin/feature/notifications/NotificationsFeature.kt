package com.codeint.shopapp.koin.feature.notifications

import com.codeint.shopapp.koin.core.notification.*
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker


class NotificationsViewModel constructor(
    private val notificationManager: NotificationManager,
    private val deepLinkHandler: DeepLinkHandler,
    private val analytics: AnalyticsTracker
) {
    fun handleNotificationTap(deepLink: String): DeepLinkResult { analytics.track("notification_tap"); return deepLinkHandler.handle(deepLink) }
    fun markAllRead() { analytics.track("notifications_mark_all_read") }
}

class NotificationPreferencesPresenter constructor(
    private val pushTokenManager: PushTokenManager,
    private val notificationManager: NotificationManager
) {
    fun isRegistered(): Boolean = pushTokenManager.getToken() != null
    fun registerForPush(token: String) { pushTokenManager.registerToken(token) }
    fun unregister() { pushTokenManager.unregisterToken() }
}
