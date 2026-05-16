package com.codeint.shopapp.hilt.data.cart.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.cart.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<CartEntity> {
        val cached = cacheManager.get("cart_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<CartEntity>()
        val fromDb = databaseManager.query("carts")
        return fromDb.map { row -> CartEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): CartEntity? {
        val cached = cacheManager.get("cart_$id") as? CartEntity
        if (cached != null) return cached
        val rows = databaseManager.query("carts", "id = '$id'")
        return rows.firstOrNull()?.let { CartEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: CartEntity) {
        databaseManager.insert("carts", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("cart_${entity.id}", entity)
    }

    fun saveAll(entities: List<CartEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("cart_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("carts", "id = '$id'")
        cacheManager.evict("cart_$id")
    }

    fun clear() {
        databaseManager.delete("carts", "1=1")
        cacheManager.clear()
    }
}
