package com.codeint.dibenchmark.runtime

import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.annotations.ScopeType

data class InjectionMetric(
    val className: String,
    val moduleName: String,
    val framework: FrameworkType,
    val injectionTimeNanos: Long,
    val memoryDeltaBytes: Long,
    val isFirstInjection: Boolean,
    val scopeType: ScopeType,
    val timestamp: Long,
    val threadName: String
)

data class ProviderMetric(
    val providerName: String,
    val moduleName: String,
    val framework: FrameworkType,
    val callCount: Int,
    val totalTimeNanos: Long,
    val averageTimeNanos: Long
)

data class GraphTraversalMetric(
    val graphName: String,
    val moduleName: String,
    val framework: FrameworkType,
    val traversalTimeNanos: Long,
    val timestamp: Long,
    val nodeCount: Int = 0,
    val edgeCount: Int = 0,
    val maxDepth: Int = 0
)

data class CompileTimeMetric(
    val moduleName: String,
    val framework: FrameworkType,
    val annotationProcessingMs: Long,
    val codeGenerationMs: Long,
    val generatedFileCount: Int,
    val generatedCodeSizeBytes: Long,
    val incrementalBuild: Boolean,
    val totalProcessorMs: Long,
    val buildTimestamp: Long
)

data class MetricSummary(
    val totalInjections: Int,
    val avgInjectionTimeNanos: Long,
    val p50InjectionTimeNanos: Long,
    val p95InjectionTimeNanos: Long,
    val p99InjectionTimeNanos: Long,
    val maxInjectionTimeNanos: Long,
    val totalMemoryDeltaBytes: Long,
    val firstInjectionLatencyNanos: Long,
    val totalProviderCalls: Int
)

data class DeviceInfo(
    val model: String,
    val manufacturer: String,
    val sdkVersion: Int,
    val cpuCores: Int,
    val totalMemoryBytes: Long
)

data class ModuleReport(
    val moduleName: String,
    val framework: FrameworkType,
    val injectionMetrics: List<InjectionMetric>,
    val providerMetrics: List<ProviderMetric>,
    val graphMetrics: List<GraphTraversalMetric>,
    val compileTimeMetric: CompileTimeMetric?,
    val summary: MetricSummary
)

data class BenchmarkConfig(
    val enabled: Boolean = true,
    val warmupIterations: Int = 3,
    val trackMemory: Boolean = true,
    val trackProviderCalls: Boolean = true,
    val logToLogcat: Boolean = true
) {
    companion object {
        val DEFAULT = BenchmarkConfig()
    }
}
