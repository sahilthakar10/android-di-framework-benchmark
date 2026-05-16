package com.codeint.benchmarking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HiltColor = Color(0xFF4CAF50)
private val MetroColor = Color(0xFF2196F3)
private val KoinColor = Color(0xFFFF9800)
private val WinnerGreen = Color(0xFF4CAF50)

@Composable
fun RuntimeResultsView(result: RuntimeBenchmark.ComparisonResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Header
        Text(
            "Hilt vs Metro vs Koin Runtime Results",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "350 classes | 285 bindings | 100 iterations",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Init Time Card
        InitTimeCard(result.hilt, result.metro, result.koin)

        // First Injection Table
        InjectionTableCard(
            title = "First Injection (Cold)",
            hiltData = result.hilt.firstInjectionNanos,
            metroData = result.metro.firstInjectionNanos,
            koinData = result.koin.firstInjectionNanos
        )

        // Warm Injection Table
        InjectionTableCard(
            title = "Warm Injection (Avg of 100)",
            hiltData = result.hilt.warmInjectionAvgNanos,
            metroData = result.metro.warmInjectionAvgNanos,
            koinData = result.koin.warmInjectionAvgNanos
        )

        // Memory Card
        MemoryCard(result.hilt, result.metro, result.koin)

        // Verdict Card
        VerdictCard(result.hilt, result.metro, result.koin)
    }
}

@Composable
private fun InitTimeCard(
    hilt: RuntimeBenchmark.BenchmarkResult,
    metro: RuntimeBenchmark.BenchmarkResult,
    koin: RuntimeBenchmark.BenchmarkResult
) {
    val minInit = minOf(hilt.initTimeNanos, metro.initTimeNanos, koin.initTimeNanos)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            SectionTitle("DI Container Init Time")
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricBox(
                    label = "Hilt",
                    value = formatTime(hilt.initTimeNanos),
                    color = HiltColor,
                    isWinner = hilt.initTimeNanos == minInit,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                MetricBox(
                    label = "Metro",
                    value = formatTime(metro.initTimeNanos),
                    color = MetroColor,
                    isWinner = metro.initTimeNanos == minInit,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                MetricBox(
                    label = "Koin",
                    value = formatTime(koin.initTimeNanos),
                    color = KoinColor,
                    isWinner = koin.initTimeNanos == minInit,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(6.dp))
            val times = listOf("Hilt" to hilt.initTimeNanos, "Metro" to metro.initTimeNanos, "Koin" to koin.initTimeNanos)
            val winner = times.minBy { it.second }
            val slowest = times.maxBy { it.second }
            val pct = percentFaster(winner.second, slowest.second)
            Text(
                "${winner.first} is ${pct}% faster than ${slowest.first}",
                style = MaterialTheme.typography.labelSmall,
                color = WinnerGreen,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InjectionTableCard(
    title: String,
    hiltData: Map<String, Long>,
    metroData: Map<String, Long>,
    koinData: Map<String, Long>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            SectionTitle(title)
            Spacer(Modifier.height(8.dp))

            // Header row
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Class",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    "Hilt",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = HiltColor,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.8f)
                )
                Text(
                    "Metro",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MetroColor,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.8f)
                )
                Text(
                    "Koin",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = KoinColor,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.8f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Data rows
            for (key in hiltData.keys) {
                val h = hiltData[key] ?: 0
                val m = metroData[key] ?: 0
                val k = koinData[key] ?: 0
                val min = minOf(h, m, k)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        key,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1.2f),
                        fontSize = 10.sp
                    )
                    Text(
                        formatTime(h),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        fontWeight = if (h == min) FontWeight.Bold else FontWeight.Normal,
                        color = if (h == min) WinnerGreen else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(0.8f),
                        fontSize = 10.sp
                    )
                    Text(
                        formatTime(m),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        fontWeight = if (m == min) FontWeight.Bold else FontWeight.Normal,
                        color = if (m == min) WinnerGreen else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(0.8f),
                        fontSize = 10.sp
                    )
                    Text(
                        formatTime(k),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        fontWeight = if (k == min) FontWeight.Bold else FontWeight.Normal,
                        color = if (k == min) WinnerGreen else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(0.8f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryCard(
    hilt: RuntimeBenchmark.BenchmarkResult,
    metro: RuntimeBenchmark.BenchmarkResult,
    koin: RuntimeBenchmark.BenchmarkResult
) {
    val minMem = minOf(hilt.memoryDeltaBytes, metro.memoryDeltaBytes, koin.memoryDeltaBytes)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            SectionTitle("Memory Overhead")
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricBox(
                    label = "Hilt",
                    value = "${"%.1f".format(hilt.memoryDeltaKB)} KB",
                    color = HiltColor,
                    isWinner = hilt.memoryDeltaBytes == minMem,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                MetricBox(
                    label = "Metro",
                    value = "${"%.1f".format(metro.memoryDeltaKB)} KB",
                    color = MetroColor,
                    isWinner = metro.memoryDeltaBytes == minMem,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                MetricBox(
                    label = "Koin",
                    value = "${"%.1f".format(koin.memoryDeltaKB)} KB",
                    color = KoinColor,
                    isWinner = koin.memoryDeltaBytes == minMem,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun VerdictCard(
    hilt: RuntimeBenchmark.BenchmarkResult,
    metro: RuntimeBenchmark.BenchmarkResult,
    koin: RuntimeBenchmark.BenchmarkResult
) {
    val initTimes = listOf("Hilt" to hilt.initTimeNanos, "Metro" to metro.initTimeNanos, "Koin" to koin.initTimeNanos)
    val warmTimes = listOf("Hilt" to hilt.totalWarmNanos, "Metro" to metro.totalWarmNanos, "Koin" to koin.totalWarmNanos)
    val memSizes = listOf("Hilt" to hilt.memoryDeltaBytes, "Metro" to metro.memoryDeltaBytes, "Koin" to koin.memoryDeltaBytes)

    val initWinner = initTimes.minBy { it.second }
    val warmWinner = warmTimes.minBy { it.second }
    val memWinner = memSizes.minBy { it.second }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Verdict",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(8.dp))

            val initSlowest = initTimes.maxBy { it.second }
            val initPct = percentFaster(initWinner.second, initSlowest.second)
            VerdictRow("Init", initWinner.first, "${initPct}% faster than ${initSlowest.first}")

            val warmSlowest = warmTimes.maxBy { it.second }
            val warmPct = percentFaster(warmWinner.second, warmSlowest.second)
            VerdictRow("Runtime", warmWinner.first, "${warmPct}% faster than ${warmSlowest.first}")

            VerdictRow("Memory", memWinner.first, "least overhead")
        }
    }
}

@Composable
private fun VerdictRow(category: String, winner: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            "$winner - $detail",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    color: Color,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isWinner) WinnerGreen.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (isWinner) {
            Spacer(Modifier.height(2.dp))
            Text(
                "WINNER",
                style = MaterialTheme.typography.labelSmall,
                color = WinnerGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )
}

private fun formatTime(nanos: Long): String {
    return when {
        nanos >= 1_000_000 -> "${"%.2f".format(nanos / 1_000_000.0)}ms"
        nanos >= 1_000 -> "${"%.0f".format(nanos / 1_000.0)}us"
        else -> "${nanos}ns"
    }
}

private fun percentFaster(a: Long, b: Long): String {
    val faster = minOf(a, b)
    val slower = maxOf(a, b)
    return if (slower > 0) "%.1f".format((1 - faster.toDouble() / slower) * 100) else "0"
}
