package com.codeint.shopapp.koin.data.chat.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.chat.*

class ChatLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ChatEntity> = emptyList()
    fun getById(id: String): ChatEntity? = null
    fun save(entity: ChatEntity) { db.insert("chats", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<ChatEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("chats", "id = '$id'") }
    fun clear() { db.delete("chats", "1=1") }
}
