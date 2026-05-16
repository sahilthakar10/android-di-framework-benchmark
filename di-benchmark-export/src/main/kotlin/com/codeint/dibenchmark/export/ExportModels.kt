package com.codeint.dibenchmark.export

import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.annotations.ScopeType
import kotlinx.serialization.Serializable

@Serializable
data class BenchmarkReportExport(
    val version: String = "1.0.0",
    val sessionId: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val device: DeviceInfoExport,
    val app: AppInfoExport,
    val modules: List<ModuleReportExport>
)

@Serializable
data class DeviceInfoExport(
    val model: String,
    val manufacturer: String,
    val sdkVersion: Int,
    val cpuCores: Int,
    val totalMemoryBytes: Long
)

@Serializable
data class AppInfoExport(
    val packageName: String,
    val versionName: String,
    val versionCode: Int,
    val buildType: String
)

@Serializable
data class ModuleReportExport(
    val name: String,
    val framework: String,
    val runtime: RuntimeMetricsExport,
    val compileTime: CompileTimeMetricsExport?
)

@Serializable
data class RuntimeMetricsExport(
    val injections: List<InjectionMetricExport>,
    val providers: List<ProviderMetricExport>,
    val graphTraversals: List<GraphTraversalExport>,
    val summary: SummaryExport
)

@Serializable
data class InjectionMetricExport(
    val className: String,
    val injectionTimeNanos: Long,
    val injectionTimeMicros: Double,
    val memoryDeltaBytes: Long,
    val isFirstInjection: Boolean,
    val scopeType: String,
    val timestamp: Long,
    val threadName: String
)

@Serializable
data class ProviderMetricExport(
    val providerName: String,
    val callCount: Int,
    val totalTimeNanos: Long,
    val averageTimeNanos: Long,
    val averageTimeMicros: Double
)

@Serializable
data class GraphTraversalExport(
    val graphName: String,
    val traversalTimeNanos: Long,
    val traversalTimeMicros: Double,
    val nodeCount: Int,
    val edgeCount: Int,
    val maxDepth: Int,
    val timestamp: Long
)

@Serializable
data class CompileTimeMetricsExport(
    val annotationProcessingMs: Long,
    val codeGenerationMs: Long,
    val generatedFileCount: Int,
    val generatedCodeSizeBytes: Long,
    val incrementalBuild: Boolean,
    val totalProcessorMs: Long,
    val buildTimestamp: Long
)

@Serializable
data class SummaryExport(
    val totalInjections: Int,
    val avgInjectionTimeNanos: Long,
    val avgInjectionTimeMicros: Double,
    val p50InjectionTimeNanos: Long,
    val p95InjectionTimeNanos: Long,
    val p99InjectionTimeNanos: Long,
    val maxInjectionTimeNanos: Long,
    val totalMemoryDeltaBytes: Long,
    val totalMemoryDeltaKB: Double,
    val firstInjectionLatencyNanos: Long,
    val totalProviderCalls: Int
)
