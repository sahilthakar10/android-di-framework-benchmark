package com.codeint.shopapp.koin.data.wishlist.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.wishlist.*

class WishlistLocalDataSource constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<WishlistEntity> {
        val cached = cacheManager.get("wishlist_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<WishlistEntity>()
        val fromDb = databaseManager.query("wishlists")
        return fromDb.map { row -> WishlistEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): WishlistEntity? {
        val cached = cacheManager.get("wishlist_$id") as? WishlistEntity
        if (cached != null) return cached
        val rows = databaseManager.query("wishlists", "id = '$id'")
        return rows.firstOrNull()?.let { WishlistEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: WishlistEntity) {
        databaseManager.insert("wishlists", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("wishlist_${entity.id}", entity)
    }

    fun saveAll(entities: List<WishlistEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("wishlist_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("wishlists", "id = '$id'")
        cacheManager.evict("wishlist_$id")
    }

    fun clear() {
        databaseManager.delete("wishlists", "1=1")
        cacheManager.clear()
    }
}
