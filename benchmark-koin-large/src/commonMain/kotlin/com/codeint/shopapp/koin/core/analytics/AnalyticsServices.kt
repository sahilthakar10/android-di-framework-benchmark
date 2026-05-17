package com.codeint.shopapp.koin.core.analytics

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

import com.codeint.shopapp.koin.core.storage.PreferencesManager

interface AnalyticsTracker { fun track(event: String, params: Map<String, Any> = emptyMap()); fun setUserId(userId: String); fun screen(name: String) }
interface CrashReporter { fun report(t: Throwable); fun log(msg: String); fun setUserId(userId: String) }

class EventBus { private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>(); fun post(e: AnalyticsEvent) { listeners.forEach { it(e) } }; fun subscribe(l: (AnalyticsEvent) -> Unit) { listeners.add(l) } }
class UserPropertyTracker { private val props = mutableMapOf<String, Any>(); fun set(k: String, v: Any) { props[k] = v }; fun get(k: String) = props[k] }
class RemoteConfigManager { fun getString(k: String, d: String) = d; fun getBoolean(k: String, d: Boolean) = d; fun getLong(k: String, d: Long) = d }

class RealAnalyticsTracker(private val eventBus: EventBus, private val userPropertyTracker: UserPropertyTracker) : AnalyticsTracker {
    override fun track(event: String, params: Map<String, Any>) { eventBus.post(AnalyticsEvent(event, params)) }
    override fun setUserId(userId: String) { userPropertyTracker.set("user_id", userId) }
    override fun screen(name: String) { track("screen_view", mapOf("screen" to name)) }
}
class RealCrashReporter : CrashReporter { override fun report(t: Throwable) {}; override fun log(msg: String) {}; override fun setUserId(userId: String) {} }
class PerformanceMonitor { fun startTrace(name: String) = TraceHandle(name, nanoTime()) }
class ABTestManager(private val rc: RemoteConfigManager) { fun getVariant(id: String) = rc.getString("exp_$id", "control") }
class ConsentManager(private val prefs: PreferencesManager) { fun hasAnalyticsConsent() = true; fun hasAdsConsent() = false }

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
