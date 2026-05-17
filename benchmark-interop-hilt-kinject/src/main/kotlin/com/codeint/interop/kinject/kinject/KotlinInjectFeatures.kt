package com.codeint.interop.kinject.kinject

import com.codeint.interop.shared.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides

/**
 * kotlin-inject-anvil feature implementations that consume Hilt-managed infrastructure.
 *
 * Each class depends only on interfaces and doesn't know
 * the implementations come from Hilt.
 */

@Inject class KInjectProductRepository(
    private val httpClient: HttpClient,
    private val cacheManager: CacheManager,
    private val logger: Logger
) : ProductRepository {

    override fun getProducts(page: Int): List<Product> {
        httpClient.get("https://api.shop.com/products?page=$page")
        logger.debug("ProductRepo", "Fetched page $page")
        return listOf(
            Product("p1", "Wireless Headphones", 79.99),
            Product("p2", "USB-C Hub", 49.99),
            Product("p3", "Mechanical Keyboard", 129.99)
        )
    }

    override fun getProductById(id: String) = getProducts().find { it.id == id }

    override fun search(query: String) =
        getProducts().filter { it.name.contains(query, ignoreCase = true) }
}

@Inject class KInjectCartRepository(
    private val authManager: AuthManager,
    private val databaseManager: DatabaseManager,
    private val logger: Logger
) : CartRepository {

    override fun getCartItems(): List<CartItem> = emptyList()

    override fun addToCart(productId: String, quantity: Int) {
        databaseManager.insert("cart", mapOf("product_id" to productId, "quantity" to quantity))
        logger.debug("CartRepo", "Added $productId x$quantity for ${authManager.getUserId()}")
    }

    override fun removeFromCart(itemId: String) {}

    override fun getTotal() = getCartItems().sumOf { it.price * it.quantity }
}

@Inject class KInjectOrderService(
    private val authManager: AuthManager,
    private val httpClient: HttpClient,
    private val analyticsTracker: AnalyticsTracker,
    private val logger: Logger
) : OrderService {

    override fun placeOrder(cartItems: List<CartItem>, addressId: String): String {
        httpClient.post("https://api.shop.com/orders", """{"user":"${authManager.getUserId()}"}""")
        analyticsTracker.track("order_placed", mapOf("items" to cartItems.size))
        return "order_${System.nanoTime()}"
    }

    override fun getOrderHistory() =
        listOf(Order("ord_1", emptyList(), 129.99, "delivered"))
}

/**
 * Component bridging Hilt singletons into kotlin-inject's graph.
 *
 * Each @get:Provides constructor parameter exposes a Hilt-managed instance.
 * kotlin-inject validates the full graph at compile time.
 */
@Component
abstract class FeatureComponent(
    @get:Provides val httpClient: HttpClient,
    @get:Provides val authManager: AuthManager,
    @get:Provides val analyticsTracker: AnalyticsTracker,
    @get:Provides val databaseManager: DatabaseManager,
    @get:Provides val cacheManager: CacheManager,
    @get:Provides val logger: Logger
) {
    abstract val productRepository: KInjectProductRepository
    abstract val cartRepository: KInjectCartRepository
    abstract val orderService: KInjectOrderService
}
