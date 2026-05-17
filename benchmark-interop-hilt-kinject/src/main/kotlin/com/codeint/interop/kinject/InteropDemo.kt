package com.codeint.interop.kinject

import com.codeint.interop.hilt.HiltCoreEntryPoint
import com.codeint.interop.kinject.kinject.FeatureComponent
import com.codeint.interop.kinject.kinject.create
import com.codeint.interop.shared.*

/**
 * Hilt + kotlin-inject-anvil coexistence example.
 *
 * Hilt owns core infrastructure (network, auth, analytics, DB, cache, logging).
 * kotlin-inject-anvil owns new feature modules (product, cart, order).
 *
 * Bridge: @get:Provides constructor params in FeatureComponent (~12 lines).
 * Compile-time validated — if a dependency is missing, the build fails.
 */
object HiltKotlinInjectInteropExample {

    fun run(hiltEntryPoint: HiltCoreEntryPoint) {
        val component = FeatureComponent::class.create(
            httpClient = hiltEntryPoint.interopHttpClient(),
            authManager = hiltEntryPoint.interopAuthManager(),
            analyticsTracker = hiltEntryPoint.interopAnalyticsTracker(),
            databaseManager = hiltEntryPoint.interopDatabaseManager(),
            cacheManager = hiltEntryPoint.interopCacheManager(),
            logger = hiltEntryPoint.interopLogger()
        )

        // kotlin-inject features consume Hilt infra transparently
        val products = component.productRepository.getProducts()
        component.cartRepository.addToCart("p1", 2)
        component.orderService.placeOrder(
            listOf(CartItem("c1", "p1", "Headphones", 79.99, 2)), "addr_1"
        )
    }
}
