package com.codeint.shopapp.hilt.data.chat.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.chat.*
import javax.inject.Inject

class ChatLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<ChatEntity> = emptyList()
    fun getById(id: String): ChatEntity? = null
    fun save(entity: ChatEntity) { databaseManager.insert("chats", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<ChatEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("chats", "id = '$id'") }
    fun clear() { databaseManager.delete("chats", "1=1") }
}
