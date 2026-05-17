package com.codeint.shopapp.hilt.core.logging

import com.codeint.shopapp.hilt.core.analytics.CrashReporter
import javax.inject.Inject

interface AppLogger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warn(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

class RealAppLogger @Inject constructor(private val crashReporter: CrashReporter) : AppLogger {
    override fun debug(tag: String, message: String) {}
    override fun info(tag: String, message: String) {}
    override fun warn(tag: String, message: String) {}
    override fun error(tag: String, message: String, throwable: Throwable?) { throwable?.let { crashReporter.report(it) } }
}

class AuditLogger @Inject constructor(private val appLogger: AppLogger) {
    fun logUserAction(action: String, details: Map<String, Any> = emptyMap()) { appLogger.info("AUDIT", "$action: $details") }
    fun logSecurityEvent(event: String) { appLogger.warn("SECURITY", event) }
}
