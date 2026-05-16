package com.codeint.dibenchmark.ui.dashboard

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.codeint.dibenchmark.export.*
import com.codeint.dibenchmark.runtime.BenchmarkRegistry
import com.codeint.dibenchmark.runtime.DiBenchmark
import com.codeint.dibenchmark.runtime.DeviceInfoProvider
import java.io.File

@Composable
fun ExportTab() {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf(ExportFormat.JSON) }
    var exportContent by remember { mutableStateOf(ExportContent.ALL) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Export Benchmark Data", style = MaterialTheme.typography.headlineSmall)

        // Format selector
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Format", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExportFormat.entries.forEach { format ->
                        FilterChip(
                            selected = selectedFormat == format,
                            onClick = { selectedFormat = format },
                            label = { Text(format.name) }
                        )
                    }
                }
            }
        }

        // Content selector
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Content", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExportContent.entries.forEach { content ->
                        FilterChip(
                            selected = exportContent == content,
                            onClick = { exportContent = content },
                            label = { Text(content.label) }
                        )
                    }
                }
            }
        }

        // Current stats
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Session Data", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                val summary = BenchmarkRegistry.getSummary()
                Text("Injections recorded: ${summary.totalInjections}")
                Text("Modules: ${BenchmarkRegistry.getModuleFrameworks().size}")
                Text("Provider calls: ${summary.totalProviderCalls}")
            }
        }

        Spacer(Modifier.weight(1f))

        // Export buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { exportAndSave(context, selectedFormat, exportContent) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save to File")
            }
            OutlinedButton(
                onClick = { exportAndShare(context, selectedFormat, exportContent) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Share")
            }
        }
    }
}

private enum class ExportFormat { JSON, CSV }

private enum class ExportContent(val label: String) {
    ALL("All Data"),
    INJECTIONS("Injections"),
    PROVIDERS("Providers"),
    SUMMARY("Summary Only")
}

private fun exportAndSave(context: Context, format: ExportFormat, content: ExportContent) {
    val report = buildExportReport()
    val fileName = "di-benchmark-${System.currentTimeMillis()}"

    val exportDir = File(context.getExternalFilesDir(null), "di-benchmark-exports")
    exportDir.mkdirs()

    when (format) {
        ExportFormat.JSON -> {
            val file = File(exportDir, "$fileName.json")
            JsonExporter().exportToFile(report, file.absolutePath)
            Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
        ExportFormat.CSV -> {
            val exporter = CsvExporter()
            val csvContent = when (content) {
                ExportContent.INJECTIONS -> exporter.exportInjections(report)
                ExportContent.PROVIDERS -> exporter.exportProviders(report)
                ExportContent.SUMMARY -> exporter.exportSummary(report)
                ExportContent.ALL -> exporter.exportInjections(report)
            }
            val file = File(exportDir, "$fileName.csv")
            exporter.exportToFile(csvContent, file.absolutePath)
            Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }
}

private fun exportAndShare(context: Context, format: ExportFormat, content: ExportContent) {
    val report = buildExportReport()
    val fileName = "di-benchmark-${System.currentTimeMillis()}"

    val cacheDir = File(context.cacheDir, "di-benchmark-share")
    cacheDir.mkdirs()

    val file = when (format) {
        ExportFormat.JSON -> {
            File(cacheDir, "$fileName.json").also {
                JsonExporter().exportToFile(report, it.absolutePath)
            }
        }
        ExportFormat.CSV -> {
            val csvContent = CsvExporter().exportInjections(report)
            File(cacheDir, "$fileName.csv").also { it.writeText(csvContent) }
        }
    }

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.dibenchmark.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = if (format == ExportFormat.JSON) "application/json" else "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Benchmark Report"))
}

private fun buildExportReport(): BenchmarkReportExport {
    val session = DiBenchmark.currentSession()
    val deviceInfo = DeviceInfoProvider.get()
    val modules = BenchmarkRegistry.getModuleFrameworks()

    val moduleDataList = modules.map { (name, framework) ->
        val injections = BenchmarkRegistry.getMetricsForModule(name)
        val providers = com.codeint.dibenchmark.runtime.ProviderCallCounter.getMetrics()
            .filter { it.moduleName == name }
        val graphs = BenchmarkRegistry.getGraphMetricsForModule(name)
        val summary = BenchmarkRegistry.getSummaryForModule(name)

        ModuleData(
            name = name,
            framework = framework,
            injections = injections.map { inj ->
                InjectionData(
                    className = inj.className,
                    injectionTimeNanos = inj.injectionTimeNanos,
                    memoryDeltaBytes = inj.memoryDeltaBytes,
                    isFirstInjection = inj.isFirstInjection,
                    scopeType = inj.scopeType,
                    timestamp = inj.timestamp,
                    threadName = inj.threadName
                )
            },
            providers = providers.map { p ->
                ProviderData(
                    providerName = p.providerName,
                    callCount = p.callCount,
                    totalTimeNanos = p.totalTimeNanos,
                    averageTimeNanos = p.averageTimeNanos
                )
            },
            graphTraversals = graphs.map { g ->
                GraphTraversalData(
                    graphName = g.graphName,
                    traversalTimeNanos = g.traversalTimeNanos,
                    nodeCount = g.nodeCount,
                    edgeCount = g.edgeCount,
                    maxDepth = g.maxDepth,
                    timestamp = g.timestamp
                )
            },
            summary = SummaryData(
                totalInjections = summary.totalInjections,
                avgInjectionTimeNanos = summary.avgInjectionTimeNanos,
                p50InjectionTimeNanos = summary.p50InjectionTimeNanos,
                p95InjectionTimeNanos = summary.p95InjectionTimeNanos,
                p99InjectionTimeNanos = summary.p99InjectionTimeNanos,
                maxInjectionTimeNanos = summary.maxInjectionTimeNanos,
                totalMemoryDeltaBytes = summary.totalMemoryDeltaBytes,
                firstInjectionLatencyNanos = summary.firstInjectionLatencyNanos,
                totalProviderCalls = summary.totalProviderCalls
            ),
            compileTime = null
        )
    }

    return ReportConverter.toExport(
        sessionId = session?.sessionId ?: "no-session",
        startTimestamp = session?.startTimestamp ?: 0,
        endTimestamp = System.currentTimeMillis(),
        device = DeviceData(
            model = deviceInfo.model,
            manufacturer = deviceInfo.manufacturer,
            sdkVersion = deviceInfo.sdkVersion,
            cpuCores = deviceInfo.cpuCores,
            totalMemoryBytes = deviceInfo.totalMemoryBytes
        ),
        app = AppData(
            packageName = "com.codeint.benchmarking",
            versionName = "1.0.0",
            versionCode = 1,
            buildType = "debug"
        ),
        modules = moduleDataList
    )
}
