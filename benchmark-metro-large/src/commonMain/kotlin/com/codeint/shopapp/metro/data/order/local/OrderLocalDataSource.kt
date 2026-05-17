package com.codeint.shopapp.metro.data.order.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.order.*
import dev.zacsweers.metro.Inject

class OrderLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<OrderEntity> = emptyList()
    fun getById(id: String): OrderEntity? = null
    fun save(entity: OrderEntity) { db.insert("orders", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<OrderEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("orders", "id = '$id'") }
    fun clear() { db.delete("orders", "1=1") }
}
