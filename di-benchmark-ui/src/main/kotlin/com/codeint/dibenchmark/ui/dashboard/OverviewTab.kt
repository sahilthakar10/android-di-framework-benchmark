package com.codeint.dibenchmark.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.runtime.BenchmarkRegistry
import com.codeint.dibenchmark.runtime.DiBenchmark
import com.codeint.dibenchmark.runtime.MetricSummary
import com.codeint.dibenchmark.ui.components.FrameworkBadge
import com.codeint.dibenchmark.ui.components.MetricCard

@Composable
fun OverviewTab() {
    val summary = remember { BenchmarkRegistry.getSummary() }
    val moduleFrameworks = remember { BenchmarkRegistry.getModuleFrameworks() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Session Summary", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    label = "Total Injections",
                    value = "${summary.totalInjections}",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Avg Time",
                    value = formatMicros(summary.avgInjectionTimeNanos),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    label = "P95",
                    value = formatMicros(summary.p95InjectionTimeNanos),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Memory Delta",
                    value = formatBytes(summary.totalMemoryDeltaBytes),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    label = "First Injection",
                    value = formatMicros(summary.firstInjectionLatencyNanos),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Provider Calls",
                    value = "${summary.totalProviderCalls}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (moduleFrameworks.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Modules", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
            }

            items(moduleFrameworks.entries.toList()) { (moduleName, framework) ->
                ModuleOverviewCard(moduleName, framework)
            }
        }
    }
}

@Composable
private fun ModuleOverviewCard(moduleName: String, framework: FrameworkType) {
    val moduleSummary = remember(moduleName) { BenchmarkRegistry.getSummaryForModule(moduleName) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(moduleName, style = MaterialTheme.typography.titleMedium)
                FrameworkBadge(framework)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Injections: ${moduleSummary.totalInjections}")
                Text("Avg: ${formatMicros(moduleSummary.avgInjectionTimeNanos)}")
                Text("P95: ${formatMicros(moduleSummary.p95InjectionTimeNanos)}")
            }
        }
    }
}

private fun formatMicros(nanos: Long): String {
    return when {
        nanos >= 1_000_000 -> "${"%.1f".format(nanos / 1_000_000.0)}ms"
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
