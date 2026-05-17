package com.codeint.interop.metro

import com.codeint.interop.shared.*
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Metro feature implementations that consume Hilt-managed infrastructure.
 *
 * Each class depends only on interfaces (HttpClient, AuthManager, etc.)
 * and doesn't know the implementations come from Hilt.
 */

@SingleIn(AppScope::class)
class MetroProductRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val cacheManager: CacheManager,
    private val logger: Logger
) : ProductRepository {

    override fun getProducts(page: Int): List<Product> {
        val cached = cacheManager.get("products_page_$page") as? List<*>
        if (cached != null) return cached.filterIsInstance<Product>()

        httpClient.get("https://api.shop.com/products?page=$page")
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

    override fun getProductById(id: String) = getProducts().find { it.id == id }

    override fun search(query: String) =
        getProducts().filter { it.name.contains(query, ignoreCase = true) }
}

@SingleIn(AppScope::class)
class MetroCartRepository @Inject constructor(
    private val authManager: AuthManager,
    private val databaseManager: DatabaseManager,
    private val logger: Logger
) : CartRepository {

    override fun getCartItems(): List<CartItem> {
        val rows = databaseManager.query("cart", "user_id = '${authManager.getUserId()}'")
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
        databaseManager.insert("cart", mapOf(
            "user_id" to authManager.getUserId(),
            "product_id" to productId,
            "quantity" to quantity
        ))
        logger.debug("CartRepo", "Added $productId x$quantity for ${authManager.getUserId()}")
    }

    override fun removeFromCart(itemId: String) {}

    override fun getTotal() = getCartItems().sumOf { it.price * it.quantity }
}

@SingleIn(AppScope::class)
class MetroOrderService @Inject constructor(
    private val authManager: AuthManager,
    private val httpClient: HttpClient,
    private val analyticsTracker: AnalyticsTracker,
    private val logger: Logger
) : OrderService {

    override fun placeOrder(cartItems: List<CartItem>, addressId: String): String {
        val total = cartItems.sumOf { it.price * it.quantity }
        httpClient.post("https://api.shop.com/orders", """{"userId":"${authManager.getUserId()}","total":$total}""")
        analyticsTracker.track("order_placed", mapOf("total" to total, "items" to cartItems.size))
        return "order_${System.nanoTime()}"
    }

    override fun getOrderHistory() = listOf(
        Order("ord_1", emptyList(), 129.99, "delivered"),
        Order("ord_2", emptyList(), 79.99, "shipped")
    )
}

/**
 * Metro graph that consumes Hilt's Dagger component via @Includes.
 *
 * The Factory takes HiltCoreEntryPoint — Metro automatically reads
 * all its public accessors (httpClient, authManager, etc.) and makes
 * them available as bindings. Zero manual bridge code.
 */
@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
interface MetroFeatureGraph {

    val productRepository: ProductRepository
    val cartRepository: CartRepository
    val orderService: OrderService

    @Provides fun bindProductRepo(impl: MetroProductRepository): ProductRepository = impl
    @Provides fun bindCartRepo(impl: MetroCartRepository): CartRepository = impl
    @Provides fun bindOrderService(impl: MetroOrderService): OrderService = impl

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Includes hiltCore: com.codeint.interop.hilt.HiltCoreEntryPoint): MetroFeatureGraph
    }
}
