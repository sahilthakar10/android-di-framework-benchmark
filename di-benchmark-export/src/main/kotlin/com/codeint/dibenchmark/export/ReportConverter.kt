package com.codeint.dibenchmark.export

import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.annotations.ScopeType

/**
 * Converts runtime data models to export-friendly serializable models.
 * This lives in the export module to avoid adding serialization deps to runtime.
 */
object ReportConverter {

    fun toExport(
        sessionId: String,
        startTimestamp: Long,
        endTimestamp: Long,
        device: DeviceData,
        app: AppData,
        modules: List<ModuleData>
    ): BenchmarkReportExport {
        return BenchmarkReportExport(
            sessionId = sessionId,
            startTimestamp = startTimestamp,
            endTimestamp = endTimestamp,
            device = DeviceInfoExport(
                model = device.model,
                manufacturer = device.manufacturer,
                sdkVersion = device.sdkVersion,
                cpuCores = device.cpuCores,
                totalMemoryBytes = device.totalMemoryBytes
            ),
            app = AppInfoExport(
                packageName = app.packageName,
                versionName = app.versionName,
                versionCode = app.versionCode,
                buildType = app.buildType
            ),
            modules = modules.map { toModuleExport(it) }
        )
    }

    private fun toModuleExport(module: ModuleData): ModuleReportExport {
        return ModuleReportExport(
            name = module.name,
            framework = module.framework.name,
            runtime = RuntimeMetricsExport(
                injections = module.injections.map { inj ->
                    InjectionMetricExport(
                        className = inj.className,
                        injectionTimeNanos = inj.injectionTimeNanos,
                        injectionTimeMicros = inj.injectionTimeNanos / 1000.0,
                        memoryDeltaBytes = inj.memoryDeltaBytes,
                        isFirstInjection = inj.isFirstInjection,
                        scopeType = inj.scopeType.name,
                        timestamp = inj.timestamp,
                        threadName = inj.threadName
                    )
                },
                providers = module.providers.map { prov ->
                    ProviderMetricExport(
                        providerName = prov.providerName,
                        callCount = prov.callCount,
                        totalTimeNanos = prov.totalTimeNanos,
                        averageTimeNanos = prov.averageTimeNanos,
                        averageTimeMicros = prov.averageTimeNanos / 1000.0
                    )
                },
                graphTraversals = module.graphTraversals.map { g ->
                    GraphTraversalExport(
                        graphName = g.graphName,
                        traversalTimeNanos = g.traversalTimeNanos,
                        traversalTimeMicros = g.traversalTimeNanos / 1000.0,
                        nodeCount = g.nodeCount,
                        edgeCount = g.edgeCount,
                        maxDepth = g.maxDepth,
                        timestamp = g.timestamp
                    )
                },
                summary = toSummaryExport(module.summary)
            ),
            compileTime = module.compileTime?.let {
                CompileTimeMetricsExport(
                    annotationProcessingMs = it.annotationProcessingMs,
                    codeGenerationMs = it.codeGenerationMs,
                    generatedFileCount = it.generatedFileCount,
                    generatedCodeSizeBytes = it.generatedCodeSizeBytes,
                    incrementalBuild = it.incrementalBuild,
                    totalProcessorMs = it.totalProcessorMs,
                    buildTimestamp = it.buildTimestamp
                )
            }
        )
    }

    private fun toSummaryExport(summary: SummaryData): SummaryExport {
        return SummaryExport(
            totalInjections = summary.totalInjections,
            avgInjectionTimeNanos = summary.avgInjectionTimeNanos,
            avgInjectionTimeMicros = summary.avgInjectionTimeNanos / 1000.0,
            p50InjectionTimeNanos = summary.p50InjectionTimeNanos,
            p95InjectionTimeNanos = summary.p95InjectionTimeNanos,
            p99InjectionTimeNanos = summary.p99InjectionTimeNanos,
            maxInjectionTimeNanos = summary.maxInjectionTimeNanos,
            totalMemoryDeltaBytes = summary.totalMemoryDeltaBytes,
            totalMemoryDeltaKB = summary.totalMemoryDeltaBytes / 1024.0,
            firstInjectionLatencyNanos = summary.firstInjectionLatencyNanos,
            totalProviderCalls = summary.totalProviderCalls
        )
    }
}

// Simple data classes that mirror runtime models without Android dependencies
data class DeviceData(
    val model: String,
    val manufacturer: String,
    val sdkVersion: Int,
    val cpuCores: Int,
    val totalMemoryBytes: Long
)

data class AppData(
    val packageName: String,
    val versionName: String,
    val versionCode: Int,
    val buildType: String
)

data class ModuleData(
    val name: String,
    val framework: FrameworkType,
    val injections: List<InjectionData>,
    val providers: List<ProviderData>,
    val graphTraversals: List<GraphTraversalData>,
    val summary: SummaryData,
    val compileTime: CompileTimeData?
)

data class InjectionData(
    val className: String,
    val injectionTimeNanos: Long,
    val memoryDeltaBytes: Long,
    val isFirstInjection: Boolean,
    val scopeType: ScopeType,
    val timestamp: Long,
    val threadName: String
)

data class ProviderData(
    val providerName: String,
    val callCount: Int,
    val totalTimeNanos: Long,
    val averageTimeNanos: Long
)

data class GraphTraversalData(
    val graphName: String,
    val traversalTimeNanos: Long,
    val nodeCount: Int,
    val edgeCount: Int,
    val maxDepth: Int,
    val timestamp: Long
)

data class SummaryData(
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

data class CompileTimeData(
    val annotationProcessingMs: Long,
    val codeGenerationMs: Long,
    val generatedFileCount: Int,
    val generatedCodeSizeBytes: Long,
    val incrementalBuild: Boolean,
    val totalProcessorMs: Long,
    val buildTimestamp: Long
)
