package com.codeint.shopapp.hilt.domain.chat

import com.codeint.shopapp.hilt.data.chat.ChatRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetChatListUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<ChatDomainModel> {
        analytics.track("get_chat_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetChatDetailUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): ChatDomainModel? {
        analytics.track("get_chat_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateChatUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val logger: AppLogger
) {
    fun execute(model: ChatDomainModel): ChatDomainModel {
        logger.info("ChatUseCase", "Creating chat: ${model.name}")
        return repository.create(model)
    }
}

class UpdateChatUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: ChatDomainModel): ChatDomainModel {
        logger.info("ChatUseCase", "Updating chat: $id")
        return repository.update(id, model)
    }
}

class DeleteChatUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("ChatUseCase", "Deleting chat: $id")
        return repository.delete(id)
    }
}

class SearchChatUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<ChatDomainModel> {
        analytics.track("search_chat", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateChatUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: ChatDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshChatCacheUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("ChatUseCase", "Refreshing chat cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetChatCountUseCase @Inject constructor(private val repository: ChatRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterChatUseCase @Inject constructor(private val repository: ChatRepository) {
    fun execute(predicate: (ChatDomainModel) -> Boolean): List<ChatDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
