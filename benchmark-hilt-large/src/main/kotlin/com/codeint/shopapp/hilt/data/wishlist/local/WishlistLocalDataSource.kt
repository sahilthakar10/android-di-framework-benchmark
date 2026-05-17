package com.codeint.shopapp.hilt.data.wishlist.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.wishlist.*
import javax.inject.Inject

class WishlistLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<WishlistEntity> = emptyList()
    fun getById(id: String): WishlistEntity? = null
    fun save(entity: WishlistEntity) { databaseManager.insert("wishlists", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<WishlistEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("wishlists", "id = '$id'") }
    fun clear() { databaseManager.delete("wishlists", "1=1") }
}
