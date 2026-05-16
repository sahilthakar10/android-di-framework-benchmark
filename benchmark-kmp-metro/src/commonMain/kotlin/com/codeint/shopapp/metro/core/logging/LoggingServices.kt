package com.codeint.shopapp.metro.core.logging

import com.codeint.shopapp.metro.core.analytics.CrashReporter
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class AppLogger @Inject constructor(private val crashReporter: CrashReporter) {
    fun debug(tag: String, message: String) {}
    fun info(tag: String, message: String) {}
    fun warn(tag: String, message: String) {}
    fun error(tag: String, message: String, throwable: Throwable? = null) { throwable?.let { crashReporter.report(it) } }
}

@SingleIn(AppScope::class)
class AuditLogger @Inject constructor(private val appLogger: AppLogger) {
    fun logUserAction(action: String, details: Map<String, Any> = emptyMap()) { appLogger.info("AUDIT", "$action: $details") }
    fun logSecurityEvent(event: String) { appLogger.warn("SECURITY", event) }
}
