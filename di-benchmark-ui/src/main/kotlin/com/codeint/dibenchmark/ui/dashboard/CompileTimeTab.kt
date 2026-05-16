package com.codeint.dibenchmark.ui.dashboard

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codeint.dibenchmark.ui.charts.BarChart
import com.codeint.dibenchmark.ui.charts.BarChartData
import com.codeint.dibenchmark.ui.theme.BenchmarkColors
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Composable
fun CompileTimeTab() {
    val context = LocalContext.current
    val compileMetrics = remember { loadCompileMetrics(context) }

    if (compileMetrics.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No compile-time data available")
                Spacer(Modifier.height(8.dp))
                Text(
                    "Run: ./gradlew assembleDebug measureDiCompileTime",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Compile-Time Metrics", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
        }

        // Processor time chart
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Processor Time by Module", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    val chartData = compileMetrics.map { metric ->
                        BarChartData(
                            label = metric.moduleName.substringAfterLast(":"),
                            value = metric.totalProcessorMs.toFloat(),
                            color = when (metric.framework) {
                                "HILT" -> BenchmarkColors.Hilt
                                "METRO" -> BenchmarkColors.Metro
                                else -> BenchmarkColors.Warning
                            }
                        )
                    }
                    BarChart(
                        data = chartData,
                        unit = "ms",
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            }
        }

        // Generated code stats
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Generated Code", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    val totalFiles = compileMetrics.sumOf { it.generatedFileCount }
                    val totalSize = compileMetrics.sumOf { it.generatedCodeSizeBytes }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$totalFiles", style = MaterialTheme.typography.headlineMedium)
                            Text("Files", style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${totalSize / 1024}KB", style = MaterialTheme.typography.headlineMedium)
                            Text("Total Size", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        // Per-module details
        items(compileMetrics) { metric ->
            CompileMetricCard(metric)
        }
    }
}

@Composable
private fun CompileMetricCard(metric: CompileMetricDisplay) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(metric.moduleName, style = MaterialTheme.typography.titleSmall)
                Text("[${metric.framework}]", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(4.dp))
            Text("KSP: ${metric.kspDurationMs}ms | KAPT: ${metric.kaptDurationMs}ms")
            Text("Generated: ${metric.generatedFileCount} files (${metric.generatedCodeSizeBytes / 1024}KB)")
            Text("Incremental: ${metric.incrementalBuild}")
        }
    }
}

private fun loadCompileMetrics(context: Context): List<CompileMetricDisplay> {
    // Try to load from app's assets or external storage
    val metricsDir = File(context.filesDir, "di-benchmark")
    if (!metricsDir.exists()) return emptyList()

    val json = Json { ignoreUnknownKeys = true }
    return metricsDir.listFiles()
        ?.filter { it.name.endsWith("-compile-metrics.json") }
        ?.mapNotNull { file ->
            try {
                json.decodeFromString<CompileMetricDisplay>(file.readText())
            } catch (_: Exception) {
                null
            }
        } ?: emptyList()
}

@Serializable
private data class CompileMetricDisplay(
    val moduleName: String = "",
    val framework: String = "UNKNOWN",
    val kspDurationMs: Long = 0,
    val kaptDurationMs: Long = 0,
    val totalProcessorMs: Long = 0,
    val generatedFileCount: Int = 0,
    val generatedCodeSizeBytes: Long = 0,
    val incrementalBuild: Boolean = false
)
