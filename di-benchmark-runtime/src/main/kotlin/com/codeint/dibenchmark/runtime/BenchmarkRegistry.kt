package com.codeint.dibenchmark.runtime

import android.util.Log
import com.codeint.dibenchmark.annotations.FrameworkType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object BenchmarkRegistry {

    private val injectionMetrics = CopyOnWriteArrayList<InjectionMetric>()
    private val graphMetrics = CopyOnWriteArrayList<GraphTraversalMetric>()
    private val injectedClasses = ConcurrentHashMap.newKeySet<String>()
    private val moduleFrameworks = ConcurrentHashMap<String, FrameworkType>()

    fun recordInjection(metric: InjectionMetric) {
        injectionMetrics.add(metric)
        injectedClasses.add(metric.className)
        moduleFrameworks.putIfAbsent(metric.moduleName, metric.framework)

        if (DiBenchmark.config.logToLogcat) {
            Log.d(
                "DiBenchmark",
                "[${metric.framework}] ${metric.className} injected in ${metric.injectionTimeNanos / 1000}µs " +
                    "(memory: ${metric.memoryDeltaBytes}B, first: ${metric.isFirstInjection})"
            )
        }
    }

    fun recordGraphTraversal(metric: GraphTraversalMetric) {
        graphMetrics.add(metric)

        if (DiBenchmark.config.logToLogcat) {
            Log.d(
                "DiBenchmark",
                "[${metric.framework}] Graph '${metric.graphName}' created in ${metric.traversalTimeNanos / 1000}µs"
            )
        }
    }

    fun hasBeenInjected(className: String): Boolean = className in injectedClasses

    fun getInjectionMetrics(): List<InjectionMetric> = injectionMetrics.toList()

    fun getGraphMetrics(): List<GraphTraversalMetric> = graphMetrics.toList()

    fun getModuleFrameworks(): Map<String, FrameworkType> = moduleFrameworks.toMap()

    fun getMetricsForModule(moduleName: String): List<InjectionMetric> {
        return injectionMetrics.filter { it.moduleName == moduleName }
    }

    fun getGraphMetricsForModule(moduleName: String): List<GraphTraversalMetric> {
        return graphMetrics.filter { it.moduleName == moduleName }
    }

    fun getSummary(): MetricSummary {
        val metrics = injectionMetrics.toList()
        if (metrics.isEmpty()) {
            return MetricSummary(0, 0, 0, 0, 0, 0, 0, 0, 0)
        }

        val sortedTimes = metrics.map { it.injectionTimeNanos }.sorted()
        val firstInjection = metrics.filter { it.isFirstInjection }

        return MetricSummary(
            totalInjections = metrics.size,
            avgInjectionTimeNanos = sortedTimes.average().toLong(),
            p50InjectionTimeNanos = sortedTimes.percentile(50),
            p95InjectionTimeNanos = sortedTimes.percentile(95),
            p99InjectionTimeNanos = sortedTimes.percentile(99),
            maxInjectionTimeNanos = sortedTimes.last(),
            totalMemoryDeltaBytes = metrics.sumOf { it.memoryDeltaBytes },
            firstInjectionLatencyNanos = firstInjection.maxOfOrNull { it.injectionTimeNanos } ?: 0,
            totalProviderCalls = ProviderCallCounter.getMetrics().sumOf { it.callCount }
        )
    }

    fun getSummaryForModule(moduleName: String): MetricSummary {
        val metrics = injectionMetrics.filter { it.moduleName == moduleName }
        if (metrics.isEmpty()) {
            return MetricSummary(0, 0, 0, 0, 0, 0, 0, 0, 0)
        }

        val sortedTimes = metrics.map { it.injectionTimeNanos }.sorted()
        val firstInjection = metrics.filter { it.isFirstInjection }
        val providerCalls = ProviderCallCounter.getMetrics()
            .filter { it.moduleName == moduleName }
            .sumOf { it.callCount }

        return MetricSummary(
            totalInjections = metrics.size,
            avgInjectionTimeNanos = sortedTimes.average().toLong(),
            p50InjectionTimeNanos = sortedTimes.percentile(50),
            p95InjectionTimeNanos = sortedTimes.percentile(95),
            p99InjectionTimeNanos = sortedTimes.percentile(99),
            maxInjectionTimeNanos = sortedTimes.last(),
            totalMemoryDeltaBytes = metrics.sumOf { it.memoryDeltaBytes },
            firstInjectionLatencyNanos = firstInjection.maxOfOrNull { it.injectionTimeNanos } ?: 0,
            totalProviderCalls = providerCalls
        )
    }

    fun reset() {
        injectionMetrics.clear()
        graphMetrics.clear()
        injectedClasses.clear()
        moduleFrameworks.clear()
        ProviderCallCounter.reset()
        ScopeTracker.reset()
    }

    private fun List<Long>.percentile(p: Int): Long {
        if (isEmpty()) return 0
        val index = (size * p / 100).coerceIn(0, size - 1)
        return this[index]
    }
}
