package com.codeint.dibenchmark.runtime

import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.annotations.ScopeType

object InjectionTimer {

    inline fun <T> measure(
        className: String,
        moduleName: String,
        framework: FrameworkType = FrameworkType.UNKNOWN,
        scopeType: ScopeType = ScopeType.UNKNOWN,
        crossinline block: () -> T
    ): T {
        if (!DiBenchmark.isEnabled) {
            return block()
        }

        val memoryBefore = MemoryTracker.currentAllocation()
        val startNanos = System.nanoTime()

        val result = block()

        val endNanos = System.nanoTime()
        val memoryAfter = MemoryTracker.currentAllocation()

        val metric = InjectionMetric(
            className = className,
            moduleName = moduleName,
            framework = framework,
            injectionTimeNanos = endNanos - startNanos,
            memoryDeltaBytes = memoryAfter - memoryBefore,
            isFirstInjection = !BenchmarkRegistry.hasBeenInjected(className),
            scopeType = scopeType,
            timestamp = System.currentTimeMillis(),
            threadName = Thread.currentThread().name
        )

        BenchmarkRegistry.recordInjection(metric)
        return result
    }

    inline fun <T> measureGraphCreation(
        graphName: String,
        moduleName: String,
        framework: FrameworkType,
        crossinline block: () -> T
    ): T {
        if (!DiBenchmark.isEnabled) {
            return block()
        }

        val startNanos = System.nanoTime()
        val result = block()
        val endNanos = System.nanoTime()

        val metric = GraphTraversalMetric(
            graphName = graphName,
            moduleName = moduleName,
            framework = framework,
            traversalTimeNanos = endNanos - startNanos,
            timestamp = System.currentTimeMillis()
        )

        BenchmarkRegistry.recordGraphTraversal(metric)
        return result
    }
}
