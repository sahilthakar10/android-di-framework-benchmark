package com.codeint.shopapp.koin.feature.cart

import com.codeint.shopapp.koin.domain.cart.*
import com.codeint.shopapp.koin.domain.product.*
import com.codeint.shopapp.koin.domain.promotion.*
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker


class CartViewModel constructor(
    private val getCartList: GetCartListUseCase,
    private val updateCart: UpdateCartUseCase,
    private val deleteCart: DeleteCartUseCase,
    private val getProductDetail: GetProductDetailUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadCart(): CartScreenState {
        analytics.screen("cart")
        val items = getCartList.execute()
        return CartScreenState(items.items, 0.0, items.totalCount)
    }
    fun updateQuantity(cartItemId: String, quantity: Int) { updateCart.execute(cartItemId, CartDomainModel(cartItemId, "")) }
    fun removeItem(cartItemId: String) { deleteCart.execute(cartItemId); analytics.track("remove_from_cart", mapOf("id" to cartItemId)) }
}

class CartCalculator constructor(private val getPromotionDetail: GetPromotionDetailUseCase) {
    fun calculateSubtotal(items: List<CartItemDisplay>): Double = items.sumOf { it.price * it.quantity }
    fun calculateTax(subtotal: Double): Double = subtotal * 0.08
    fun calculateShipping(subtotal: Double): Double = if (subtotal > 50.0) 0.0 else 5.99
    fun calculateTotal(items: List<CartItemDisplay>): Double { val sub = calculateSubtotal(items); return sub + calculateTax(sub) + calculateShipping(sub) }
}

class CouponValidator constructor(
    private val getPromotionDetail: GetPromotionDetailUseCase,
    private val analytics: AnalyticsTracker
) {
    fun validate(code: String): CouponResult {
        analytics.track("coupon_attempt", mapOf("code" to code))
        return CouponResult(true, 10.0, "10% off!")
    }
}

class CartBadgeManager constructor(private val getCartCount: GetCartCountUseCase) {
    fun getBadgeCount(): Int = getCartCount.execute()
}

data class CartScreenState(val items: List<CartDomainModel>, val total: Double, val itemCount: Int)
data class CartItemDisplay(val id: String, val name: String, val price: Double, val quantity: Int, val imageUrl: String)
data class CouponResult(val isValid: Boolean, val discount: Double, val message: String)
