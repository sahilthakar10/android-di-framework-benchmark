package com.codeint.shopapp.hilt.data.review.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.review.*
import javax.inject.Inject

class ReviewLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<ReviewEntity> = emptyList()
    fun getById(id: String): ReviewEntity? = null
    fun save(entity: ReviewEntity) { databaseManager.insert("reviews", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<ReviewEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("reviews", "id = '$id'") }
    fun clear() { databaseManager.delete("reviews", "1=1") }
}
