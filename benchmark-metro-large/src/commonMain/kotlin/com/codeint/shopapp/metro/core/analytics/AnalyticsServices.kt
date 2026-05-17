package com.codeint.shopapp.metro.core.analytics

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.storage.PreferencesManager
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface AnalyticsTracker { fun track(event: String, params: Map<String, Any> = emptyMap()); fun setUserId(userId: String); fun screen(name: String) }
interface CrashReporter { fun report(t: Throwable); fun log(msg: String); fun setUserId(userId: String) }

@SingleIn(AppScope::class) class EventBus @Inject constructor() { private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>(); fun post(e: AnalyticsEvent) { listeners.forEach { it(e) } } }
@SingleIn(AppScope::class) class UserPropertyTracker @Inject constructor() { private val props = mutableMapOf<String, Any>(); fun set(k: String, v: Any) { props[k] = v }; fun get(k: String) = props[k] }
@SingleIn(AppScope::class) class RemoteConfigManager @Inject constructor() { fun getString(k: String, d: String) = d; fun getBoolean(k: String, d: Boolean) = d; fun getLong(k: String, d: Long) = d }
@SingleIn(AppScope::class) class RealAnalyticsTracker @Inject constructor(private val eventBus: EventBus, private val upt: UserPropertyTracker) : AnalyticsTracker {
    override fun track(event: String, params: Map<String, Any>) { eventBus.post(AnalyticsEvent(event, params)) }
    override fun setUserId(userId: String) { upt.set("user_id", userId) }; override fun screen(name: String) { track("screen_view", mapOf("screen" to name)) }
}
@SingleIn(AppScope::class) class RealCrashReporter @Inject constructor() : CrashReporter { override fun report(t: Throwable) {}; override fun log(msg: String) {}; override fun setUserId(userId: String) {} }
class PerformanceMonitor @Inject constructor() { fun startTrace(name: String) = TraceHandle(name, nanoTime()) }
class ABTestManager @Inject constructor(private val rc: RemoteConfigManager) { fun getVariant(id: String) = rc.getString("exp_$id", "control") }
class ConsentManager @Inject constructor(private val prefs: PreferencesManager) { fun hasAnalyticsConsent() = true }

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
