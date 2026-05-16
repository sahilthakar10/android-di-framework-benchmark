package com.codeint.shopapp.koin.benchmark

import com.codeint.shopapp.common.platform.nanoTime
import com.codeint.shopapp.koin.di.*
import com.codeint.shopapp.koin.feature.home.HomeViewModel
import com.codeint.shopapp.koin.feature.search.SearchViewModel
import com.codeint.shopapp.koin.feature.productdetail.ProductDetailViewModel
import com.codeint.shopapp.koin.feature.cart.CartViewModel
import com.codeint.shopapp.koin.feature.checkout.CheckoutViewModel
import com.codeint.shopapp.koin.feature.profile.ProfileViewModel
import com.codeint.shopapp.koin.feature.chat.ChatViewModel
import com.codeint.shopapp.koin.feature.orders.OrderHistoryViewModel
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.data.product.ProductRepository
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * Koin Runtime Benchmark — Proper Methodology
 *
 * Follows kotlinx-benchmark / JMH best practices:
 * 1. Warmup phase: startKoin + full resolution once (excluded from results)
 * 2. Single Koin lifecycle: startKoin() once, keep alive for all measurements
 *    (This matches real-world usage — Koin starts once at app launch and stays
 *    alive for the entire app session. Apps should NEVER cycle startKoin/stopKoin.)
 * 3. Blackhole: Consume resolved values to prevent dead code elimination
 * 4. createdAtStart: 19 critical singletons eagerly initialized (best practice)
 *
 * Reference: https://insert-koin.io/docs/reference/koin-mp/kmp/
 * Reference: https://github.com/Kotlin/kotlinx-benchmark/blob/master/docs/writing-benchmarks.md
 */

data class KoinBenchmarkResult(
    val initTimeNanos: Long,
    val coldInjectionNanos: Map<String, Long>,
    val warmInjectionAvgNanos: Map<String, Long>,
    val totalWarmNanos: Long
)

fun runKoinBenchmark(warmIterations: Int = 100): KoinBenchmarkResult {
    // ── SETUP: Clean any stale Koin state from previous runs ──
    try { stopKoin() } catch (_: Exception) {}

    // ── SETUP: Warmup phase (excluded from measurement) ──
    // Purpose: Warm up class loading, Koin's internal HashMap allocation,
    // and Kotlin/Native memory allocator.
    // startKoin once, resolve all 10 targets, then stopKoin.
    val warmupKoin = startKoin { modules(allShopAppModules) }.koin
    val warmupResolvers: List<() -> Any> = listOf(
        { warmupKoin.get<HomeViewModel>() },
        { warmupKoin.get<SearchViewModel>() },
        { warmupKoin.get<ProductDetailViewModel>() },
        { warmupKoin.get<CartViewModel>() },
        { warmupKoin.get<CheckoutViewModel>() },
        { warmupKoin.get<ProfileViewModel>() },
        { warmupKoin.get<ChatViewModel>() },
        { warmupKoin.get<OrderHistoryViewModel>() },
        { warmupKoin.get<AnalyticsTracker>() },
        { warmupKoin.get<ProductRepository>() }
    )
    repeat(5) {
        warmupResolvers.forEach { resolve -> blackhole(resolve()) }
    }
    stopKoin()

    // ── MEASUREMENT 1: Container initialization ──
    // Real-world scenario: This happens once at app startup.
    // Includes: module DSL processing, HashMap allocation, 19 createdAtStart singletons.
    val initStart = nanoTime()
    val koinApp = startKoin { modules(allShopAppModules) }
    val initTime = nanoTime() - initStart
    val koin = koinApp.koin

    // ── MEASUREMENT 2: Cold injection (first access from this Koin instance) ──
    // Real-world scenario: First screen load — singletons resolved for first time,
    // factory instances created with full dependency chain resolution.
    val cold = linkedMapOf<String, Long>()
    cold["HomeViewModel"] = measureNanos { blackhole(koin.get<HomeViewModel>()) }
    cold["SearchViewModel"] = measureNanos { blackhole(koin.get<SearchViewModel>()) }
    cold["ProductDetailVM"] = measureNanos { blackhole(koin.get<ProductDetailViewModel>()) }
    cold["CartViewModel"] = measureNanos { blackhole(koin.get<CartViewModel>()) }
    cold["CheckoutViewModel"] = measureNanos { blackhole(koin.get<CheckoutViewModel>()) }
    cold["ProfileViewModel"] = measureNanos { blackhole(koin.get<ProfileViewModel>()) }
    cold["ChatViewModel"] = measureNanos { blackhole(koin.get<ChatViewModel>()) }
    cold["OrderHistoryVM"] = measureNanos { blackhole(koin.get<OrderHistoryViewModel>()) }
    cold["AnalyticsTracker"] = measureNanos { blackhole(koin.get<AnalyticsTracker>()) }
    cold["ProductRepository"] = measureNanos { blackhole(koin.get<ProductRepository>()) }

    // ── MEASUREMENT 3: Warm injection (repeated access, same Koin instance) ──
    // Real-world scenario: User navigating between screens throughout the session.
    // Singletons return cached instances (HashMap lookup).
    // Factories create new instances but resolve singleton deps from cache.
    val warm = linkedMapOf<String, Long>()
    var totalWarm = 0L
    val targets = listOf<Pair<String, () -> Any>>(
        "HomeViewModel" to { koin.get<HomeViewModel>() },
        "SearchViewModel" to { koin.get<SearchViewModel>() },
        "ProductDetailVM" to { koin.get<ProductDetailViewModel>() },
        "CartViewModel" to { koin.get<CartViewModel>() },
        "CheckoutViewModel" to { koin.get<CheckoutViewModel>() },
        "ProfileViewModel" to { koin.get<ProfileViewModel>() },
        "ChatViewModel" to { koin.get<ChatViewModel>() },
        "OrderHistoryVM" to { koin.get<OrderHistoryViewModel>() },
        "AnalyticsTracker" to { koin.get<AnalyticsTracker>() },
        "ProductRepository" to { koin.get<ProductRepository>() }
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

    // ── TEARDOWN: Clean up for next benchmark run ──
    // stopKoin releases all singletons and clears the registry.
    stopKoin()
    return KoinBenchmarkResult(initTime, cold, warm, totalWarm)
}

// Blackhole: prevents LLVM dead code elimination on Kotlin/Native.
@Suppress("UNUSED_PARAMETER")
private fun blackhole(value: Any?) {
    // Intentionally empty — the function call itself prevents DCE
}

private inline fun measureNanos(block: () -> Unit): Long {
    val s = nanoTime()
    block()
    return nanoTime() - s
}
