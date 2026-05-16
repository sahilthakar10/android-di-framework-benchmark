package com.codeint.dibenchmark.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.runtime.BenchmarkRegistry
import com.codeint.dibenchmark.runtime.InjectionMetric
import com.codeint.dibenchmark.ui.charts.BarChart
import com.codeint.dibenchmark.ui.charts.BarChartData
import com.codeint.dibenchmark.ui.components.FrameworkBadge
import com.codeint.dibenchmark.ui.theme.BenchmarkColors

@Composable
fun ModulesTab() {
    val moduleFrameworks = remember { BenchmarkRegistry.getModuleFrameworks() }
    var selectedModule by remember { mutableStateOf(moduleFrameworks.keys.firstOrNull()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (moduleFrameworks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No injection data recorded yet")
            }
            return
        }

        // Module selector
        ScrollableTabRow(
            selectedTabIndex = moduleFrameworks.keys.indexOf(selectedModule).coerceAtLeast(0)
        ) {
            moduleFrameworks.forEach { (name, framework) ->
                Tab(
                    selected = name == selectedModule,
                    onClick = { selectedModule = name },
                    text = {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(name.substringAfterLast(":"))
                            FrameworkBadge(framework, compact = true)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        selectedModule?.let { module ->
            ModuleDetail(module, moduleFrameworks[module] ?: FrameworkType.UNKNOWN)
        }
    }
}

@Composable
private fun ModuleDetail(moduleName: String, framework: FrameworkType) {
    val metrics = remember(moduleName) { BenchmarkRegistry.getMetricsForModule(moduleName) }
    val summary = remember(moduleName) { BenchmarkRegistry.getSummaryForModule(moduleName) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Summary", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    SummaryRow("Total Injections", "${summary.totalInjections}")
                    SummaryRow("Avg Time", formatNanos(summary.avgInjectionTimeNanos))
                    SummaryRow("P50", formatNanos(summary.p50InjectionTimeNanos))
                    SummaryRow("P95", formatNanos(summary.p95InjectionTimeNanos))
                    SummaryRow("P99", formatNanos(summary.p99InjectionTimeNanos))
                    SummaryRow("Max", formatNanos(summary.maxInjectionTimeNanos))
                    SummaryRow("Memory Delta", formatBytes(summary.totalMemoryDeltaBytes))
                    SummaryRow("First Injection", formatNanos(summary.firstInjectionLatencyNanos))
                }
            }
        }

        // Injection time bar chart
        if (metrics.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Top Injections by Time", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))

                        val topMetrics = metrics.sortedByDescending { it.injectionTimeNanos }.take(10)
                        val chartData = topMetrics.map { metric ->
                            BarChartData(
                                label = metric.className.substringAfterLast("."),
                                value = metric.injectionTimeNanos / 1000f, // micros
                                color = if (framework == FrameworkType.HILT) BenchmarkColors.Hilt else BenchmarkColors.Metro
                            )
                        }
                        BarChart(
                            data = chartData,
                            unit = "us",
                            modifier = Modifier.fillMaxWidth().height(250.dp)
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("All Injections", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            items(metrics.sortedByDescending { it.injectionTimeNanos }) { metric ->
                InjectionRow(metric)
            }
        }
    }
}

@Composable
private fun InjectionRow(metric: InjectionMetric) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            metric.className.substringAfterLast("."),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            formatNanos(metric.injectionTimeNanos),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            formatBytes(metric.memoryDeltaBytes),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatNanos(nanos: Long): String {
    return when {
        nanos >= 1_000_000 -> "${"%.2f".format(nanos / 1_000_000.0)}ms"
        nanos >= 1_000 -> "${"%.1f".format(nanos / 1_000.0)}us"
        else -> "${nanos}ns"
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1_048_576 -> "${"%.1f".format(bytes / 1_048_576.0)}MB"
        bytes >= 1024 -> "${"%.1f".format(bytes / 1024.0)}KB"
        else -> "${bytes}B"
    }
}
