package com.codeint.shopapp.metro.data.chat.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.chat.*
import dev.zacsweers.metro.Inject

class ChatLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ChatEntity> = emptyList()
    fun getById(id: String): ChatEntity? = null
    fun save(entity: ChatEntity) { db.insert("chats", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<ChatEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("chats", "id = '$id'") }
    fun clear() { db.delete("chats", "1=1") }
}
