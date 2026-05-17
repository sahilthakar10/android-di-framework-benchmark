package com.codeint.benchmarking

import android.app.Application
import android.os.Debug
import com.codeint.shopapp.hilt.di.BenchmarkEntryPoint
import com.codeint.shopapp.koin.di.*
import com.codeint.shopapp.koin.feature.home.HomeViewModel as KoinHomeViewModel
import com.codeint.shopapp.koin.feature.search.SearchViewModel as KoinSearchViewModel
import com.codeint.shopapp.koin.feature.productdetail.ProductDetailViewModel as KoinProductDetailVM
import com.codeint.shopapp.koin.feature.cart.CartViewModel as KoinCartViewModel
import com.codeint.shopapp.koin.feature.checkout.CheckoutViewModel as KoinCheckoutViewModel
import com.codeint.shopapp.koin.feature.profile.ProfileViewModel as KoinProfileViewModel
import com.codeint.shopapp.koin.feature.chat.ChatViewModel as KoinChatViewModel
import com.codeint.shopapp.koin.feature.orders.OrderHistoryViewModel as KoinOrderHistoryVM
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker as KoinAnalyticsTracker
import com.codeint.shopapp.koin.data.product.ProductRepository as KoinProductRepo

import com.codeint.shopapp.metro.graph.ShopAppGraph
import com.codeint.shopapp.metro.feature.home.HomeViewModel as MetroHomeViewModel
import com.codeint.shopapp.metro.feature.search.SearchViewModel as MetroSearchViewModel
import com.codeint.shopapp.metro.feature.productdetail.ProductDetailViewModel as MetroProductDetailVM
import com.codeint.shopapp.metro.feature.cart.CartViewModel as MetroCartViewModel
import com.codeint.shopapp.metro.feature.checkout.CheckoutViewModel as MetroCheckoutViewModel
import com.codeint.shopapp.metro.feature.profile.ProfileViewModel as MetroProfileViewModel
import com.codeint.shopapp.metro.feature.chat.ChatViewModel as MetroChatViewModel
import com.codeint.shopapp.metro.feature.orders.OrderHistoryViewModel as MetroOrderHistoryVM
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker as MetroAnalyticsTracker
import com.codeint.shopapp.metro.data.product.ProductRepository as MetroProductRepo

import com.codeint.shopapp.metro.graph.GraphFactory
import dagger.hilt.android.EntryPointAccessors
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.GlobalContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.component.get

/**
 * Runtime benchmark comparing Hilt vs Metro vs Koin.
 *
 * Measures:
 *   1. DI Container Initialization time
 *   2. First injection (cold) for each class
 *   3. Repeated injection (warm) - 100 iterations
 *   4. Memory overhead of each DI container
 */
object RuntimeBenchmark {

    data class BenchmarkResult(
        val framework: String,
        val initTimeNanos: Long,
        val firstInjectionNanos: Map<String, Long>,
        val warmInjectionAvgNanos: Map<String, Long>,
        val totalWarmNanos: Long,
        val memoryBeforeBytes: Long,
        val memoryAfterBytes: Long,
        val memoryDeltaBytes: Long
    ) {
        val initTimeMs get() = initTimeNanos / 1_000_000.0
        val memoryDeltaKB get() = memoryDeltaBytes / 1024.0
    }

    data class ComparisonResult(
        val hilt: BenchmarkResult,
        val metro: BenchmarkResult,
        val koin: BenchmarkResult,
        val summary: String
    )

    fun runFullComparison(app: Application, iterations: Int = 100): ComparisonResult {
        // === WARMUP PHASE ===
        // Run all frameworks once to eliminate JVM class loading / JIT bias.
        warmup(app)

        // === ACTUAL MEASUREMENT ===
        System.gc()
        Thread.sleep(300)
        val hiltResult = benchmarkHilt(app, iterations)

        System.gc()
        Thread.sleep(300)
        val metroResult = benchmarkMetro(iterations)

        System.gc()
        Thread.sleep(300)
        val koinResult = benchmarkKoin(iterations)

        val summary = buildSummary(hiltResult, metroResult, koinResult, iterations)
        return ComparisonResult(hiltResult, metroResult, koinResult, summary)
    }

