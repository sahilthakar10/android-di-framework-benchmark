package com.codeint.shopapp.metro.data.feed.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.feed.*
import dev.zacsweers.metro.Inject

class FeedLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<FeedEntity> = emptyList()
    fun getById(id: String): FeedEntity? = null
    fun save(entity: FeedEntity) { db.insert("feeds", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<FeedEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("feeds", "id = '$id'") }
    fun clear() { db.delete("feeds", "1=1") }
}
