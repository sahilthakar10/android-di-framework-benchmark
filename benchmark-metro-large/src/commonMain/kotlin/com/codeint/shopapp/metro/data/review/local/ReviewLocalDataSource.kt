package com.codeint.shopapp.metro.data.review.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.review.*
import dev.zacsweers.metro.Inject

class ReviewLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ReviewEntity> = emptyList()
    fun getById(id: String): ReviewEntity? = null
    fun save(entity: ReviewEntity) { db.insert("reviews", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<ReviewEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("reviews", "id = '$id'") }
    fun clear() { db.delete("reviews", "1=1") }
}
