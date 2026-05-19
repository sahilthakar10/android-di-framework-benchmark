package com.codeint.shopapp.kinject.domain.chat

import com.codeint.shopapp.kinject.data.chat.ChatRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetChatListUseCase(private val repo: ChatRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_chat_list") }
}
@Inject class GetChatDetailUseCase(private val repo: ChatRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_chat_detail") }
}
@Inject class CreateChatUseCase(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(m: ChatDomainModel) = repo.create(m) }
@Inject class UpdateChatUseCase(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(id: String, m: ChatDomainModel) = repo.update(id, m) }
@Inject class DeleteChatUseCase(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchChatUseCase(private val repo: ChatRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateChatUseCase(private val logger: AppLogger) {
    fun execute(m: ChatDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshChatCacheUseCase(private val repo: ChatRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetChatCountUseCase(private val repo: ChatRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterChatUseCase(private val repo: ChatRepository) { fun execute(p: (ChatDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
