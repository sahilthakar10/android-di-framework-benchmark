package com.codeint.shopapp.koin.domain.order

import com.codeint.shopapp.koin.data.order.OrderRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger


class GetOrderListUseCase constructor(
    private val repository: OrderRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<OrderDomainModel> {
        analytics.track("get_order_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetOrderDetailUseCase constructor(
    private val repository: OrderRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): OrderDomainModel? {
        analytics.track("get_order_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateOrderUseCase constructor(
    private val repository: OrderRepository,
    private val logger: AppLogger
) {
    fun execute(model: OrderDomainModel): OrderDomainModel {
        logger.info("OrderUseCase", "Creating order: ${model.name}")
        return repository.create(model)
    }
}

class UpdateOrderUseCase constructor(
    private val repository: OrderRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: OrderDomainModel): OrderDomainModel {
        logger.info("OrderUseCase", "Updating order: $id")
        return repository.update(id, model)
    }
}

class DeleteOrderUseCase constructor(
    private val repository: OrderRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("OrderUseCase", "Deleting order: $id")
        return repository.delete(id)
    }
}

class SearchOrderUseCase constructor(
    private val repository: OrderRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<OrderDomainModel> {
        analytics.track("search_order", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateOrderUseCase constructor(private val logger: AppLogger) {
    fun execute(model: OrderDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshOrderCacheUseCase constructor(
    private val repository: OrderRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("OrderUseCase", "Refreshing order cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetOrderCountUseCase constructor(private val repository: OrderRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterOrderUseCase constructor(private val repository: OrderRepository) {
    fun execute(predicate: (OrderDomainModel) -> Boolean): List<OrderDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
