package com.codeint.dibenchmark.runtime

import com.codeint.dibenchmark.annotations.FrameworkType
import java.util.UUID

class BenchmarkSession(
    val sessionId: String = UUID.randomUUID().toString(),
    val startTimestamp: Long = System.currentTimeMillis()
) {
    private var endTimestamp: Long = 0L
    private var isActive = true

    val isRunning: Boolean get() = isActive

    fun end(): BenchmarkSession {
        endTimestamp = System.currentTimeMillis()
        isActive = false
        return this
    }

    fun getReport(): SessionReport {
        val injections = BenchmarkRegistry.getInjectionMetrics()
        val graphs = BenchmarkRegistry.getGraphMetrics()
        val providers = ProviderCallCounter.getMetrics()
        val modules = BenchmarkRegistry.getModuleFrameworks()

        val moduleReports = modules.map { (moduleName, framework) ->
            ModuleReport(
                moduleName = moduleName,
                framework = framework,
                injectionMetrics = injections.filter { it.moduleName == moduleName },
                providerMetrics = providers.filter { it.moduleName == moduleName },
                graphMetrics = graphs.filter { it.moduleName == moduleName },
                compileTimeMetric = null, // Loaded from build output separately
                summary = BenchmarkRegistry.getSummaryForModule(moduleName)
            )
        }

        return SessionReport(
            sessionId = sessionId,
            startTimestamp = startTimestamp,
            endTimestamp = if (endTimestamp > 0) endTimestamp else System.currentTimeMillis(),
            deviceInfo = DeviceInfoProvider.get(),
            moduleReports = moduleReports,
            overallSummary = BenchmarkRegistry.getSummary()
        )
    }
}

data class SessionReport(
    val sessionId: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val deviceInfo: DeviceInfo,
    val moduleReports: List<ModuleReport>,
    val overallSummary: MetricSummary
) {
    val durationMs: Long get() = endTimestamp - startTimestamp

    fun getModulesByFramework(framework: FrameworkType): List<ModuleReport> {
        return moduleReports.filter { it.framework == framework }
    }
}
