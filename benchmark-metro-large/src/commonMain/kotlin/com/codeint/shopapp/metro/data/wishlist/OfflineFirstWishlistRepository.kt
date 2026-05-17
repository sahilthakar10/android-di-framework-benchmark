package com.codeint.shopapp.metro.data.wishlist

import com.codeint.shopapp.metro.data.wishlist.remote.WishlistRemoteDataSource
import com.codeint.shopapp.metro.data.wishlist.local.WishlistLocalDataSource
import com.codeint.shopapp.metro.data.wishlist.mapper.WishlistMapper
import com.codeint.shopapp.metro.domain.wishlist.*
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirstWishlistRepository @Inject constructor(
    private val remote: WishlistRemoteDataSource, private val local: WishlistLocalDataSource,
    private val mapper: WishlistMapper, private val logger: AppLogger
) : WishlistRepository {
    override fun getAll(request: WishlistRequest): PagedResult<WishlistDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("WishlistRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: WishlistDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: WishlistDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
