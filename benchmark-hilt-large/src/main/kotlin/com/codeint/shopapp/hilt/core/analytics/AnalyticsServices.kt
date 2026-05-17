package com.codeint.shopapp.hilt.core.analytics

import com.codeint.shopapp.hilt.core.storage.PreferencesManager
import javax.inject.Inject

interface AnalyticsTracker {
    fun track(event: String, params: Map<String, Any> = emptyMap())
    fun setUserId(userId: String)
    fun screen(screenName: String)
}

interface CrashReporter {
    fun report(throwable: Throwable)
    fun log(message: String)
    fun setUserId(userId: String)
}

class EventBus @Inject constructor() {
    private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>()
    fun post(event: AnalyticsEvent) { listeners.forEach { it(event) } }
    fun subscribe(listener: (AnalyticsEvent) -> Unit) { listeners.add(listener) }
}

class UserPropertyTracker @Inject constructor() {
    private val properties = mutableMapOf<String, Any>()
    fun set(key: String, value: Any) { properties[key] = value }
    fun get(key: String): Any? = properties[key]
}

class RemoteConfigManager @Inject constructor() {
    fun getString(key: String, default: String) = default
    fun getBoolean(key: String, default: Boolean) = default
    fun getLong(key: String, default: Long) = default
    fun fetch() {}
}

class RealAnalyticsTracker @Inject constructor(
    private val eventBus: EventBus,
    private val userPropertyTracker: UserPropertyTracker
) : AnalyticsTracker {
    override fun track(event: String, params: Map<String, Any>) { eventBus.post(AnalyticsEvent(event, params)) }
    override fun setUserId(userId: String) { userPropertyTracker.set("user_id", userId) }
    override fun screen(screenName: String) { track("screen_view", mapOf("screen" to screenName)) }
}

class RealCrashReporter @Inject constructor() : CrashReporter {
    override fun report(throwable: Throwable) {}
    override fun log(message: String) {}
    override fun setUserId(userId: String) {}
}

class PerformanceMonitor @Inject constructor() {
    fun startTrace(name: String) = TraceHandle(name, System.nanoTime())
    fun endTrace(handle: TraceHandle) {}
}

class ABTestManager @Inject constructor(private val remoteConfig: RemoteConfigManager) {
    fun getVariant(experimentId: String) = remoteConfig.getString("exp_$experimentId", "control")
    fun isInExperiment(experimentId: String) = true
}

class ConsentManager @Inject constructor(private val prefs: PreferencesManager) {
    fun hasAnalyticsConsent() = true
    fun hasAdsConsent() = false
    fun updateConsent(analytics: Boolean, ads: Boolean) {}
}

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