    /**
     * Warmup: resolve all frameworks once so that all classes are loaded
     * by the JVM and JIT-compiled. This ensures the actual benchmark
     * measures only DI overhead, not class loading or JIT compilation.
     */
    private fun warmup(app: Application) {
        // Warm up Hilt — access every entry point method to trigger class loading
        val entryPoint = EntryPointAccessors.fromApplication(app, BenchmarkEntryPoint::class.java)
        entryPoint.httpClient()
        entryPoint.authManager()
        entryPoint.analyticsTracker()
        entryPoint.databaseManager()
        entryPoint.cacheManager()
        entryPoint.appLogger()
        entryPoint.productRepository()
        entryPoint.cartRepository()
        entryPoint.orderRepository()
        entryPoint.userRepository()

        // Warm up Metro
        val graph = GraphFactory.create()
        graph.homeViewModel
        graph.searchViewModel
        graph.productDetailViewModel
        graph.cartViewModel
        graph.checkoutViewModel
        graph.profileViewModel
        graph.chatViewModel
        graph.orderHistoryViewModel
        graph.analyticsTracker
        graph.productRepository

        // Warm up Koin
        try { stopKoin() } catch (_: Exception) {}
        val koinApp = startKoin { modules(allShopAppModules) }
        val koin = koinApp.koin
        koin.get<KoinHomeViewModel>()
        koin.get<KoinSearchViewModel>()
        koin.get<KoinProductDetailVM>()
        koin.get<KoinCartViewModel>()
        koin.get<KoinCheckoutViewModel>()
        koin.get<KoinProfileViewModel>()
        koin.get<KoinChatViewModel>()
        koin.get<KoinOrderHistoryVM>()
        koin.get<KoinAnalyticsTracker>()
        koin.get<KoinProductRepo>()
        stopKoin()
    }

    // =================================================================
    // HILT BENCHMARK
    // =================================================================
    private fun benchmarkHilt(app: Application, iterations: Int): BenchmarkResult {
        System.gc()
        Thread.sleep(100)
        val memBefore = getUsedMemory()

        // 1. Measure EntryPoint access time (Hilt's container is already initialized
        //    by @HiltAndroidApp, so we measure the cost of accessing the component)
        val initStart = System.nanoTime()
        val entryPoint = EntryPointAccessors.fromApplication(app, BenchmarkEntryPoint::class.java)
        val initTime = System.nanoTime() - initStart

        // 2. First injection (cold) — honest names matching actual calls
        val firstInjections = mutableMapOf<String, Long>()

        firstInjections["HttpClient"] = measureNanos { entryPoint.httpClient() }
        firstInjections["AuthManager"] = measureNanos { entryPoint.authManager() }
        firstInjections["AnalyticsTracker"] = measureNanos { entryPoint.analyticsTracker() }
        firstInjections["DatabaseManager"] = measureNanos { entryPoint.databaseManager() }
        firstInjections["CacheManager"] = measureNanos { entryPoint.cacheManager() }
        firstInjections["AppLogger"] = measureNanos { entryPoint.appLogger() }
        firstInjections["ProductRepository"] = measureNanos { entryPoint.productRepository() }
        firstInjections["CartRepository"] = measureNanos { entryPoint.cartRepository() }
        firstInjections["OrderRepository"] = measureNanos { entryPoint.orderRepository() }
        firstInjections["UserRepository"] = measureNanos { entryPoint.userRepository() }

        // 3. Warm injection - repeated resolution
        val warmInjections = mutableMapOf<String, Long>()
        var totalWarm = 0L

        val targets = listOf<Pair<String, () -> Any>>(
            "HttpClient" to { entryPoint.httpClient() },
            "AuthManager" to { entryPoint.authManager() },
            "AnalyticsTracker" to { entryPoint.analyticsTracker() },
            "DatabaseManager" to { entryPoint.databaseManager() },
            "CacheManager" to { entryPoint.cacheManager() },
            "AppLogger" to { entryPoint.appLogger() },
            "ProductRepository" to { entryPoint.productRepository() },
            "CartRepository" to { entryPoint.cartRepository() },
            "OrderRepository" to { entryPoint.orderRepository() },
            "UserRepository" to { entryPoint.userRepository() }
        )

        for ((name, resolver) in targets) {
            var sum = 0L
            repeat(iterations) {
                val start = System.nanoTime()
                resolver()
                sum += System.nanoTime() - start
            }
            warmInjections[name] = sum / iterations
            totalWarm += sum
        }

        val memAfter = getUsedMemory()

        return BenchmarkResult(
            framework = "Hilt",
            initTimeNanos = initTime,
            firstInjectionNanos = firstInjections,
            warmInjectionAvgNanos = warmInjections,
            totalWarmNanos = totalWarm,
            memoryBeforeBytes = memBefore,
            memoryAfterBytes = memAfter,
            memoryDeltaBytes = memAfter - memBefore
        )
    }

