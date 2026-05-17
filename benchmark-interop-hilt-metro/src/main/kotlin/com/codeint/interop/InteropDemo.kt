package com.codeint.interop

import com.codeint.interop.hilt.HiltCoreEntryPoint
import com.codeint.interop.metro.MetroFeatureGraph
import com.codeint.interop.shared.*
import dev.zacsweers.metro.createGraphFactory

/**
 * INTEROP DEMO — Hilt provides 50% (infrastructure), Metro provides 50% (features).
 *
 * Architecture:
 *   ┌──────────────────────────────────────────────────────┐
 *   │ HILT SIDE (existing infrastructure)                  │
 *   │  HttpClient, AuthManager, AnalyticsTracker,          │
 *   │  DatabaseManager, CacheManager, Logger               │
 *   │  → All @Singleton, managed by Hilt lifecycle         │
 *   ├──────────────────────────────────────────────────────┤
 *   │              @Includes (zero bridge code)            │
 *   ├──────────────────────────────────────────────────────┤
 *   │ METRO SIDE (new KMP-ready features)                  │
 *   │  ProductRepository, CartRepository, OrderService     │
 *   │  → Uses Hilt singletons via @Includes                │
 *   │  → @SingleIn(AppScope), compile-time validated       │
 *   └──────────────────────────────────────────────────────┘
 *
 * The key point: Metro's MetroFeatureGraph.Factory takes HiltCoreEntryPoint
 * via @Includes — Metro automatically reads all 6 accessors (httpClient(),
 * authManager(), etc.) and makes them available as bindings. No manual
 * wiring, no bridge code, no duplication.
 */
object InteropDemo {

    /**
     * Demonstrates the full interop flow.
     * In a real app, hiltEntryPoint comes from:
     *   EntryPointAccessors.fromApplication(context, HiltCoreEntryPoint::class.java)
     */
    fun run(hiltEntryPoint: HiltCoreEntryPoint): String {
        val results = StringBuilder()

        // ── Step 1: Create Metro graph with Hilt dependencies ──
        val metroGraph = createGraphFactory<MetroFeatureGraph.Factory>().create(hiltEntryPoint)
        results.appendLine("✓ Metro graph created with Hilt dependencies via @Includes")

        // ── Step 2: Metro features using Hilt infrastructure ──

        // ProductRepository (Metro) uses HttpClient + CacheManager + Logger (all Hilt)
        val products = metroGraph.productRepository.getProducts()
        results.appendLine("✓ Metro ProductRepository returned ${products.size} products")
        results.appendLine("  Using Hilt's HttpClient, CacheManager, Logger")

        // CartRepository (Metro) uses AuthManager + DatabaseManager + Logger (all Hilt)
        metroGraph.cartRepository.addToCart("p1", 2)
        results.appendLine("✓ Metro CartRepository added item to cart")
        results.appendLine("  Using Hilt's AuthManager (userId: ${hiltEntryPoint.interopAuthManager().getUserId()})")

        // OrderService (Metro) uses AuthManager + HttpClient + AnalyticsTracker + Logger (all Hilt)
        val orderId = metroGraph.orderService.placeOrder(
            listOf(CartItem("c1", "p1", "Wireless Headphones", 79.99, 2)),
            "addr_1"
        )
        results.appendLine("✓ Metro OrderService placed order: $orderId")
        results.appendLine("  Using Hilt's AuthManager, HttpClient, AnalyticsTracker, Logger")

        // ── Step 3: Hilt side can also access Metro services ──
        // (In a real app, Hilt would depend on MetroFeatureGraph via @Component(dependencies))
        val orderHistory = metroGraph.orderService.getOrderHistory()
        results.appendLine("✓ Order history (from Metro): ${orderHistory.size} orders")

        results.appendLine("")
        results.appendLine("INTEROP SUMMARY:")
        results.appendLine("  Hilt provides: HttpClient, AuthManager, AnalyticsTracker, DatabaseManager, CacheManager, Logger")
        results.appendLine("  Metro provides: ProductRepository, CartRepository, OrderService")
        results.appendLine("  Bridge code written: ZERO lines")
        results.appendLine("  Both frameworks share the same dependency graph via @Includes")

        return results.toString()
    }
}
