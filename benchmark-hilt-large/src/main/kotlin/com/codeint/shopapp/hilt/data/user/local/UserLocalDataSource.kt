package com.codeint.shopapp.hilt.data.user.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.user.*
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<UserEntity> = emptyList()
    fun getById(id: String): UserEntity? = null
    fun save(entity: UserEntity) { databaseManager.insert("users", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<UserEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("users", "id = '$id'") }
    fun clear() { databaseManager.delete("users", "1=1") }
}
