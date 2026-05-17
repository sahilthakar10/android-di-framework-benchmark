package com.codeint.shopapp.hilt.data.order.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.order.*
import javax.inject.Inject

class OrderLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<OrderEntity> = emptyList()
    fun getById(id: String): OrderEntity? = null
    fun save(entity: OrderEntity) { databaseManager.insert("orders", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<OrderEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("orders", "id = '$id'") }
    fun clear() { databaseManager.delete("orders", "1=1") }
}
