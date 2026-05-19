package com.codeint.shopapp.kinject.data.user.local

import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.data.user.*
import me.tatarka.inject.annotations.Inject

@Inject class UserLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<UserEntity> = emptyList()
    fun getById(id: String): UserEntity? = null
    fun save(entity: UserEntity) { db.insert("users", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<UserEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("users", "id = '$id'") }
    fun clear() { db.delete("users", "1=1") }
}
