package com.codeint.shopapp.metro.core.logging

import com.codeint.shopapp.metro.core.analytics.CrashReporter
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface AppLogger { fun debug(tag: String, msg: String); fun info(tag: String, msg: String); fun warn(tag: String, msg: String); fun error(tag: String, msg: String, t: Throwable? = null) }
@SingleIn(AppScope::class) class RealAppLogger @Inject constructor(private val crashReporter: CrashReporter) : AppLogger {
    override fun debug(tag: String, msg: String) {}; override fun info(tag: String, msg: String) {}
    override fun warn(tag: String, msg: String) {}; override fun error(tag: String, msg: String, t: Throwable?) { t?.let { crashReporter.report(it) } }
}
class AuditLogger @Inject constructor(private val appLogger: AppLogger) { fun logUserAction(a: String) { appLogger.info("AUDIT", a) } }
