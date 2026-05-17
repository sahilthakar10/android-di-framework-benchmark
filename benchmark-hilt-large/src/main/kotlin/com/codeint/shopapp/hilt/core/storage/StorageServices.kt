package com.codeint.shopapp.hilt.core.storage

import javax.inject.Inject

interface DatabaseManager {
    fun query(table: String, where: String = ""): List<Map<String, Any>>
    fun insert(table: String, values: Map<String, Any>): Long
    fun update(table: String, values: Map<String, Any>, where: String): Int
    fun delete(table: String, where: String): Int
    fun transaction(block: () -> Unit)
}

interface PreferencesManager {
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun putLong(key: String, value: Long)
    fun getLong(key: String, default: Long = 0): Long
    fun remove(key: String)
}

interface SecureStorage {
    fun put(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
}

interface CacheManager {
    fun get(key: String): Any?
    fun put(key: String, value: Any, ttlMs: Long = 300_000)
    fun evict(key: String)
    fun clear()
}

class RealDatabaseManager @Inject constructor() : DatabaseManager {
    override fun query(table: String, where: String) = emptyList<Map<String, Any>>()
    override fun insert(table: String, values: Map<String, Any>) = 1L
    override fun update(table: String, values: Map<String, Any>, where: String) = 1
    override fun delete(table: String, where: String) = 1
    override fun transaction(block: () -> Unit) { block() }
}

class RealPreferencesManager @Inject constructor() : PreferencesManager {
    private val store = mutableMapOf<String, Any>()
    override fun putString(key: String, value: String) { store[key] = value }
    override fun getString(key: String) = store[key] as? String
    override fun putBoolean(key: String, value: Boolean) { store[key] = value }
    override fun getBoolean(key: String, default: Boolean) = store[key] as? Boolean ?: default
    override fun putLong(key: String, value: Long) { store[key] = value }
    override fun getLong(key: String, default: Long) = store[key] as? Long ?: default
    override fun remove(key: String) { store.remove(key) }
}

class RealSecureStorage @Inject constructor() : SecureStorage {
    private val store = mutableMapOf<String, String>()
    override fun put(key: String, value: String) { store[key] = value }
    override fun get(key: String) = store[key]
    override fun remove(key: String) { store.remove(key) }
}

class RealCacheManager @Inject constructor(private val prefs: PreferencesManager) : CacheManager {
    private val memoryCache = mutableMapOf<String, CacheEntry>()
    override fun get(key: String): Any? = memoryCache[key]?.takeIf { !it.isExpired() }?.value
    override fun put(key: String, value: Any, ttlMs: Long) { memoryCache[key] = CacheEntry(value, System.currentTimeMillis() + ttlMs) }
    override fun evict(key: String) { memoryCache.remove(key) }
    override fun clear() { memoryCache.clear() }
}

class FileManager @Inject constructor() {
    fun read(path: String): ByteArray = ByteArray(0)
    fun write(path: String, data: ByteArray) {}
    fun delete(path: String) = true
    fun exists(path: String) = false
}

class DownloadManager @Inject constructor(private val fileManager: FileManager) {
    fun download(url: String, destination: String) = destination
    fun cancelDownload(id: String) {}
}

data class CacheEntry(val value: Any, val expiresAt: Long) {
    fun isExpired() = System.currentTimeMillis() > expiresAt
}
