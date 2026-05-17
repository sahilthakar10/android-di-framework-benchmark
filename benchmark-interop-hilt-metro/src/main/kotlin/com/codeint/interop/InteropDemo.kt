package com.codeint.interop

import com.codeint.interop.hilt.HiltCoreEntryPoint
import com.codeint.interop.metro.MetroFeatureGraph
import com.codeint.interop.shared.*
import dev.zacsweers.metro.createGraphFactory

/**
 * Hilt + Metro coexistence example.
 *
 * Hilt owns core infrastructure (network, auth, analytics, DB, cache, logging).
 * Metro owns new feature modules (product, cart, order).
 *
 * Bridge: @Includes in MetroFeatureGraph.Factory — zero manual wiring.
 * Metro reads HiltCoreEntryPoint accessors directly at compile time.
 */
object HiltMetroInteropExample {

    fun run(hiltEntryPoint: HiltCoreEntryPoint) {
        // @Includes auto-reads all Hilt dependencies — no bridge code needed
        val graph = createGraphFactory<MetroFeatureGraph.Factory>().create(hiltEntryPoint)

        // Metro features consume Hilt infra transparently
        val products = graph.productRepository.getProducts()
        graph.cartRepository.addToCart("p1", 2)
        graph.orderService.placeOrder(
            listOf(CartItem("c1", "p1", "Headphones", 79.99, 2)), "addr_1"
        )
    }
}
