package com.codeint.shopapp.kinject.domain.order

import com.codeint.shopapp.kinject.data.order.OrderRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetOrderListUseCase(private val repo: OrderRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_order_list") }
}
@Inject class GetOrderDetailUseCase(private val repo: OrderRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_order_detail") }
}
@Inject class CreateOrderUseCase(private val repo: OrderRepository, private val logger: AppLogger) { fun execute(m: OrderDomainModel) = repo.create(m) }
@Inject class UpdateOrderUseCase(private val repo: OrderRepository, private val logger: AppLogger) { fun execute(id: String, m: OrderDomainModel) = repo.update(id, m) }
@Inject class DeleteOrderUseCase(private val repo: OrderRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchOrderUseCase(private val repo: OrderRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateOrderUseCase(private val logger: AppLogger) {
    fun execute(m: OrderDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshOrderCacheUseCase(private val repo: OrderRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetOrderCountUseCase(private val repo: OrderRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterOrderUseCase(private val repo: OrderRepository) { fun execute(p: (OrderDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
