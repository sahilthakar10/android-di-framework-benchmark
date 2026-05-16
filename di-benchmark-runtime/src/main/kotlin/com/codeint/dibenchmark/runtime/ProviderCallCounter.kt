package com.codeint.dibenchmark.runtime

import com.codeint.dibenchmark.annotations.FrameworkType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

object ProviderCallCounter {

    @PublishedApi
    internal val counters = ConcurrentHashMap<String, ProviderStats>()

    inline fun <T> track(
        providerName: String,
        moduleName: String,
        framework: FrameworkType = FrameworkType.UNKNOWN,
        crossinline block: () -> T
    ): T {
        if (!DiBenchmark.isEnabled) {
            return block()
        }

        val key = "$moduleName::$providerName"
        val stats = counters.getOrPut(key) {
            ProviderStats(providerName, moduleName, framework)
        }

        val startNanos = System.nanoTime()
        val result = block()
        val elapsed = System.nanoTime() - startNanos

        stats.callCount.incrementAndGet()
        stats.totalTimeNanos.addAndGet(elapsed)

        return result
    }

    fun getMetrics(): List<ProviderMetric> {
        return counters.values.map { stats ->
            val count = stats.callCount.get()
            val totalTime = stats.totalTimeNanos.get()
            ProviderMetric(
                providerName = stats.providerName,
                moduleName = stats.moduleName,
                framework = stats.framework,
                callCount = count,
                totalTimeNanos = totalTime,
                averageTimeNanos = if (count > 0) totalTime / count else 0
            )
        }
    }

    fun reset() {
        counters.clear()
    }

    @PublishedApi
    internal class ProviderStats(
        val providerName: String,
        val moduleName: String,
        val framework: FrameworkType,
        val callCount: AtomicInteger = AtomicInteger(0),
        val totalTimeNanos: AtomicLong = AtomicLong(0)
    )
}