    // =================================================================
    // METRO BENCHMARK
    // =================================================================
    private fun benchmarkMetro(iterations: Int): BenchmarkResult {
        System.gc()
        Thread.sleep(100)
        val memBefore = getUsedMemory()

        // 1. Measure graph initialization
        val initStart = System.nanoTime()
        val graph: ShopAppGraph = GraphFactory.create()
        val initTime = System.nanoTime() - initStart

        // 2. First injection (cold)
        val firstInjections = mutableMapOf<String, Long>()

        firstInjections["HomeViewModel"] = measureNanos { graph.homeViewModel }
        firstInjections["SearchViewModel"] = measureNanos { graph.searchViewModel }
        firstInjections["ProductDetailVM"] = measureNanos { graph.productDetailViewModel }
        firstInjections["CartViewModel"] = measureNanos { graph.cartViewModel }
        firstInjections["CheckoutViewModel"] = measureNanos { graph.checkoutViewModel }
        firstInjections["ProfileViewModel"] = measureNanos { graph.profileViewModel }
        firstInjections["ChatViewModel"] = measureNanos { graph.chatViewModel }
        firstInjections["OrderHistoryVM"] = measureNanos { graph.orderHistoryViewModel }
        firstInjections["AnalyticsTracker"] = measureNanos { graph.analyticsTracker }
        firstInjections["ProductRepository"] = measureNanos { graph.productRepository }

        // 3. Warm injection - repeated resolution
        val warmInjections = mutableMapOf<String, Long>()
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
            repeat(iterations) {
                val start = System.nanoTime()
                resolver()
                sum += System.nanoTime() - start
            }
            warmInjections[name] = sum / iterations
            totalWarm += sum
        }

        val memAfter = getUsedMemory()

