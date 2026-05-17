package com.codeint.interop.metro

import com.codeint.interop.shared.*
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * METRO SIDE — Feature implementations.
 *
 * These consume Hilt-provided infrastructure (HttpClient, AuthManager, etc.)
 * via @Includes on the DependencyGraph. Metro reads the Dagger component's
 * public accessors and makes them available as bindings — ZERO bridge code.
 *
 * This is the key interop demo: Metro features using Hilt infrastructure,
 * and the Hilt side can also access Metro-provided services.
 */

// ── Implementations using Hilt-provided dependencies ──

@SingleIn(AppScope::class)
class MetroProductRepository @Inject constructor(
    private val httpClient: HttpClient,       // ← Provided by Hilt
    private val cacheManager: CacheManager,   // ← Provided by Hilt
    private val logger: Logger                // ← Provided by Hilt
) : ProductRepository {

    override fun getProducts(page: Int): List<Product> {
        val cached = cacheManager.get("products_page_$page") as? List<*>
        if (cached != null) return cached.filterIsInstance<Product>()

        val response = httpClient.get("https://api.shop.com/products?page=$page")
        logger.debug("ProductRepo", "Fetched products page $page")
        val products = listOf(
            Product("p1", "Wireless Headphones", 79.99, "Noise-cancelling BT headphones"),
            Product("p2", "USB-C Hub", 49.99, "7-in-1 multiport adapter"),
            Product("p3", "Mechanical Keyboard", 129.99, "Cherry MX switches"),
            Product("p4", "4K Monitor", 399.99, "27-inch IPS display"),
            Product("p5", "Laptop Stand", 39.99, "Adjustable aluminum stand")
        )
        cacheManager.put("products_page_$page", products)
        return products
    }

    override fun getProductById(id: String): Product? = getProducts().find { it.id == id }
    override fun search(query: String): List<Product> = getProducts().filter { it.name.contains(query, ignoreCase = true) }
}

@SingleIn(AppScope::class)
class MetroCartRepository @Inject constructor(
    private val authManager: AuthManager,         // ← Provided by Hilt
    private val databaseManager: DatabaseManager, // ← Provided by Hilt
    private val logger: Logger                    // ← Provided by Hilt
) : CartRepository {

    override fun getCartItems(): List<CartItem> {
        val userId = authManager.getUserId()
        val rows = databaseManager.query("cart", "user_id = '$userId'")
        return rows.map { row ->
            CartItem(
                id = row["id"].toString(),
                productId = row["product_id"].toString(),
                productName = row["product_name"].toString(),
                price = (row["price"] as? Double) ?: 0.0,
                quantity = (row["quantity"] as? Int) ?: 1
            )
        }
    }

    override fun addToCart(productId: String, quantity: Int) {
        val userId = authManager.getUserId()
        databaseManager.insert("cart", mapOf(
            "id" to "cart_${System.nanoTime()}",
            "user_id" to userId,
            "product_id" to productId,
            "product_name" to "Product $productId",
            "price" to 29.99,
            "quantity" to quantity
        ))
        logger.debug("CartRepo", "Added $productId x$quantity to cart for $userId")
    }

    override fun removeFromCart(itemId: String) {
        logger.debug("CartRepo", "Removed $itemId from cart")
    }

    override fun getTotal(): Double = getCartItems().sumOf { it.price * it.quantity }
}

@SingleIn(AppScope::class)
class MetroOrderService @Inject constructor(
    private val authManager: AuthManager,             // ← Provided by Hilt
    private val httpClient: HttpClient,               // ← Provided by Hilt
    private val analyticsTracker: AnalyticsTracker,   // ← Provided by Hilt
    private val logger: Logger                        // ← Provided by Hilt
) : OrderService {

    override fun placeOrder(cartItems: List<CartItem>, addressId: String): String {
        val userId = authManager.getUserId()
        val token = authManager.getAccessToken()
        val total = cartItems.sumOf { it.price * it.quantity }

        httpClient.post("https://api.shop.com/orders", """{"userId":"$userId","total":$total}""")
        analyticsTracker.track("order_placed", mapOf("total" to total, "items" to cartItems.size))
        logger.debug("OrderService", "Order placed for $userId, total=$total")

        return "order_${System.nanoTime()}"
    }

    override fun getOrderHistory(): List<Order> {
        val response = httpClient.get("https://api.shop.com/orders?user=${authManager.getUserId()}")
        return listOf(
            Order("ord_1", emptyList(), 129.99, "delivered"),
            Order("ord_2", emptyList(), 79.99, "shipped"),
            Order("ord_3", emptyList(), 249.99, "processing")
        )
    }
}

// ── Metro DependencyGraph consuming Hilt's component ──

@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
interface MetroFeatureGraph {

    // Metro-provided feature services (implementations above)
    val productRepository: ProductRepository
    val cartRepository: CartRepository
    val orderService: OrderService

    // Bind interfaces to Metro implementations
    @Provides fun bindProductRepo(impl: MetroProductRepository): ProductRepository = impl
    @Provides fun bindCartRepo(impl: MetroCartRepository): CartRepository = impl
    @Provides fun bindOrderService(impl: MetroOrderService): OrderService = impl

    // Factory: Hilt's entry point is passed via @Includes
    // Metro reads all public accessors from it as available bindings
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Includes hiltCore: com.codeint.interop.hilt.HiltCoreEntryPoint
        ): MetroFeatureGraph
    }
}
