package com.codeint.shopapp.koin.core.logging

import com.codeint.shopapp.koin.core.analytics.CrashReporter

interface AppLogger { fun debug(tag: String, msg: String); fun info(tag: String, msg: String); fun warn(tag: String, msg: String); fun error(tag: String, msg: String, t: Throwable? = null) }
class RealAppLogger(private val crashReporter: CrashReporter) : AppLogger {
    override fun debug(tag: String, msg: String) {}; override fun info(tag: String, msg: String) {}
    override fun warn(tag: String, msg: String) {}; override fun error(tag: String, msg: String, t: Throwable?) { t?.let { crashReporter.report(it) } }
}
class AuditLogger(private val appLogger: AppLogger) { fun logUserAction(a: String) { appLogger.info("AUDIT", a) } }
