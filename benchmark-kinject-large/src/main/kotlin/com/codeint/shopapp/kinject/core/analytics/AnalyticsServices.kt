package com.codeint.shopapp.kinject.core.analytics

import com.codeint.shopapp.kinject.core.storage.PreferencesManager
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface AnalyticsTracker { fun track(event: String, params: Map<String, Any> = emptyMap()); fun setUserId(userId: String); fun screen(name: String) }
interface CrashReporter { fun report(t: Throwable); fun log(msg: String); fun setUserId(userId: String) }

@Inject @SingleIn(AppScope::class) class EventBus { private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>(); fun post(e: AnalyticsEvent) { listeners.forEach { it(e) } } }
@Inject @SingleIn(AppScope::class) class UserPropertyTracker { private val props = mutableMapOf<String, Any>(); fun set(k: String, v: Any) { props[k] = v }; fun get(k: String) = props[k] }
@Inject @SingleIn(AppScope::class) class RemoteConfigManager { fun getString(k: String, d: String) = d; fun getBoolean(k: String, d: Boolean) = d; fun getLong(k: String, d: Long) = d }
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealAnalyticsTracker(private val eventBus: EventBus, private val upt: UserPropertyTracker) : AnalyticsTracker {
    override fun track(event: String, params: Map<String, Any>) { eventBus.post(AnalyticsEvent(event, params)) }
    override fun setUserId(userId: String) { upt.set("user_id", userId) }; override fun screen(name: String) { track("screen_view", mapOf("screen" to name)) }
}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealCrashReporter : CrashReporter { override fun report(t: Throwable) {}; override fun log(msg: String) {}; override fun setUserId(userId: String) {} }
@Inject class PerformanceMonitor { fun startTrace(name: String) = TraceHandle(name, System.nanoTime()) }
@Inject class ABTestManager(private val rc: RemoteConfigManager) { fun getVariant(id: String) = rc.getString("exp_$id", "control") }
@Inject class ConsentManager(private val prefs: PreferencesManager) { fun hasAnalyticsConsent() = true }

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
