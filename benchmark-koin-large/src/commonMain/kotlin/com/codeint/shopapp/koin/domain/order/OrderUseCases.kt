package com.codeint.shopapp.koin.domain.order

import com.codeint.shopapp.koin.data.order.OrderRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetOrderListUseCase(private val repo: OrderRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_order_list") }
}
class GetOrderDetailUseCase(private val repo: OrderRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_order_detail") }
}
class CreateOrderUseCase(private val repo: OrderRepository, private val logger: AppLogger) { fun execute(m: OrderDomainModel) = repo.create(m) }
class UpdateOrderUseCase(private val repo: OrderRepository, private val logger: AppLogger) { fun execute(id: String, m: OrderDomainModel) = repo.update(id, m) }
class DeleteOrderUseCase(private val repo: OrderRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchOrderUseCase(private val repo: OrderRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateOrderUseCase(private val logger: AppLogger) {
    fun execute(m: OrderDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshOrderCacheUseCase(private val repo: OrderRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetOrderCountUseCase(private val repo: OrderRepository) { fun execute() = repo.getAll().totalCount }
class FilterOrderUseCase(private val repo: OrderRepository) { fun execute(p: (OrderDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
