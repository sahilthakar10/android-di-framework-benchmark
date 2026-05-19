package com.codeint.shopapp.kinject.domain.user

import com.codeint.shopapp.kinject.data.user.UserRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetUserListUseCase(private val repo: UserRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_user_list") }
}
@Inject class GetUserDetailUseCase(private val repo: UserRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_user_detail") }
}
@Inject class CreateUserUseCase(private val repo: UserRepository, private val logger: AppLogger) { fun execute(m: UserDomainModel) = repo.create(m) }
@Inject class UpdateUserUseCase(private val repo: UserRepository, private val logger: AppLogger) { fun execute(id: String, m: UserDomainModel) = repo.update(id, m) }
@Inject class DeleteUserUseCase(private val repo: UserRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchUserUseCase(private val repo: UserRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateUserUseCase(private val logger: AppLogger) {
    fun execute(m: UserDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshUserCacheUseCase(private val repo: UserRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetUserCountUseCase(private val repo: UserRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterUserUseCase(private val repo: UserRepository) { fun execute(p: (UserDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
