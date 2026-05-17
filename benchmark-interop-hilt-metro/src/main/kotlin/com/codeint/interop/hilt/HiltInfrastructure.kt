package com.codeint.interop.hilt

import com.codeint.interop.shared.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HILT SIDE — Core infrastructure.
 *
 * These are the existing services in a typical Hilt-based app.
 * They were here before Metro was introduced.
 * Metro will consume them via @Includes(daggerComponent).
 */

// ── Implementations ──

@Singleton
class RealHttpClient @Inject constructor() : HttpClient {
    override fun get(url: String): String = """{"status":"ok","url":"$url"}"""
    override fun post(url: String, body: String): String = """{"status":"created","url":"$url"}"""
}

@Singleton
class RealAuthManager @Inject constructor() : AuthManager {
    override fun isLoggedIn(): Boolean = true
    override fun getAccessToken(): String = "Bearer hilt_access_token_${System.nanoTime()}"
    override fun getUserId(): String = "user_hilt_123"
}

@Singleton
class RealAnalyticsTracker @Inject constructor(
    private val logger: Logger
) : AnalyticsTracker {
    override fun track(event: String, params: Map<String, Any>) {
        logger.debug("Analytics", "Event: $event, params: $params")
    }
    override fun screen(name: String) = track("screen_view", mapOf("screen" to name))
}

@Singleton
class RealDatabaseManager @Inject constructor() : DatabaseManager {
    private val tables = mutableMapOf<String, MutableList<Map<String, Any>>>()
    override fun query(table: String, where: String): List<Map<String, Any>> = tables[table] ?: emptyList()
    override fun insert(table: String, values: Map<String, Any>): Long {
        tables.getOrPut(table) { mutableListOf() }.add(values)
        return tables[table]!!.size.toLong()
    }
}

@Singleton
class RealCacheManager @Inject constructor() : CacheManager {
    private val cache = mutableMapOf<String, Any>()
    override fun get(key: String): Any? = cache[key]
    override fun put(key: String, value: Any, ttlMs: Long) { cache[key] = value }
    override fun evict(key: String) { cache.remove(key) }
}

@Singleton
class RealLogger @Inject constructor() : Logger {
    override fun debug(tag: String, message: String) { /* logcat in real app */ }
    override fun error(tag: String, message: String, throwable: Throwable?) { /* logcat + crash reporter */ }
}

// ── Hilt Module (binds interfaces to implementations) ──

@Module
@InstallIn(SingletonComponent::class)
abstract class HiltInfrastructureModule {
    @dagger.Binds abstract fun bindHttpClient(impl: RealHttpClient): HttpClient
    @dagger.Binds abstract fun bindAuthManager(impl: RealAuthManager): AuthManager
    @dagger.Binds abstract fun bindAnalyticsTracker(impl: RealAnalyticsTracker): AnalyticsTracker
    @dagger.Binds abstract fun bindDatabaseManager(impl: RealDatabaseManager): DatabaseManager
    @dagger.Binds abstract fun bindCacheManager(impl: RealCacheManager): CacheManager
    @dagger.Binds abstract fun bindLogger(impl: RealLogger): Logger
}

/**
 * Hilt Entry Point — exposes Hilt-managed singletons for Metro to consume.
 * In a real app with Metro interop via @Includes, this wouldn't be needed.
 * But for demonstration, this shows how to manually bridge if needed.
 */
@dagger.hilt.EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltCoreEntryPoint {
    fun interopHttpClient(): HttpClient
    fun interopAuthManager(): AuthManager
    fun interopAnalyticsTracker(): AnalyticsTracker
    fun interopDatabaseManager(): DatabaseManager
    fun interopCacheManager(): CacheManager
    fun interopLogger(): Logger
}
