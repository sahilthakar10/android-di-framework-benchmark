package com.codeint.shopapp.hilt.domain.order

import com.codeint.shopapp.hilt.data.order.OrderRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetOrderListUseCase @Inject constructor(
    private val repository: OrderRepository, private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<OrderDomainModel> {
        analytics.track("get_order_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetOrderDetailUseCase @Inject constructor(
    private val repository: OrderRepository, private val analytics: AnalyticsTracker
) {
    fun execute(id: String): OrderDomainModel? {
        analytics.track("get_order_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateOrderUseCase @Inject constructor(private val repository: OrderRepository, private val logger: AppLogger) {
    fun execute(model: OrderDomainModel) = repository.create(model)
}

class UpdateOrderUseCase @Inject constructor(private val repository: OrderRepository, private val logger: AppLogger) {
    fun execute(id: String, model: OrderDomainModel) = repository.update(id, model)
}

class DeleteOrderUseCase @Inject constructor(private val repository: OrderRepository, private val logger: AppLogger) {
    fun execute(id: String) = repository.delete(id)
}

class SearchOrderUseCase @Inject constructor(private val repository: OrderRepository, private val analytics: AnalyticsTracker) {
    fun execute(query: String, page: Int = 0) = repository.search(query, page)
}

class ValidateOrderUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: OrderDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshOrderCacheUseCase @Inject constructor(private val repository: OrderRepository, private val logger: AppLogger) {
    fun execute() { repository.clearCache(); repository.getAll() }
}

class GetOrderCountUseCase @Inject constructor(private val repository: OrderRepository) {
    fun execute() = repository.getAll().totalCount
}

class FilterOrderUseCase @Inject constructor(private val repository: OrderRepository) {
    fun execute(predicate: (OrderDomainModel) -> Boolean) = repository.getAll().items.filter(predicate)
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
