package com.codeint.shopapp.hilt.data.order.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.order.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<OrderEntity> {
        val cached = cacheManager.get("order_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<OrderEntity>()
        val fromDb = databaseManager.query("orders")
        return fromDb.map { row -> OrderEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): OrderEntity? {
        val cached = cacheManager.get("order_$id") as? OrderEntity
        if (cached != null) return cached
        val rows = databaseManager.query("orders", "id = '$id'")
        return rows.firstOrNull()?.let { OrderEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: OrderEntity) {
        databaseManager.insert("orders", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("order_${entity.id}", entity)
    }

    fun saveAll(entities: List<OrderEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("order_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("orders", "id = '$id'")
        cacheManager.evict("order_$id")
    }

    fun clear() {
        databaseManager.delete("orders", "1=1")
        cacheManager.clear()
    }
}
