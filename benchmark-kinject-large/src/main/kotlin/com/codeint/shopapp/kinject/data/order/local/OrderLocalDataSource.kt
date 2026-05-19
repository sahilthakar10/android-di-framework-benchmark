package com.codeint.shopapp.kinject.data.order.local

import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.data.order.*
import me.tatarka.inject.annotations.Inject

@Inject class OrderLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<OrderEntity> = emptyList()
    fun getById(id: String): OrderEntity? = null
    fun save(entity: OrderEntity) { db.insert("orders", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<OrderEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("orders", "id = '$id'") }
    fun clear() { db.delete("orders", "1=1") }
}
