package com.codeint.shopapp.metro.data.product

import com.codeint.shopapp.metro.data.product.remote.ProductRemoteDataSource
import com.codeint.shopapp.metro.data.product.local.ProductLocalDataSource
import com.codeint.shopapp.metro.data.product.mapper.ProductMapper
import com.codeint.shopapp.metro.domain.product.*
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirstProductRepository @Inject constructor(
    private val remote: ProductRemoteDataSource, private val local: ProductLocalDataSource,
    private val mapper: ProductMapper, private val logger: AppLogger
) : ProductRepository {
    override fun getAll(request: ProductRequest): PagedResult<ProductDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("ProductRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: ProductDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: ProductDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
