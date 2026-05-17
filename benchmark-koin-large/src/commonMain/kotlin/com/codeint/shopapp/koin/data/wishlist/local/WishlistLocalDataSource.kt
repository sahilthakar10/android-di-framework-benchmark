package com.codeint.shopapp.koin.data.wishlist.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.wishlist.*

class WishlistLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<WishlistEntity> = emptyList()
    fun getById(id: String): WishlistEntity? = null
    fun save(entity: WishlistEntity) { db.insert("wishlists", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<WishlistEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("wishlists", "id = '$id'") }
    fun clear() { db.delete("wishlists", "1=1") }
}
