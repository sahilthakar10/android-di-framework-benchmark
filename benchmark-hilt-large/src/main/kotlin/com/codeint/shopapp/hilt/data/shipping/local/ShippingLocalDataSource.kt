package com.codeint.shopapp.hilt.data.shipping.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.shipping.*
import javax.inject.Inject

class ShippingLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<ShippingEntity> = emptyList()
    fun getById(id: String): ShippingEntity? = null
    fun save(entity: ShippingEntity) { databaseManager.insert("shippings", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<ShippingEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("shippings", "id = '$id'") }
    fun clear() { databaseManager.delete("shippings", "1=1") }
}
