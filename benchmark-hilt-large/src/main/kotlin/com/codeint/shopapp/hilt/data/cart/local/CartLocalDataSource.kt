package com.codeint.shopapp.hilt.data.cart.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.cart.*
import javax.inject.Inject

class CartLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<CartEntity> = emptyList()
    fun getById(id: String): CartEntity? = null
    fun save(entity: CartEntity) { databaseManager.insert("carts", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<CartEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("carts", "id = '$id'") }
    fun clear() { databaseManager.delete("carts", "1=1") }
}
