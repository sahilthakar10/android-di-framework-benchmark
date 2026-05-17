package com.codeint.shopapp.koin.domain.chat

import com.codeint.shopapp.koin.data.chat.ChatRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetChatListUseCase(private val repo: ChatRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_chat_list") }
}
class GetChatDetailUseCase(private val repo: ChatRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_chat_detail") }
}
class CreateChatUseCase(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(m: ChatDomainModel) = repo.create(m) }
class UpdateChatUseCase(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(id: String, m: ChatDomainModel) = repo.update(id, m) }
class DeleteChatUseCase(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchChatUseCase(private val repo: ChatRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateChatUseCase(private val logger: AppLogger) {
    fun execute(m: ChatDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshChatCacheUseCase(private val repo: ChatRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetChatCountUseCase(private val repo: ChatRepository) { fun execute() = repo.getAll().totalCount }
class FilterChatUseCase(private val repo: ChatRepository) { fun execute(p: (ChatDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