        return BenchmarkResult(
            framework = "Metro",
            initTimeNanos = initTime,
            firstInjectionNanos = firstInjections,
            warmInjectionAvgNanos = warmInjections,
            totalWarmNanos = totalWarm,
            memoryBeforeBytes = memBefore,
            memoryAfterBytes = memAfter,
            memoryDeltaBytes = memAfter - memBefore
        )
    }

    // =================================================================
    // KOIN BENCHMARK
    // =================================================================
    private fun benchmarkKoin(iterations: Int): BenchmarkResult {
        // Stop any existing Koin instance
        try { stopKoin() } catch (_: Exception) {}

        System.gc()
        Thread.sleep(100)
        val memBefore = getUsedMemory()

        // 1. Measure Koin module loading (startKoin)
        val initStart = System.nanoTime()
        val koinApp = startKoin {
            modules(allShopAppModules)
        }
        val initTime = System.nanoTime() - initStart
        val koin = koinApp.koin

        // 2. First injection (cold)
        val firstInjections = mutableMapOf<String, Long>()

        firstInjections["HomeViewModel"] = measureNanos { koin.get<KoinHomeViewModel>() }
        firstInjections["SearchViewModel"] = measureNanos { koin.get<KoinSearchViewModel>() }
        firstInjections["ProductDetailVM"] = measureNanos { koin.get<KoinProductDetailVM>() }
        firstInjections["CartViewModel"] = measureNanos { koin.get<KoinCartViewModel>() }
        firstInjections["CheckoutViewModel"] = measureNanos { koin.get<KoinCheckoutViewModel>() }
        firstInjections["ProfileViewModel"] = measureNanos { koin.get<KoinProfileViewModel>() }
        firstInjections["ChatViewModel"] = measureNanos { koin.get<KoinChatViewModel>() }
        firstInjections["OrderHistoryVM"] = measureNanos { koin.get<KoinOrderHistoryVM>() }
        firstInjections["AnalyticsTracker"] = measureNanos { koin.get<KoinAnalyticsTracker>() }
        firstInjections["ProductRepository"] = measureNanos { koin.get<KoinProductRepo>() }

        // 3. Warm injection - repeated resolution
        val warmInjections = mutableMapOf<String, Long>()
        var totalWarm = 0L

        val targets = listOf<Pair<String, () -> Any>>(
            "HomeViewModel" to { koin.get<KoinHomeViewModel>() },
            "SearchViewModel" to { koin.get<KoinSearchViewModel>() },
            "ProductDetailVM" to { koin.get<KoinProductDetailVM>() },
            "CartViewModel" to { koin.get<KoinCartViewModel>() },
            "CheckoutViewModel" to { koin.get<KoinCheckoutViewModel>() },
            "ProfileViewModel" to { koin.get<KoinProfileViewModel>() },
            "ChatViewModel" to { koin.get<KoinChatViewModel>() },
            "OrderHistoryVM" to { koin.get<KoinOrderHistoryVM>() },
            "AnalyticsTracker" to { koin.get<KoinAnalyticsTracker>() },
            "ProductRepository" to { koin.get<KoinProductRepo>() }
        )

        for ((name, resolver) in targets) {
            var sum = 0L
            repeat(iterations) {
                val start = System.nanoTime()
                resolver()
                sum += System.nanoTime() - start
            }
            warmInjections[name] = sum / iterations
            totalWarm += sum
        }

        val memAfter = getUsedMemory()

        stopKoin()

        return BenchmarkResult(
            framework = "Koin",
            initTimeNanos = initTime,
            firstInjectionNanos = firstInjections,
            warmInjectionAvgNanos = warmInjections,
            totalWarmNanos = totalWarm,
            memoryBeforeBytes = memBefore,
            memoryAfterBytes = memAfter,
            memoryDeltaBytes = memAfter - memBefore
        )
    }

    // =================================================================
    // HELPERS
    // =================================================================
    private inline fun measureNanos(block: () -> Any): Long {
        val start = System.nanoTime()
        block()
        return System.nanoTime() - start
    }

    private fun getUsedMemory(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() - runtime.freeMemory() + Debug.getNativeHeapAllocatedSize()
    }

    private fun buildSummary(hilt: BenchmarkResult, metro: BenchmarkResult, koin: BenchmarkResult, iterations: Int): String {
        val sb = StringBuilder()
        sb.appendLine("════════════════════════════════════════════════════════")
        sb.appendLine("  RUNTIME BENCHMARK: Hilt vs Metro vs Koin")
        sb.appendLine("  E-Commerce App: ~350 classes, ~285 bindings")
        sb.appendLine("  Warm iterations: $iterations per class")
        sb.appendLine("════════════════════════════════════════════════════════")
        sb.appendLine()

        // Init time
        sb.appendLine("┌─ DI Container Initialization ──────────────────────┐")
        sb.appendLine("│  Hilt  (EntryPoint):   ${"%.2f".format(hilt.initTimeMs)}ms")
        sb.appendLine("│  Metro (createGraph):  ${"%.2f".format(metro.initTimeMs)}ms")
        sb.appendLine("│  Koin  (startKoin):    ${"%.2f".format(koin.initTimeMs)}ms")
        val initTimes = listOf("Hilt" to hilt.initTimeNanos, "Metro" to metro.initTimeNanos, "Koin" to koin.initTimeNanos)
        val initWinner = initTimes.minBy { it.second }
        val initSlowest = initTimes.maxBy { it.second }
        val initPct = "%.1f".format((1 - initWinner.second.toDouble() / initSlowest.second) * 100)
        sb.appendLine("│  Winner: ${initWinner.first} ($initPct% faster than ${initSlowest.first})")
        sb.appendLine("└────────────────────────────────────────────────────┘")
        sb.appendLine()

        // First injection
        sb.appendLine("┌─ First Injection (Cold) ───────────────────────────┐")
        sb.appendLine("│  Class                  Hilt         Metro        Koin")
        sb.appendLine("│  ─────────────────────  ──────────   ──────────   ──────────")
        for (key in hilt.firstInjectionNanos.keys) {
            val h = hilt.firstInjectionNanos[key] ?: 0
            val m = metro.firstInjectionNanos[key] ?: 0
            val k = koin.firstInjectionNanos[key] ?: 0
            val min = minOf(h, m, k)
            val markerH = if (h == min) " ◀" else ""
            val markerM = if (m == min) " ◀" else ""
            val markerK = if (k == min) " ◀" else ""
            sb.appendLine("│  %-22s %8sus%s  %8sus%s  %8sus%s".format(key, h/1000, markerH, m/1000, markerM, k/1000, markerK))
        }
        sb.appendLine("└────────────────────────────────────────────────────┘")
        sb.appendLine()

        // Warm injection
        sb.appendLine("┌─ Warm Injection (Avg of $iterations) ─────────────────────┐")
        sb.appendLine("│  Class                  Hilt         Metro        Koin")
        sb.appendLine("│  ─────────────────────  ──────────   ──────────   ──────────")
        for (key in hilt.warmInjectionAvgNanos.keys) {
            val h = hilt.warmInjectionAvgNanos[key] ?: 0
            val m = metro.warmInjectionAvgNanos[key] ?: 0
            val k = koin.warmInjectionAvgNanos[key] ?: 0
            val min = minOf(h, m, k)
            val markerH = if (h == min) " ◀" else ""
            val markerM = if (m == min) " ◀" else ""
            val markerK = if (k == min) " ◀" else ""
            sb.appendLine("│  %-22s %8sus%s  %8sus%s  %8sus%s".format(key, h/1000, markerH, m/1000, markerM, k/1000, markerK))
        }
        val totalHiltAvg = hilt.totalWarmNanos / (iterations * 10)
        val totalMetroAvg = metro.totalWarmNanos / (iterations * 10)
        val totalKoinAvg = koin.totalWarmNanos / (iterations * 10)
        sb.appendLine("│  ─────────────────────  ──────────   ──────────   ──────────")
        sb.appendLine("│  TOTAL avg/injection   %8sus    %8sus    %8sus".format(totalHiltAvg/1000, totalMetroAvg/1000, totalKoinAvg/1000))
        sb.appendLine("└────────────────────────────────────────────────────┘")
        sb.appendLine()

        // Memory
        sb.appendLine("┌─ Memory Overhead ──────────────────────────────────┐")
        sb.appendLine("│  Hilt:  ${"%.1f".format(hilt.memoryDeltaKB)}KB")
        sb.appendLine("│  Metro: ${"%.1f".format(metro.memoryDeltaKB)}KB")
        sb.appendLine("│  Koin:  ${"%.1f".format(koin.memoryDeltaKB)}KB")
        sb.appendLine("└────────────────────────────────────────────────────┘")
        sb.appendLine()

        // Overall
        val warmTimes = listOf("Hilt" to hilt.totalWarmNanos, "Metro" to metro.totalWarmNanos, "Koin" to koin.totalWarmNanos)
        val warmWinner = warmTimes.minBy { it.second }
        val warmSlowest = warmTimes.maxBy { it.second }
        val warmPct = "%.1f".format((1 - warmWinner.second.toDouble() / warmSlowest.second) * 100)
        sb.appendLine("═══ VERDICT ═══")
        sb.appendLine("  Init: ${initWinner.first} is $initPct% faster than ${initSlowest.first}")
        sb.appendLine("  Runtime: ${warmWinner.first} is $warmPct% faster than ${warmSlowest.first} on repeated injection")
        sb.appendLine("════════════════════════════════════════════════════════")

        return sb.toString()
    }
}
