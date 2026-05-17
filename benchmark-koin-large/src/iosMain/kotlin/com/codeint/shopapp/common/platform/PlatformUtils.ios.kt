package com.codeint.shopapp.common.platform

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.time.TimeSource

private val startMark = TimeSource.Monotonic.markNow()

actual fun nanoTime(): Long = startMark.elapsedNow().inWholeNanoseconds
actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
