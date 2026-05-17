package com.codeint.interop.kinject

import com.codeint.interop.hilt.HiltCoreEntryPoint
import com.codeint.interop.kinject.kinject.FeatureComponent
import com.codeint.interop.kinject.kinject.create
import com.codeint.interop.shared.*

/**
 * Hilt + kotlin-inject-anvil interop demo. Uses shared HiltCoreEntryPoint from :benchmark-interop-hilt-metro.
 */
object InteropDemo {

    fun run(hiltEntryPoint: HiltCoreEntryPoint): String {
        val results = StringBuilder()

        val component = FeatureComponent::class.create(
            httpClient = hiltEntryPoint.interopHttpClient(),
            authManager = hiltEntryPoint.interopAuthManager(),
            analyticsTracker = hiltEntryPoint.interopAnalyticsTracker(),
            databaseManager = hiltEntryPoint.interopDatabaseManager(),
            cacheManager = hiltEntryPoint.interopCacheManager(),
            logger = hiltEntryPoint.interopLogger()
        )
        results.appendLine("✓ kotlin-inject component created with 6 Hilt deps (MANUAL)")

        val products = component.productRepository.getProducts()
        results.appendLine("✓ kotlin-inject ProductRepository returned ${products.size} products")

        component.cartRepository.addToCart("p1", 2)
        results.appendLine("✓ kotlin-inject CartRepository added item to cart")

        val orderId = component.orderService.placeOrder(
            listOf(CartItem("c1", "p1", "Headphones", 79.99, 2)), "addr_1"
        )
        results.appendLine("✓ kotlin-inject OrderService placed order: $orderId")

        results.appendLine("")
        results.appendLine("Bridge code: ~12 LINES (6 @get:Provides + 6 constructor args)")

        return results.toString()
    }
}
