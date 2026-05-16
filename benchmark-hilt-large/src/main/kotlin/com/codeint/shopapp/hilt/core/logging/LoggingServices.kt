package com.codeint.shopapp.hilt.core.logging

import com.codeint.shopapp.hilt.core.analytics.CrashReporter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class AppLogger @Inject constructor(private val crashReporter: CrashReporter) {
    fun debug(tag: String, message: String) {}
    fun info(tag: String, message: String) {}
    fun warn(tag: String, message: String) {}
    fun error(tag: String, message: String, throwable: Throwable? = null) { throwable?.let { crashReporter.report(it) } }
}

@Singleton class AuditLogger @Inject constructor(private val appLogger: AppLogger) {
    fun logUserAction(action: String, details: Map<String, Any> = emptyMap()) { appLogger.info("AUDIT", "$action: $details") }
    fun logSecurityEvent(event: String) { appLogger.warn("SECURITY", event) }
}
