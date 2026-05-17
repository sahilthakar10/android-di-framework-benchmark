package com.codeint.shopapp.metro.data.cart

import com.codeint.shopapp.metro.data.cart.remote.CartRemoteDataSource
import com.codeint.shopapp.metro.data.cart.local.CartLocalDataSource
import com.codeint.shopapp.metro.data.cart.mapper.CartMapper
import com.codeint.shopapp.metro.domain.cart.*
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirstCartRepository @Inject constructor(
    private val remote: CartRemoteDataSource, private val local: CartLocalDataSource,
    private val mapper: CartMapper, private val logger: AppLogger
) : CartRepository {
    override fun getAll(request: CartRequest): PagedResult<CartDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("CartRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: CartDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: CartDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
