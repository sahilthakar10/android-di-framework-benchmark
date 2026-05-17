package com.codeint.shopapp.metro.domain.chat

import com.codeint.shopapp.metro.data.chat.ChatRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetChatListUseCase @Inject constructor(private val repo: ChatRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_chat_list") }
}
class GetChatDetailUseCase @Inject constructor(private val repo: ChatRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_chat_detail") }
}
class CreateChatUseCase @Inject constructor(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(m: ChatDomainModel) = repo.create(m) }
class UpdateChatUseCase @Inject constructor(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(id: String, m: ChatDomainModel) = repo.update(id, m) }
class DeleteChatUseCase @Inject constructor(private val repo: ChatRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchChatUseCase @Inject constructor(private val repo: ChatRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateChatUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(m: ChatDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshChatCacheUseCase @Inject constructor(private val repo: ChatRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetChatCountUseCase @Inject constructor(private val repo: ChatRepository) { fun execute() = repo.getAll().totalCount }
class FilterChatUseCase @Inject constructor(private val repo: ChatRepository) { fun execute(p: (ChatDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
