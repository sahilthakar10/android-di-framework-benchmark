package com.codeint.shopapp.metro.data.chat.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.chat.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ChatLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<ChatEntity> {
        val cached = cacheManager.get("chat_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<ChatEntity>()
        val fromDb = databaseManager.query("chats")
        return fromDb.map { row -> ChatEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): ChatEntity? {
        val cached = cacheManager.get("chat_$id") as? ChatEntity
        if (cached != null) return cached
        val rows = databaseManager.query("chats", "id = '$id'")
        return rows.firstOrNull()?.let { ChatEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: ChatEntity) {
        databaseManager.insert("chats", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("chat_${entity.id}", entity)
    }

    fun saveAll(entities: List<ChatEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("chat_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("chats", "id = '$id'")
        cacheManager.evict("chat_$id")
    }

    fun clear() {
        databaseManager.delete("chats", "1=1")
        cacheManager.clear()
    }
}
