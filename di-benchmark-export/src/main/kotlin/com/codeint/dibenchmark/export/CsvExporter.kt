package com.codeint.dibenchmark.export

class CsvExporter {

    fun exportInjections(report: BenchmarkReportExport): String {
        val sb = StringBuilder()
        sb.appendLine("module,framework,class_name,injection_time_nanos,injection_time_micros,memory_delta_bytes,is_first_injection,scope_type,timestamp,thread")

        for (module in report.modules) {
            for (injection in module.runtime.injections) {
                sb.appendLine(
                    "${module.name},${module.framework},${injection.className}," +
                        "${injection.injectionTimeNanos},${injection.injectionTimeMicros}," +
                        "${injection.memoryDeltaBytes},${injection.isFirstInjection}," +
                        "${injection.scopeType},${injection.timestamp},${injection.threadName}"
                )
            }
        }
        return sb.toString()
    }

    fun exportProviders(report: BenchmarkReportExport): String {
        val sb = StringBuilder()
        sb.appendLine("module,framework,provider_name,call_count,total_time_nanos,avg_time_nanos,avg_time_micros")

        for (module in report.modules) {
            for (provider in module.runtime.providers) {
                sb.appendLine(
                    "${module.name},${module.framework},${provider.providerName}," +
                        "${provider.callCount},${provider.totalTimeNanos}," +
                        "${provider.averageTimeNanos},${provider.averageTimeMicros}"
                )
            }
        }
        return sb.toString()
    }

    fun exportSummary(report: BenchmarkReportExport): String {
        val sb = StringBuilder()
        sb.appendLine("module,framework,total_injections,avg_time_nanos,avg_time_micros,p50_nanos,p95_nanos,p99_nanos,max_nanos,total_memory_bytes,total_memory_kb,first_injection_latency_nanos,provider_calls")

        for (module in report.modules) {
            val s = module.runtime.summary
            sb.appendLine(
                "${module.name},${module.framework},${s.totalInjections}," +
                    "${s.avgInjectionTimeNanos},${s.avgInjectionTimeMicros}," +
                    "${s.p50InjectionTimeNanos},${s.p95InjectionTimeNanos}," +
                    "${s.p99InjectionTimeNanos},${s.maxInjectionTimeNanos}," +
                    "${s.totalMemoryDeltaBytes},${s.totalMemoryDeltaKB}," +
                    "${s.firstInjectionLatencyNanos},${s.totalProviderCalls}"
            )
        }
        return sb.toString()
    }

    fun exportToFile(content: String, filePath: String) {
        java.io.File(filePath).apply {
            parentFile?.mkdirs()
            writeText(content)
        }
    }
}
