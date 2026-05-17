package com.codeint.shopapp.koin.data.cart.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.cart.*

class CartLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<CartEntity> = emptyList()
    fun getById(id: String): CartEntity? = null
    fun save(entity: CartEntity) { db.insert("carts", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<CartEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("carts", "id = '$id'") }
    fun clear() { db.delete("carts", "1=1") }
}
