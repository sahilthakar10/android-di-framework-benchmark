package com.codeint.shopapp.kinject.core.logging

import com.codeint.shopapp.kinject.core.analytics.CrashReporter
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface AppLogger { fun debug(tag: String, msg: String); fun info(tag: String, msg: String); fun warn(tag: String, msg: String); fun error(tag: String, msg: String, t: Throwable? = null) }
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealAppLogger(private val crashReporter: CrashReporter) : AppLogger {
    override fun debug(tag: String, msg: String) {}; override fun info(tag: String, msg: String) {}
    override fun warn(tag: String, msg: String) {}; override fun error(tag: String, msg: String, t: Throwable?) { t?.let { crashReporter.report(it) } }
}
@Inject class AuditLogger(private val appLogger: AppLogger) { fun logUserAction(a: String) { appLogger.info("AUDIT", a) } }
