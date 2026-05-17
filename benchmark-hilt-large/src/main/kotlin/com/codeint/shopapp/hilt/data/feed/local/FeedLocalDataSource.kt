package com.codeint.shopapp.hilt.data.feed.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.feed.*
import javax.inject.Inject

class FeedLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<FeedEntity> = emptyList()
    fun getById(id: String): FeedEntity? = null
    fun save(entity: FeedEntity) { databaseManager.insert("feeds", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<FeedEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("feeds", "id = '$id'") }
    fun clear() { databaseManager.delete("feeds", "1=1") }
}
