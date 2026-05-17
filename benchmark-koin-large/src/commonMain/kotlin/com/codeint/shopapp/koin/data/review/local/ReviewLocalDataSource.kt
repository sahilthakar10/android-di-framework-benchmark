package com.codeint.shopapp.koin.data.review.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.review.*

class ReviewLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ReviewEntity> = emptyList()
    fun getById(id: String): ReviewEntity? = null
    fun save(entity: ReviewEntity) { db.insert("reviews", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<ReviewEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("reviews", "id = '$id'") }
    fun clear() { db.delete("reviews", "1=1") }
}
