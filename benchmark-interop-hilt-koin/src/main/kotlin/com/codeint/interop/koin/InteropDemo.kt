package com.codeint.interop.koin

import com.codeint.interop.hilt.HiltCoreEntryPoint
import com.codeint.interop.koin.koin.*
import com.codeint.interop.shared.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * Hilt + Koin interop demo. Uses shared HiltCoreEntryPoint from :benchmark-interop-hilt-metro.
 */
object InteropDemo {

    fun run(hiltEntryPoint: HiltCoreEntryPoint): String {
        val results = StringBuilder()

        val bridgeModule = createBridgeModule(
            httpClient = hiltEntryPoint.interopHttpClient(),
            authManager = hiltEntryPoint.interopAuthManager(),
            analyticsTracker = hiltEntryPoint.interopAnalyticsTracker(),
            databaseManager = hiltEntryPoint.interopDatabaseManager(),
            cacheManager = hiltEntryPoint.interopCacheManager(),
            logger = hiltEntryPoint.interopLogger()
        )
        results.appendLine("✓ Created Koin bridge module with 6 Hilt dependencies (MANUAL)")

        try { stopKoin() } catch (_: Exception) {}
        val koinApp = startKoin { modules(bridgeModule, koinFeatureModule) }
        val koin = koinApp.koin
        results.appendLine("✓ Koin started with bridge + feature modules")

        val products = koin.get<ProductRepository>().getProducts()
        results.appendLine("✓ Koin ProductRepository returned ${products.size} products")

        koin.get<CartRepository>().addToCart("p1", 2)
        results.appendLine("✓ Koin CartRepository added item to cart")

        val orderId = koin.get<OrderService>().placeOrder(
            listOf(CartItem("c1", "p1", "Headphones", 79.99, 2)), "addr_1"
        )
        results.appendLine("✓ Koin OrderService placed order: $orderId")

        stopKoin()

        results.appendLine("")
        results.appendLine("Bridge code: ~20 LINES (createBridgeModule + @EntryPoint)")

        return results.toString()
    }
}
