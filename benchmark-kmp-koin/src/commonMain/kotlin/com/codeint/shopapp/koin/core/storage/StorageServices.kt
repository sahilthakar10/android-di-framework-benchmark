package com.codeint.shopapp.koin.core.storage


class DatabaseManager constructor() {
    fun query(table: String, where: String = ""): List<Map<String, Any>> = emptyList()
    fun insert(table: String, values: Map<String, Any>): Long = 1L
    fun update(table: String, values: Map<String, Any>, where: String): Int = 1
    fun delete(table: String, where: String): Int = 1
    fun transaction(block: () -> Unit) { block() }
}

class PreferencesManager constructor() {
    private val store = mutableMapOf<String, Any>()
    fun putString(key: String, value: String) { store[key] = value }
    fun getString(key: String): String? = store[key] as? String
    fun putBoolean(key: String, value: Boolean) { store[key] = value }
    fun getBoolean(key: String, default: Boolean = false): Boolean = store[key] as? Boolean ?: default
    fun putLong(key: String, value: Long) { store[key] = value }
    fun getLong(key: String, default: Long = 0): Long = store[key] as? Long ?: default
    fun remove(key: String) { store.remove(key) }
}

class SecureStorage constructor() {
    private val store = mutableMapOf<String, String>()
    fun put(key: String, value: String) { store[key] = value }
    fun get(key: String): String? = store[key]
    fun remove(key: String) { store.remove(key) }
}

class CacheManager constructor(private val preferencesManager: PreferencesManager) {
    private val memoryCache = mutableMapOf<String, CacheEntry>()
    fun get(key: String): Any? = memoryCache[key]?.takeIf { !it.isExpired() }?.value
    fun put(key: String, value: Any, ttlMs: Long = 300_000) { memoryCache[key] = CacheEntry(value, com.codeint.shopapp.common.platform.currentTimeMillis() + ttlMs) }
    fun evict(key: String) { memoryCache.remove(key) }
    fun clear() { memoryCache.clear() }
}

class FileManager constructor() {
    fun read(path: String): ByteArray = ByteArray(0)
    fun write(path: String, data: ByteArray) {}
    fun delete(path: String): Boolean = true
    fun exists(path: String): Boolean = false
}

class DownloadManager constructor(private val fileManager: FileManager) {
    fun download(url: String, destination: String): String = destination
    fun cancelDownload(id: String) {}
}

data class CacheEntry(val value: Any, val expiresAt: Long) {
    fun isExpired(): Boolean = com.codeint.shopapp.common.platform.currentTimeMillis() > expiresAt
}
