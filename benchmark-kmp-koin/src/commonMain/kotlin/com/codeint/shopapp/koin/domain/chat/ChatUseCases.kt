package com.codeint.shopapp.koin.domain.chat

import com.codeint.shopapp.koin.data.chat.ChatRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger


class GetChatListUseCase constructor(
    private val repository: ChatRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<ChatDomainModel> {
        analytics.track("get_chat_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetChatDetailUseCase constructor(
    private val repository: ChatRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): ChatDomainModel? {
        analytics.track("get_chat_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateChatUseCase constructor(
    private val repository: ChatRepository,
    private val logger: AppLogger
) {
    fun execute(model: ChatDomainModel): ChatDomainModel {
        logger.info("ChatUseCase", "Creating chat: ${model.name}")
        return repository.create(model)
    }
}

class UpdateChatUseCase constructor(
    private val repository: ChatRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: ChatDomainModel): ChatDomainModel {
        logger.info("ChatUseCase", "Updating chat: $id")
        return repository.update(id, model)
    }
}

class DeleteChatUseCase constructor(
    private val repository: ChatRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("ChatUseCase", "Deleting chat: $id")
        return repository.delete(id)
    }
}

class SearchChatUseCase constructor(
    private val repository: ChatRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<ChatDomainModel> {
        analytics.track("search_chat", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateChatUseCase constructor(private val logger: AppLogger) {
    fun execute(model: ChatDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshChatCacheUseCase constructor(
    private val repository: ChatRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("ChatUseCase", "Refreshing chat cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetChatCountUseCase constructor(private val repository: ChatRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterChatUseCase constructor(private val repository: ChatRepository) {
    fun execute(predicate: (ChatDomainModel) -> Boolean): List<ChatDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
