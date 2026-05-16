package com.codeint.shopapp.hilt.core.analytics

import javax.inject.Inject
import javax.inject.Singleton

@Singleton class AnalyticsTracker @Inject constructor(
    private val eventBus: EventBus,
    private val userPropertyTracker: UserPropertyTracker
) {
    fun track(event: String, params: Map<String, Any> = emptyMap()) { eventBus.post(AnalyticsEvent(event, params)) }
    fun setUserId(userId: String) { userPropertyTracker.set("user_id", userId) }
    fun screen(screenName: String) { track("screen_view", mapOf("screen" to screenName)) }
}

@Singleton class CrashReporter @Inject constructor() {
    fun report(throwable: Throwable) {}
    fun log(message: String) {}
    fun setUserId(userId: String) {}
}

@Singleton class PerformanceMonitor @Inject constructor() {
    fun startTrace(name: String): TraceHandle = TraceHandle(name, System.nanoTime())
    fun endTrace(handle: TraceHandle) {}
}

@Singleton class UserPropertyTracker @Inject constructor() {
    private val properties = mutableMapOf<String, Any>()
    fun set(key: String, value: Any) { properties[key] = value }
    fun get(key: String): Any? = properties[key]
}

@Singleton class ABTestManager @Inject constructor(private val remoteConfigManager: RemoteConfigManager) {
    fun getVariant(experimentId: String): String = remoteConfigManager.getString("exp_$experimentId", "control")
    fun isInExperiment(experimentId: String): Boolean = true
}

@Singleton class EventBus @Inject constructor() {
    private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>()
    fun post(event: AnalyticsEvent) { listeners.forEach { it(event) } }
    fun subscribe(listener: (AnalyticsEvent) -> Unit) { listeners.add(listener) }
}

@Singleton class RemoteConfigManager @Inject constructor() {
    fun getString(key: String, default: String): String = default
    fun getBoolean(key: String, default: Boolean): Boolean = default
    fun getLong(key: String, default: Long): Long = default
    fun fetch() {}
}

@Singleton class ConsentManager @Inject constructor(private val preferencesManager: com.codeint.shopapp.hilt.core.storage.PreferencesManager) {
    fun hasAnalyticsConsent(): Boolean = true
    fun hasAdsConsent(): Boolean = false
    fun updateConsent(analytics: Boolean, ads: Boolean) {}
}

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
