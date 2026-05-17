package com.codeint.shopapp.koin.data.feed.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.feed.*

class FeedLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<FeedEntity> = emptyList()
    fun getById(id: String): FeedEntity? = null
    fun save(entity: FeedEntity) { db.insert("feeds", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<FeedEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("feeds", "id = '$id'") }
    fun clear() { db.delete("feeds", "1=1") }
}
