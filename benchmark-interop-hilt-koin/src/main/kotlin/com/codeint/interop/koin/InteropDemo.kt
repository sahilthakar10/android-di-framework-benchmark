package com.codeint.interop.koin

import com.codeint.interop.hilt.HiltCoreEntryPoint
import com.codeint.interop.koin.koin.*
import com.codeint.interop.shared.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * Hilt + Koin coexistence example.
 *
 * Hilt owns core infrastructure (network, auth, analytics, DB, cache, logging).
 * Koin owns new feature modules (product, cart, order).
 *
 * Bridge: createBridgeModule() manually passes 6 Hilt singletons into Koin (~20 lines).
 * Compare with Metro interop which uses @Includes for zero bridge code.
 */
object HiltKoinInteropExample {

    fun run(hiltEntryPoint: HiltCoreEntryPoint) {
        val bridgeModule = createBridgeModule(
            httpClient = hiltEntryPoint.interopHttpClient(),
            authManager = hiltEntryPoint.interopAuthManager(),
            analyticsTracker = hiltEntryPoint.interopAnalyticsTracker(),
            databaseManager = hiltEntryPoint.interopDatabaseManager(),
            cacheManager = hiltEntryPoint.interopCacheManager(),
            logger = hiltEntryPoint.interopLogger()
        )

        try { stopKoin() } catch (_: Exception) {}
        val koin = startKoin { modules(bridgeModule, koinFeatureModule) }.koin

        // Koin features consume Hilt infra transparently
        val products = koin.get<ProductRepository>().getProducts()
        koin.get<CartRepository>().addToCart("p1", 2)
        koin.get<OrderService>().placeOrder(
            listOf(CartItem("c1", "p1", "Headphones", 79.99, 2)), "addr_1"
        )

        stopKoin()
    }
}
