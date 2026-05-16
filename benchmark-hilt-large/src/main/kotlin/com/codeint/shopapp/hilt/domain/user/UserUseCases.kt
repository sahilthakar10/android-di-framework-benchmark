package com.codeint.shopapp.hilt.domain.user

import com.codeint.shopapp.hilt.data.user.UserRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetUserListUseCase @Inject constructor(
    private val repository: UserRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<UserDomainModel> {
        analytics.track("get_user_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetUserDetailUseCase @Inject constructor(
    private val repository: UserRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): UserDomainModel? {
        analytics.track("get_user_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val logger: AppLogger
) {
    fun execute(model: UserDomainModel): UserDomainModel {
        logger.info("UserUseCase", "Creating user: ${model.name}")
        return repository.create(model)
    }
}

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: UserDomainModel): UserDomainModel {
        logger.info("UserUseCase", "Updating user: $id")
        return repository.update(id, model)
    }
}

class DeleteUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("UserUseCase", "Deleting user: $id")
        return repository.delete(id)
    }
}

class SearchUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<UserDomainModel> {
        analytics.track("search_user", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateUserUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: UserDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshUserCacheUseCase @Inject constructor(
    private val repository: UserRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("UserUseCase", "Refreshing user cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetUserCountUseCase @Inject constructor(private val repository: UserRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterUserUseCase @Inject constructor(private val repository: UserRepository) {
    fun execute(predicate: (UserDomainModel) -> Boolean): List<UserDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
