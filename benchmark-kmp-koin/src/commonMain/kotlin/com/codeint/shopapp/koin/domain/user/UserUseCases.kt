package com.codeint.shopapp.koin.domain.user

import com.codeint.shopapp.koin.data.user.UserRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger


class GetUserListUseCase constructor(
    private val repository: UserRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<UserDomainModel> {
        analytics.track("get_user_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetUserDetailUseCase constructor(
    private val repository: UserRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): UserDomainModel? {
        analytics.track("get_user_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateUserUseCase constructor(
    private val repository: UserRepository,
    private val logger: AppLogger
) {
    fun execute(model: UserDomainModel): UserDomainModel {
        logger.info("UserUseCase", "Creating user: ${model.name}")
        return repository.create(model)
    }
}

class UpdateUserUseCase constructor(
    private val repository: UserRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: UserDomainModel): UserDomainModel {
        logger.info("UserUseCase", "Updating user: $id")
        return repository.update(id, model)
    }
}

class DeleteUserUseCase constructor(
    private val repository: UserRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("UserUseCase", "Deleting user: $id")
        return repository.delete(id)
    }
}

class SearchUserUseCase constructor(
    private val repository: UserRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<UserDomainModel> {
        analytics.track("search_user", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateUserUseCase constructor(private val logger: AppLogger) {
    fun execute(model: UserDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshUserCacheUseCase constructor(
    private val repository: UserRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("UserUseCase", "Refreshing user cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetUserCountUseCase constructor(private val repository: UserRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterUserUseCase constructor(private val repository: UserRepository) {
    fun execute(predicate: (UserDomainModel) -> Boolean): List<UserDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
