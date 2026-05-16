package com.codeint.dibenchmark.export

class CIReporter {

    fun printReport(report: BenchmarkReportExport) {
        println("=" .repeat(70))
        println("DI BENCHMARK REPORT")
        println("=" .repeat(70))
        println("Session: ${report.sessionId}")
        println("Device: ${report.device.manufacturer} ${report.device.model} (SDK ${report.device.sdkVersion})")
        println("Duration: ${report.endTimestamp - report.startTimestamp}ms")
        println()

        for (module in report.modules) {
            printModuleReport(module)
        }

        println("=" .repeat(70))
    }

    private fun printModuleReport(module: ModuleReportExport) {
        println("-".repeat(50))
        println("Module: ${module.name} [${module.framework}]")
        println("-".repeat(50))

        val s = module.runtime.summary
        println("  Injections:     ${s.totalInjections}")
        println("  Avg time:       ${formatNanos(s.avgInjectionTimeNanos)}")
        println("  P50:            ${formatNanos(s.p50InjectionTimeNanos)}")
        println("  P95:            ${formatNanos(s.p95InjectionTimeNanos)}")
        println("  P99:            ${formatNanos(s.p99InjectionTimeNanos)}")
        println("  Max:            ${formatNanos(s.maxInjectionTimeNanos)}")
        println("  Memory delta:   ${formatBytes(s.totalMemoryDeltaBytes)}")
        println("  First inject:   ${formatNanos(s.firstInjectionLatencyNanos)}")
        println("  Provider calls: ${s.totalProviderCalls}")

        if (module.compileTime != null) {
            val ct = module.compileTime
            println()
            println("  Compile-time metrics:")
            println("    AP time:        ${ct.annotationProcessingMs}ms")
            println("    CodeGen time:   ${ct.codeGenerationMs}ms")
            println("    Generated files: ${ct.generatedFileCount}")
            println("    Generated size: ${formatBytes(ct.generatedCodeSizeBytes)}")
            println("    Incremental:    ${ct.incrementalBuild}")
        }

        if (module.runtime.graphTraversals.isNotEmpty()) {
            println()
            println("  Graph traversals:")
            for (g in module.runtime.graphTraversals) {
                println("    ${g.graphName}: ${formatNanos(g.traversalTimeNanos)}")
            }
        }

        // Top 5 slowest injections
        val slowest = module.runtime.injections.sortedByDescending { it.injectionTimeNanos }.take(5)
        if (slowest.isNotEmpty()) {
            println()
            println("  Top 5 slowest injections:")
            for (inj in slowest) {
                println("    ${inj.className}: ${formatNanos(inj.injectionTimeNanos)} (mem: ${formatBytes(inj.memoryDeltaBytes)})")
            }
        }
        println()
    }

    private fun formatNanos(nanos: Long): String {
        return when {
            nanos >= 1_000_000 -> "${nanos / 1_000_000.0}ms"
            nanos >= 1_000 -> "${nanos / 1_000.0}µs"
            else -> "${nanos}ns"
        }
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1_048_576 -> "${"%.2f".format(bytes / 1_048_576.0)}MB"
            bytes >= 1024 -> "${"%.2f".format(bytes / 1024.0)}KB"
            else -> "${bytes}B"
        }
    }
}
