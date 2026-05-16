package com.codeint.shopapp.hilt.feature.orders

import com.codeint.shopapp.hilt.domain.order.*
import com.codeint.shopapp.hilt.domain.shipping.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import javax.inject.Inject

class OrderHistoryViewModel @Inject constructor(
    private val getOrderList: GetOrderListUseCase,
    private val getOrderDetail: GetOrderDetailUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadOrders(): List<OrderDomainModel> { analytics.screen("order_history"); return getOrderList.execute().items }
    fun getOrderDetail(id: String): OrderDomainModel? = getOrderDetail.execute(id)
}

class OrderTrackingPresenter @Inject constructor(
    private val getShippingDetail: GetShippingDetailUseCase,
    private val analytics: AnalyticsTracker
) {
    fun trackOrder(orderId: String): TrackingInfo { analytics.track("track_order", mapOf("id" to orderId)); return TrackingInfo("In Transit", "Expected in 2 days", listOf("Shipped", "In Transit")) }
}

class ReturnRequestPresenter @Inject constructor(
    private val getOrderDetail: GetOrderDetailUseCase,
    private val analytics: AnalyticsTracker
) {
    fun requestReturn(orderId: String, reason: String): Boolean { analytics.track("return_request", mapOf("order_id" to orderId)); return true }
}

data class TrackingInfo(val status: String, val eta: String, val steps: List<String>)
