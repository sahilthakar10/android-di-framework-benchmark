package com.codeint.shopapp.koin.data.user

import com.codeint.shopapp.koin.data.user.remote.UserRemoteDataSource
import com.codeint.shopapp.koin.data.user.local.UserLocalDataSource
import com.codeint.shopapp.koin.data.user.mapper.UserMapper
import com.codeint.shopapp.koin.domain.user.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class OfflineFirstUserRepository(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource,
    private val mapper: UserMapper,
    private val logger: AppLogger
) : UserRepository {
    override fun getAll(request: UserRequest): PagedResult<UserDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("UserRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: UserDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: UserDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
