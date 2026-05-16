package com.codeint.shopapp.common.platform

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.time.TimeSource

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

private val timeSource = TimeSource.Monotonic
private val startMark = timeSource.markNow()

actual fun nanoTime(): Long = startMark.elapsedNow().inWholeNanoseconds
