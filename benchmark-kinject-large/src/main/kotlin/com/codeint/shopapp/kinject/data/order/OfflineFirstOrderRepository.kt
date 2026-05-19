package com.codeint.shopapp.kinject.data.order

import com.codeint.shopapp.kinject.data.order.remote.OrderRemoteDataSource
import com.codeint.shopapp.kinject.data.order.local.OrderLocalDataSource
import com.codeint.shopapp.kinject.data.order.mapper.OrderMapper
import com.codeint.shopapp.kinject.domain.order.*
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class OfflineFirstOrderRepository(
    private val remote: OrderRemoteDataSource, private val local: OrderLocalDataSource,
    private val mapper: OrderMapper, private val logger: AppLogger
) : OrderRepository {
    override fun getAll(request: OrderRequest): PagedResult<OrderDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("OrderRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: OrderDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: OrderDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
