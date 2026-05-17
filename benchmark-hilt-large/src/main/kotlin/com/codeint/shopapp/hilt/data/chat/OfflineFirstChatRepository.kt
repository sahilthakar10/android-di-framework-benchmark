package com.codeint.shopapp.hilt.data.chat

import com.codeint.shopapp.hilt.data.chat.remote.ChatRemoteDataSource
import com.codeint.shopapp.hilt.data.chat.local.ChatLocalDataSource
import com.codeint.shopapp.hilt.data.chat.mapper.ChatMapper
import com.codeint.shopapp.hilt.domain.chat.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstChatRepository @Inject constructor(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val mapper: ChatMapper,
    private val logger: AppLogger
) : ChatRepository {

    override fun getAll(request: ChatRequest): PagedResult<ChatDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("ChatRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    override fun getById(id: String): ChatDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: ChatDomainModel): ChatDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: ChatDomainModel): ChatDomainModel {
        val entity = mapper.toEntity(model)
        val updated = remoteDataSource.update(id, entity)
        localDataSource.save(updated)
        return mapper.toDomain(updated)
    }

    override fun delete(id: String): Boolean {
        val result = remoteDataSource.delete(id)
        if (result) localDataSource.delete(id)
        return result
    }

    override fun search(query: String, page: Int) = mapper.toPagedResult(remoteDataSource.search(query, page))
    override fun clearCache() { localDataSource.clear() }
}
