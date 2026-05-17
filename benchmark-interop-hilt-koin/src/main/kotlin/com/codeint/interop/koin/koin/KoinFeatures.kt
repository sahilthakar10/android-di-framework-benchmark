package com.codeint.interop.koin.koin

import com.codeint.interop.shared.*
import org.koin.dsl.module

/**
 * Koin feature implementations that consume Hilt-managed infrastructure.
 *
 * Each class depends only on interfaces (HttpClient, AuthManager, etc.)
 * and doesn't know the implementations come from Hilt.
 */

class KoinProductRepository(
    private val httpClient: HttpClient,
    private val cacheManager: CacheManager,
    private val logger: Logger
) : ProductRepository {

    override fun getProducts(page: Int): List<Product> {
        httpClient.get("https://api.shop.com/products?page=$page")
        logger.debug("ProductRepo", "Fetched products page $page")
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

class KoinCartRepository(
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

class KoinOrderService(
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

// Koin module registering feature implementations
val koinFeatureModule = module {
    single<ProductRepository> { KoinProductRepository(get(), get(), get()) }
    single<CartRepository> { KoinCartRepository(get(), get(), get()) }
    single<OrderService> { KoinOrderService(get(), get(), get(), get()) }
}

/**
 * Bridge module — passes Hilt-managed singletons into Koin's registry.
 *
 * Koin cannot read Hilt's Dagger component directly. Each dependency
 * must be registered manually. Compare with Metro's @Includes which
 * does this automatically with zero bridge code.
 */
fun createBridgeModule(
    httpClient: HttpClient,
    authManager: AuthManager,
    analyticsTracker: AnalyticsTracker,
    databaseManager: DatabaseManager,
    cacheManager: CacheManager,
    logger: Logger
) = module {
    single<HttpClient> { httpClient }
    single<AuthManager> { authManager }
    single<AnalyticsTracker> { analyticsTracker }
    single<DatabaseManager> { databaseManager }
    single<CacheManager> { cacheManager }
    single<Logger> { logger }
}
