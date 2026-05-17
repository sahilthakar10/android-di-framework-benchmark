package com.codeint.interop.shared

/**
 * Shared interfaces and models used by BOTH Hilt and Metro sides.
 * Neither framework owns these — they're plain Kotlin.
 */

// ── Core Infrastructure (provided by Hilt, consumed by Metro) ──

interface HttpClient {
    fun get(url: String): String
    fun post(url: String, body: String): String
}

interface AuthManager {
    fun isLoggedIn(): Boolean
    fun getAccessToken(): String
    fun getUserId(): String
}

interface AnalyticsTracker {
    fun track(event: String, params: Map<String, Any> = emptyMap())
    fun screen(name: String)
}

interface DatabaseManager {
    fun query(table: String, where: String = ""): List<Map<String, Any>>
    fun insert(table: String, values: Map<String, Any>): Long
}

interface CacheManager {
    fun get(key: String): Any?
    fun put(key: String, value: Any, ttlMs: Long = 300_000)
    fun evict(key: String)
}

interface Logger {
    fun debug(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

// ── Feature Services (provided by Metro, consumed by Hilt) ──

interface ProductRepository {
    fun getProducts(page: Int = 0): List<Product>
    fun getProductById(id: String): Product?
    fun search(query: String): List<Product>
}

interface CartRepository {
    fun getCartItems(): List<CartItem>
    fun addToCart(productId: String, quantity: Int)
    fun removeFromCart(itemId: String)
    fun getTotal(): Double
}

interface OrderService {
    fun placeOrder(cartItems: List<CartItem>, addressId: String): String
    fun getOrderHistory(): List<Order>
}

// ── Data Models ──

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String = "",
    val imageUrl: String = "",
    val inStock: Boolean = true
)

data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int
)

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
