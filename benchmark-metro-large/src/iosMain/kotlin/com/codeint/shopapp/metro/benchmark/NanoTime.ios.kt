package com.codeint.shopapp.metro.benchmark

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.time.TimeSource

private val startMark = TimeSource.Monotonic.markNow()

internal actual fun nanoTime(): Long = startMark.elapsedNow().inWholeNanoseconds
internal actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
