package com.codeint.shopapp.hilt.feature.checkout

import com.codeint.shopapp.hilt.domain.order.*
import com.codeint.shopapp.hilt.domain.payment.*
import com.codeint.shopapp.hilt.domain.address.*
import com.codeint.shopapp.hilt.domain.shipping.*
import com.codeint.shopapp.hilt.domain.cart.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.auth.AuthManager
import javax.inject.Inject

class CheckoutViewModel @Inject constructor(
    private val createOrder: CreateOrderUseCase,
    private val getAddressList: GetAddressListUseCase,
    private val getShippingList: GetShippingListUseCase,
    private val getPaymentList: GetPaymentListUseCase,
    private val getCartList: GetCartListUseCase,
    private val analytics: AnalyticsTracker,
    private val authManager: AuthManager
) {
    fun loadCheckout(): CheckoutState {
        analytics.screen("checkout")
        val addresses = getAddressList.execute().items
        val shippingOptions = getShippingList.execute().items
        val paymentMethods = getPaymentList.execute().items
        return CheckoutState(addresses.map { it.name }, shippingOptions.map { it.name }, paymentMethods.map { it.name })
    }
    fun placeOrder(addressId: String, shippingId: String, paymentId: String): String {
        analytics.track("place_order")
        val order = createOrder.execute(OrderDomainModel("", "Order"))
        return order.id
    }
}

class PaymentProcessor @Inject constructor(
    private val getPaymentDetail: GetPaymentDetailUseCase,
    private val analytics: AnalyticsTracker
) {
    fun processPayment(amount: Double, method: String): PaymentResult {
        analytics.track("payment_process", mapOf("amount" to amount, "method" to method))
        return PaymentResult(true, "txn_${System.currentTimeMillis()}", null)
    }
}

class ShippingCalculator @Inject constructor(private val getShippingList: GetShippingListUseCase) {
    fun getShippingOptions(addressId: String): List<ShippingOption> = listOf(
        ShippingOption("standard", "Standard (5-7 days)", 5.99),
        ShippingOption("express", "Express (2-3 days)", 12.99),
        ShippingOption("overnight", "Overnight", 24.99)
    )
}

class OrderValidator @Inject constructor(private val authManager: AuthManager) {
    fun validate(addressId: String, paymentId: String): Boolean = authManager.isLoggedIn() && addressId.isNotBlank() && paymentId.isNotBlank()
}

data class CheckoutState(val addresses: List<String>, val shippingOptions: List<String>, val paymentMethods: List<String>)
data class PaymentResult(val success: Boolean, val transactionId: String?, val error: String?)
data class ShippingOption(val id: String, val label: String, val price: Double)
