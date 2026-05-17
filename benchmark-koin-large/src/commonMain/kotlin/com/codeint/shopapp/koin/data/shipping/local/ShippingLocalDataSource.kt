package com.codeint.shopapp.koin.data.shipping.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.shipping.*

class ShippingLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ShippingEntity> = emptyList()
    fun getById(id: String): ShippingEntity? = null
    fun save(entity: ShippingEntity) { db.insert("shippings", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<ShippingEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("shippings", "id = '$id'") }
    fun clear() { db.delete("shippings", "1=1") }
}
