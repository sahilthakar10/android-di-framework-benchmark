package com.codeint.shopapp.kinject.data.chat.local

import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.data.chat.*
import me.tatarka.inject.annotations.Inject

@Inject class ChatLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ChatEntity> = emptyList()
    fun getById(id: String): ChatEntity? = null
    fun save(entity: ChatEntity) { db.insert("chats", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<ChatEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("chats", "id = '$id'") }
    fun clear() { db.delete("chats", "1=1") }
}
