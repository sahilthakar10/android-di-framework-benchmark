package com.codeint.shopapp.koin.data.chat

import com.codeint.shopapp.koin.data.chat.remote.ChatRemoteDataSource
import com.codeint.shopapp.koin.data.chat.local.ChatLocalDataSource
import com.codeint.shopapp.koin.data.chat.mapper.ChatMapper
import com.codeint.shopapp.koin.domain.chat.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class OfflineFirstChatRepository(
    private val remote: ChatRemoteDataSource,
    private val local: ChatLocalDataSource,
    private val mapper: ChatMapper,
    private val logger: AppLogger
) : ChatRepository {
    override fun getAll(request: ChatRequest): PagedResult<ChatDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("ChatRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: ChatDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: ChatDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
