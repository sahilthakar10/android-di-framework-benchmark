package com.codeint.benchmarking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codeint.benchmarking.ui.theme.BenchMarkingTheme
import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.annotations.ScopeType
import com.codeint.dibenchmark.runtime.BenchmarkRegistry
import com.codeint.dibenchmark.runtime.DiBenchmark
import com.codeint.dibenchmark.ui.BenchmarkActivity
import com.codeint.sample.hilt.SampleHiltDataSource
import com.codeint.sample.hilt.SampleHiltRepository
import com.codeint.sample.hilt.SampleHiltService
import com.codeint.sample.hilt.SampleHiltSingleton
import com.codeint.sample.metro.MetroBenchmarkHelper
import com.codeint.sample.metro.SampleMetroGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var hiltRepository: SampleHiltRepository
    @Inject lateinit var hiltDataSource: SampleHiltDataSource
    @Inject lateinit var hiltSingleton: SampleHiltSingleton
    @Inject lateinit var hiltService: SampleHiltService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BenchMarkingTheme {
                MainScreen(
                    onRunHiltBenchmark = { benchmarkHiltInjections() },
                    onRunMetroBenchmark = { benchmarkMetroInjections() },
                    onRunBothBenchmarks = {
                        benchmarkHiltInjections()
                        benchmarkMetroInjections()
                    }
                )
            }
        }
    }

    private fun benchmarkHiltInjections() {
        // Benchmark each real Hilt injection
        DiBenchmark.injection(
            className = "SampleHiltRepository",
            moduleName = ":sample-hilt-module",
            framework = FrameworkType.HILT,
            scopeType = ScopeType.FACTORY
        ) { hiltRepository.getData() }

        DiBenchmark.injection(
            className = "SampleHiltDataSource",
            moduleName = ":sample-hilt-module",
            framework = FrameworkType.HILT,
            scopeType = ScopeType.FACTORY
        ) { hiltDataSource.fetch() }

        DiBenchmark.injection(
            className = "SampleHiltSingleton",
            moduleName = ":sample-hilt-module",
            framework = FrameworkType.HILT,
            scopeType = ScopeType.SINGLETON
        ) { hiltSingleton.process() }

        DiBenchmark.provider(
            providerName = "SampleHiltModule.provideSampleService",
            moduleName = ":sample-hilt-module",
            framework = FrameworkType.HILT
        ) { hiltService.execute() }

        // Run multiple iterations for meaningful stats
        repeat(50) { i ->
            DiBenchmark.injection(
                className = "SampleHiltRepository",
                moduleName = ":sample-hilt-module",
                framework = FrameworkType.HILT,
                scopeType = ScopeType.FACTORY
            ) { hiltRepository.getData() }

            DiBenchmark.injection(
                className = "SampleHiltDataSource",
                moduleName = ":sample-hilt-module",
                framework = FrameworkType.HILT,
                scopeType = ScopeType.FACTORY
            ) { hiltDataSource.fetch() }

            DiBenchmark.provider(
                providerName = "SampleHiltModule.provideSampleService",
                moduleName = ":sample-hilt-module",
                framework = FrameworkType.HILT
            ) { hiltService.execute() }
        }

        val summary = BenchmarkRegistry.getSummaryForModule(":sample-hilt-module")
        Log.i("BenchmarkDemo", "Hilt: ${summary.totalInjections} injections, avg=${summary.avgInjectionTimeNanos}ns")
    }

    private fun benchmarkMetroInjections() {
        // Benchmark Metro graph creation
        val graph = DiBenchmark.createMetroGraph<SampleMetroGraph>(
            moduleName = ":sample-metro-module"
        ) { MetroBenchmarkHelper.createGraph() }

        // Benchmark accessing dependencies from the graph
        DiBenchmark.injection(
            className = "SampleMetroRepository",
            moduleName = ":sample-metro-module",
            framework = FrameworkType.METRO,
            scopeType = ScopeType.FACTORY
        ) { graph.repository.getData() }

        DiBenchmark.injection(
            className = "SampleMetroService",
            moduleName = ":sample-metro-module",
            framework = FrameworkType.METRO,
            scopeType = ScopeType.FACTORY
        ) { graph.service.process() }

        DiBenchmark.injection(
            className = "SampleMetroAnalytics",
            moduleName = ":sample-metro-module",
            framework = FrameworkType.METRO,
            scopeType = ScopeType.FACTORY
        ) { graph.analytics.track("metro_test") }

        DiBenchmark.provider(
            providerName = "SampleMetroGraph.provideConfig",
            moduleName = ":sample-metro-module",
            framework = FrameworkType.METRO
        ) { graph.service.process() }

        // Multiple iterations
        repeat(50) {
            val freshGraph = DiBenchmark.createMetroGraph<SampleMetroGraph>(
                moduleName = ":sample-metro-module"
            ) { MetroBenchmarkHelper.createGraph() }

            DiBenchmark.injection(
                className = "SampleMetroRepository",
                moduleName = ":sample-metro-module",
                framework = FrameworkType.METRO,
                scopeType = ScopeType.FACTORY
            ) { freshGraph.repository.getData() }

            DiBenchmark.injection(
                className = "SampleMetroService",
                moduleName = ":sample-metro-module",
                framework = FrameworkType.METRO,
                scopeType = ScopeType.FACTORY
            ) { freshGraph.service.process() }
        }

        val summary = BenchmarkRegistry.getSummaryForModule(":sample-metro-module")
        Log.i("BenchmarkDemo", "Metro: ${summary.totalInjections} injections, avg=${summary.avgInjectionTimeNanos}ns")
    }
}

