package com.codeint.shopapp.metro.data.shipping.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.shipping.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ShippingLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<ShippingEntity> {
        val cached = cacheManager.get("shipping_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<ShippingEntity>()
        val fromDb = databaseManager.query("shippings")
        return fromDb.map { row -> ShippingEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): ShippingEntity? {
        val cached = cacheManager.get("shipping_$id") as? ShippingEntity
        if (cached != null) return cached
        val rows = databaseManager.query("shippings", "id = '$id'")
        return rows.firstOrNull()?.let { ShippingEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: ShippingEntity) {
        databaseManager.insert("shippings", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("shipping_${entity.id}", entity)
    }

    fun saveAll(entities: List<ShippingEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("shipping_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("shippings", "id = '$id'")
        cacheManager.evict("shipping_$id")
    }

    fun clear() {
        databaseManager.delete("shippings", "1=1")
        cacheManager.clear()
    }
}
