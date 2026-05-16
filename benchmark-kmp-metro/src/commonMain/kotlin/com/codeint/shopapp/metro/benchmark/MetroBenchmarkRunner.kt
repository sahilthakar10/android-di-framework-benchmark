package com.codeint.shopapp.metro.benchmark

import com.codeint.shopapp.common.platform.nanoTime
import com.codeint.shopapp.metro.graph.GraphFactory

/**
 * Metro Runtime Benchmark — Proper Methodology
 *
 * Follows kotlinx-benchmark / JMH best practices:
 * 1. Warmup phase: Multiple iterations to stabilize (excluded from results)
 * 2. Blackhole: Return values consumed to prevent dead code elimination
 * 3. Single graph lifecycle: createGraph() once, measure injection patterns
 *    (This matches real-world usage — graph is created at app startup)
 * 4. Measurement: Multiple iterations averaged for statistical significance
 *
 * Reference: https://github.com/Kotlin/kotlinx-benchmark/blob/master/docs/writing-benchmarks.md
 */

data class MetroBenchmarkResult(
    val initTimeNanos: Long,
    val coldInjectionNanos: Map<String, Long>,
    val warmInjectionAvgNanos: Map<String, Long>,
    val totalWarmNanos: Long
)

fun runMetroBenchmark(warmIterations: Int = 100): MetroBenchmarkResult {
    // ── SETUP: Warmup phase (excluded from measurement) ──
    // Purpose: Eliminate class loading, memory allocation warmup, and
    // Native compiler optimization stabilization (no JIT on iOS, but
    // memory allocator and CPU cache need to warm up)
    val warmupGraph = GraphFactory.create()
    val warmupTargets = listOf(
        { warmupGraph.homeViewModel },
        { warmupGraph.searchViewModel },
        { warmupGraph.productDetailViewModel },
        { warmupGraph.cartViewModel },
        { warmupGraph.checkoutViewModel },
        { warmupGraph.profileViewModel },
        { warmupGraph.chatViewModel },
        { warmupGraph.orderHistoryViewModel },
        { warmupGraph.analyticsTracker },
        { warmupGraph.productRepository }
    )
    // Run warmup iterations — results discarded
    repeat(5) {
        warmupTargets.forEach { resolve -> blackhole(resolve()) }
    }

    // ── MEASUREMENT 1: Container initialization ──
    // Real-world scenario: This happens once at app startup
    val initStart = nanoTime()
    val graph = GraphFactory.create()
    val initTime = nanoTime() - initStart

    // ── MEASUREMENT 2: Cold injection (first access after fresh graph) ──
    // Real-world scenario: First screen load after app start
    val cold = linkedMapOf<String, Long>()
    cold["HomeViewModel"] = measureNanos { blackhole(graph.homeViewModel) }
    cold["SearchViewModel"] = measureNanos { blackhole(graph.searchViewModel) }
    cold["ProductDetailVM"] = measureNanos { blackhole(graph.productDetailViewModel) }
    cold["CartViewModel"] = measureNanos { blackhole(graph.cartViewModel) }
    cold["CheckoutViewModel"] = measureNanos { blackhole(graph.checkoutViewModel) }
    cold["ProfileViewModel"] = measureNanos { blackhole(graph.profileViewModel) }
    cold["ChatViewModel"] = measureNanos { blackhole(graph.chatViewModel) }
    cold["OrderHistoryVM"] = measureNanos { blackhole(graph.orderHistoryViewModel) }
    cold["AnalyticsTracker"] = measureNanos { blackhole(graph.analyticsTracker) }
    cold["ProductRepository"] = measureNanos { blackhole(graph.productRepository) }

    // ── MEASUREMENT 3: Warm injection (repeated access) ──
    // Real-world scenario: User navigating between screens, ViewModels
    // being re-created, singletons accessed from cached graph
    val warm = linkedMapOf<String, Long>()
    var totalWarm = 0L
    val targets = listOf<Pair<String, () -> Any>>(
        "HomeViewModel" to { graph.homeViewModel },
        "SearchViewModel" to { graph.searchViewModel },
        "ProductDetailVM" to { graph.productDetailViewModel },
        "CartViewModel" to { graph.cartViewModel },
        "CheckoutViewModel" to { graph.checkoutViewModel },
        "ProfileViewModel" to { graph.profileViewModel },
        "ChatViewModel" to { graph.chatViewModel },
        "OrderHistoryVM" to { graph.orderHistoryViewModel },
        "AnalyticsTracker" to { graph.analyticsTracker },
        "ProductRepository" to { graph.productRepository }
    )
    for ((name, resolver) in targets) {
        var sum = 0L
        repeat(warmIterations) {
            val s = nanoTime()
            blackhole(resolver())
            sum += nanoTime() - s
        }
        warm[name] = sum / warmIterations
        totalWarm += sum
    }

    // No teardown needed — Metro graph is a single object with no global state
    return MetroBenchmarkResult(initTime, cold, warm, totalWarm)
}

// Blackhole: prevents LLVM dead code elimination on Kotlin/Native.
// Without this, LLVM can optimize away graph property access if the
// result is unused, giving misleadingly fast times.
@Suppress("UNUSED_PARAMETER")
private fun blackhole(value: Any?) {
    // Intentionally empty — the function call itself prevents DCE
}

private inline fun measureNanos(block: () -> Unit): Long {
    val s = nanoTime()
    block()
    return nanoTime() - s
}