@Composable
fun MainScreen(
    onRunHiltBenchmark: () -> Unit,
    onRunMetroBenchmark: () -> Unit,
    onRunBothBenchmarks: () -> Unit
) {
    val context = LocalContext.current
    var statusText by remember { mutableStateOf("Ready to benchmark") }
    var hiltCount by remember { mutableIntStateOf(0) }
    var metroCount by remember { mutableIntStateOf(0) }
    var runtimeResult by remember { mutableStateOf<RuntimeBenchmark.ComparisonResult?>(null) }
    var isRunningRuntime by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Text("DI Benchmark SDK", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("Hilt vs Metro vs Koin", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(24.dp))

            // ========== SECTION 1: Hilt/Metro Small Sample Benchmarks ==========
            Text("Sample Module Benchmarks", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(statusText, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$hiltCount", style = MaterialTheme.typography.headlineSmall)
                            Text("Hilt", style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$metroCount", style = MaterialTheme.typography.headlineSmall)
                            Text("Metro", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        onRunHiltBenchmark()
                        hiltCount = BenchmarkRegistry.getMetricsForModule(":sample-hilt-module").size
                        statusText = "Hilt: $hiltCount injections"
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Hilt") }

                Button(
                    onClick = {
                        onRunMetroBenchmark()
                        metroCount = BenchmarkRegistry.getMetricsForModule(":sample-metro-module").size
                        statusText = "Metro: $metroCount injections"
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Metro") }

                OutlinedButton(
                    onClick = {
                        BenchmarkRegistry.reset(); DiBenchmark.startSession()
                        hiltCount = 0; metroCount = 0; statusText = "Reset"
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Reset") }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { context.startActivity(Intent(context, BenchmarkActivity::class.java)) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Open Dashboard") }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // ========== SECTION 2: Hilt vs Metro vs Koin Runtime Benchmark ==========
            Text("Hilt vs Metro vs Koin Runtime (350 classes)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Real e-commerce app: 14 domains, 13 features, 285 bindings",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    isRunningRuntime = true
                    runtimeResult = null
                    val app = context.applicationContext as android.app.Application
                    Thread {
                        val result = RuntimeBenchmark.runFullComparison(app, iterations = 100)
                        Log.i("RuntimeBenchmark", result.summary)
                        runtimeResult = result
                        isRunningRuntime = false
                    }.start()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRunningRuntime,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                if (isRunningRuntime) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onTertiary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Running benchmark...")
                } else {
                    Text("Run Hilt vs Metro vs Koin Runtime Benchmark")
                }
            }

            // Results
            if (runtimeResult != null) {
                Spacer(Modifier.height(16.dp))
                RuntimeResultsView(result = runtimeResult!!)
            }

            Spacer(Modifier.height(32.dp))
        }
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
