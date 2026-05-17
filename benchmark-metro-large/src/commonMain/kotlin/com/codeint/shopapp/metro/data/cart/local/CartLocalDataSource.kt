package com.codeint.shopapp.metro.data.cart.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.cart.*
import dev.zacsweers.metro.Inject

class CartLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<CartEntity> = emptyList()
    fun getById(id: String): CartEntity? = null
    fun save(entity: CartEntity) { db.insert("carts", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<CartEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("carts", "id = '$id'") }
    fun clear() { db.delete("carts", "1=1") }
}
