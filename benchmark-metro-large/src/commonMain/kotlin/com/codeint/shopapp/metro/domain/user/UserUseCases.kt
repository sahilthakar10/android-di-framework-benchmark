package com.codeint.shopapp.metro.domain.user

import com.codeint.shopapp.metro.data.user.UserRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetUserListUseCase @Inject constructor(private val repo: UserRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_user_list") }
}
class GetUserDetailUseCase @Inject constructor(private val repo: UserRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_user_detail") }
}
class CreateUserUseCase @Inject constructor(private val repo: UserRepository, private val logger: AppLogger) { fun execute(m: UserDomainModel) = repo.create(m) }
class UpdateUserUseCase @Inject constructor(private val repo: UserRepository, private val logger: AppLogger) { fun execute(id: String, m: UserDomainModel) = repo.update(id, m) }
class DeleteUserUseCase @Inject constructor(private val repo: UserRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchUserUseCase @Inject constructor(private val repo: UserRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateUserUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(m: UserDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshUserCacheUseCase @Inject constructor(private val repo: UserRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetUserCountUseCase @Inject constructor(private val repo: UserRepository) { fun execute() = repo.getAll().totalCount }
class FilterUserUseCase @Inject constructor(private val repo: UserRepository) { fun execute(p: (UserDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
