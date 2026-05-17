#!/usr/bin/env python3
"""
Rewrites benchmark-hilt-large as a production-quality Hilt app.
Following Google's Now in Android patterns.
"""

import os
import shutil

BASE = "/Users/sahilthakar/AndroidStudioProjects/BenchMarking/benchmark-hilt-large/src/main/kotlin/com/codeint/shopapp/hilt"
PKG = "com.codeint.shopapp.hilt"

DOMAINS = ["product", "user", "cart", "order", "payment", "chat", "search",
           "review", "category", "address", "wishlist", "promotion", "shipping", "feed"]

def upper(s):
    return s[0].upper() + s[1:]

def write(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(content)

# ═══════════════════════════════════════
# STEP 1: Clean old di/ directory
# ═══════════════════════════════════════
di_dir = os.path.join(BASE, "di")
if os.path.exists(di_dir):
    shutil.rmtree(di_dir)
os.makedirs(di_dir)

# Also clean old module directory if exists
module_dir = os.path.join(BASE, "module")
if os.path.exists(module_dir):
    shutil.rmtree(module_dir)

# ═══════════════════════════════════════
# STEP 2: Core Layer — Interfaces + Implementations
# ═══════════════════════════════════════

# --- core/network/NetworkModule.kt (data classes only, no DI) ---
write(f"{BASE}/core/network/NetworkModule.kt", f"""package {PKG}.core.network

class HttpClient(val baseUrl: String, val timeout: Long)
class ApiResponseParser
class NetworkMonitor
class RetryPolicy(val maxRetries: Int, val backoffMs: Long)
class SslPinningConfig(val pins: List<String>)
""")

# --- core/network/ApiServices.kt (interfaces + implementations) ---
write(f"{BASE}/core/network/ApiServices.kt", f"""package {PKG}.core.network

import javax.inject.Inject

// ── Interfaces ──

interface TokenProvider {{
    fun getAccessToken(): String
    fun getRefreshToken(): String
    fun isExpired(): Boolean
}}

interface NetworkLogger {{
    fun logRequest(method: String, url: String, status: Int)
    fun logError(url: String, error: String)
}}

interface CachePolicy {{
    fun isCacheable(url: String): Boolean
    fun getTtl(url: String): Long
}}

// ── Implementations ──

class RealTokenProvider @Inject constructor() : TokenProvider {{
    override fun getAccessToken() = "mock_access_token"
    override fun getRefreshToken() = "mock_refresh_token"
    override fun isExpired() = false
}}

class RealNetworkLogger @Inject constructor() : NetworkLogger {{
    override fun logRequest(method: String, url: String, status: Int) {{}}
    override fun logError(url: String, error: String) {{}}
}}

class RealCachePolicy @Inject constructor() : CachePolicy {{
    override fun isCacheable(url: String) = url.contains("/products") || url.contains("/categories")
    override fun getTtl(url: String) = if (url.contains("/products")) 300_000L else 60_000L
}}

class AuthInterceptor @Inject constructor(private val tokenProvider: TokenProvider) {{
    fun intercept(url: String): Map<String, String> = mapOf("Authorization" to "Bearer ${{tokenProvider.getAccessToken()}}")
}}

class CacheInterceptor @Inject constructor(private val cachePolicy: CachePolicy) {{
    fun shouldCache(url: String) = cachePolicy.isCacheable(url)
}}

class LoggingInterceptor @Inject constructor(private val networkLogger: NetworkLogger) {{
    fun log(method: String, url: String, status: Int) {{ networkLogger.logRequest(method, url, status) }}
}}

class RateLimiter @Inject constructor(private val config: RetryPolicy) {{
    private val requestCounts = mutableMapOf<String, Int>()
    fun shouldThrottle(endpoint: String) = (requestCounts[endpoint] ?: 0) > 100
}}

class WebSocketManager @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {{
    fun connect(channel: String) {{}}
    fun disconnect() {{}}
    fun send(message: String) {{}}
}}

class GraphQLClient @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) {{
    fun query(query: String): String = "{{}}"
    fun mutate(mutation: String): String = "{{}}"
}}

class FileUploader @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {{
    fun upload(filePath: String, endpoint: String): String = "upload_id_123"
}}
""")

# --- core/auth/AuthServices.kt ---
write(f"{BASE}/core/auth/AuthServices.kt", f"""package {PKG}.core.auth

import {PKG}.core.analytics.AnalyticsTracker
import {PKG}.core.storage.PreferencesManager
import {PKG}.core.storage.SecureStorage
import {PKG}.core.network.HttpClient
import {PKG}.core.network.TokenProvider
import javax.inject.Inject

interface AuthManager {{
    fun login(email: String, password: String): Boolean
    fun logout()
    fun isLoggedIn(): Boolean
}}

interface TokenStorage {{
    fun saveTokens(access: String, refresh: String)
    fun getAccessToken(): String?
    fun hasValidToken(): Boolean
    fun clear()
}}

interface SessionManager {{
    fun startSession(userId: String)
    fun getCurrentUserId(): String?
    fun invalidate()
}}

class RealAuthManager @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
    private val analyticsTracker: AnalyticsTracker
) : AuthManager {{
    override fun login(email: String, password: String): Boolean {{ analyticsTracker.track("login_attempt"); return true }}
    override fun logout() {{ tokenStorage.clear(); sessionManager.invalidate(); analyticsTracker.track("logout") }}
    override fun isLoggedIn() = tokenStorage.hasValidToken()
}}

class RealTokenStorage @Inject constructor(private val secureStorage: SecureStorage) : TokenStorage {{
    override fun saveTokens(access: String, refresh: String) {{ secureStorage.put("access_token", access) }}
    override fun getAccessToken() = secureStorage.get("access_token")
    override fun hasValidToken() = getAccessToken() != null
    override fun clear() {{ secureStorage.remove("access_token") }}
}}

class RealSessionManager @Inject constructor(private val prefs: PreferencesManager) : SessionManager {{
    override fun startSession(userId: String) {{ prefs.putString("current_user", userId) }}
    override fun getCurrentUserId() = prefs.getString("current_user")
    override fun invalidate() {{ prefs.remove("current_user") }}
}}

class BiometricAuthProvider @Inject constructor(private val secureStorage: SecureStorage) {{
    fun isAvailable() = true
    fun authenticate() = true
}}

class OAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {{
    fun authorizeWithGoogle() = true
    fun authorizeWithApple() = true
}}

class PasswordValidator @Inject constructor() {{
    fun validate(password: String) = password.length >= 8
    fun getStrength(password: String) = when {{ password.length >= 12 -> 3; password.length >= 8 -> 2; else -> 1 }}
}}

class TwoFactorAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {{
    fun requestCode(phone: String) = true
    fun verifyCode(code: String) = code == "123456"
}}
""")

# --- core/analytics/AnalyticsServices.kt ---
write(f"{BASE}/core/analytics/AnalyticsServices.kt", f"""package {PKG}.core.analytics

import {PKG}.core.storage.PreferencesManager
import javax.inject.Inject

interface AnalyticsTracker {{
    fun track(event: String, params: Map<String, Any> = emptyMap())
    fun setUserId(userId: String)
    fun screen(screenName: String)
}}

interface CrashReporter {{
    fun report(throwable: Throwable)
    fun log(message: String)
    fun setUserId(userId: String)
}}

class EventBus @Inject constructor() {{
    private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>()
    fun post(event: AnalyticsEvent) {{ listeners.forEach {{ it(event) }} }}
    fun subscribe(listener: (AnalyticsEvent) -> Unit) {{ listeners.add(listener) }}
}}

class UserPropertyTracker @Inject constructor() {{
    private val properties = mutableMapOf<String, Any>()
    fun set(key: String, value: Any) {{ properties[key] = value }}
    fun get(key: String): Any? = properties[key]
}}

class RemoteConfigManager @Inject constructor() {{
    fun getString(key: String, default: String) = default
    fun getBoolean(key: String, default: Boolean) = default
    fun getLong(key: String, default: Long) = default
    fun fetch() {{}}
}}

class RealAnalyticsTracker @Inject constructor(
    private val eventBus: EventBus,
    private val userPropertyTracker: UserPropertyTracker
) : AnalyticsTracker {{
    override fun track(event: String, params: Map<String, Any>) {{ eventBus.post(AnalyticsEvent(event, params)) }}
    override fun setUserId(userId: String) {{ userPropertyTracker.set("user_id", userId) }}
    override fun screen(screenName: String) {{ track("screen_view", mapOf("screen" to screenName)) }}
}}

class RealCrashReporter @Inject constructor() : CrashReporter {{
    override fun report(throwable: Throwable) {{}}
    override fun log(message: String) {{}}
    override fun setUserId(userId: String) {{}}
}}

class PerformanceMonitor @Inject constructor() {{
    fun startTrace(name: String) = TraceHandle(name, System.nanoTime())
    fun endTrace(handle: TraceHandle) {{}}
}}

class ABTestManager @Inject constructor(private val remoteConfig: RemoteConfigManager) {{
    fun getVariant(experimentId: String) = remoteConfig.getString("exp_$experimentId", "control")
    fun isInExperiment(experimentId: String) = true
}}

class ConsentManager @Inject constructor(private val prefs: PreferencesManager) {{
    fun hasAnalyticsConsent() = true
    fun hasAdsConsent() = false
    fun updateConsent(analytics: Boolean, ads: Boolean) {{}}
}}

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
""")

# --- core/storage/StorageServices.kt ---
write(f"{BASE}/core/storage/StorageServices.kt", f"""package {PKG}.core.storage

import javax.inject.Inject

interface DatabaseManager {{
    fun query(table: String, where: String = ""): List<Map<String, Any>>
    fun insert(table: String, values: Map<String, Any>): Long
    fun update(table: String, values: Map<String, Any>, where: String): Int
    fun delete(table: String, where: String): Int
    fun transaction(block: () -> Unit)
}}

interface PreferencesManager {{
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun putLong(key: String, value: Long)
    fun getLong(key: String, default: Long = 0): Long
    fun remove(key: String)
}}

interface SecureStorage {{
    fun put(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
}}

interface CacheManager {{
    fun get(key: String): Any?
    fun put(key: String, value: Any, ttlMs: Long = 300_000)
    fun evict(key: String)
    fun clear()
}}

class RealDatabaseManager @Inject constructor() : DatabaseManager {{
    override fun query(table: String, where: String) = emptyList<Map<String, Any>>()
    override fun insert(table: String, values: Map<String, Any>) = 1L
    override fun update(table: String, values: Map<String, Any>, where: String) = 1
    override fun delete(table: String, where: String) = 1
    override fun transaction(block: () -> Unit) {{ block() }}
}}

class RealPreferencesManager @Inject constructor() : PreferencesManager {{
    private val store = mutableMapOf<String, Any>()
    override fun putString(key: String, value: String) {{ store[key] = value }}
    override fun getString(key: String) = store[key] as? String
    override fun putBoolean(key: String, value: Boolean) {{ store[key] = value }}
    override fun getBoolean(key: String, default: Boolean) = store[key] as? Boolean ?: default
    override fun putLong(key: String, value: Long) {{ store[key] = value }}
    override fun getLong(key: String, default: Long) = store[key] as? Long ?: default
    override fun remove(key: String) {{ store.remove(key) }}
}}

class RealSecureStorage @Inject constructor() : SecureStorage {{
    private val store = mutableMapOf<String, String>()
    override fun put(key: String, value: String) {{ store[key] = value }}
    override fun get(key: String) = store[key]
    override fun remove(key: String) {{ store.remove(key) }}
}}

class RealCacheManager @Inject constructor(private val prefs: PreferencesManager) : CacheManager {{
    private val memoryCache = mutableMapOf<String, CacheEntry>()
    override fun get(key: String): Any? = memoryCache[key]?.takeIf {{ !it.isExpired() }}?.value
    override fun put(key: String, value: Any, ttlMs: Long) {{ memoryCache[key] = CacheEntry(value, System.currentTimeMillis() + ttlMs) }}
    override fun evict(key: String) {{ memoryCache.remove(key) }}
    override fun clear() {{ memoryCache.clear() }}
}}

class FileManager @Inject constructor() {{
    fun read(path: String): ByteArray = ByteArray(0)
    fun write(path: String, data: ByteArray) {{}}
    fun delete(path: String) = true
    fun exists(path: String) = false
}}

class DownloadManager @Inject constructor(private val fileManager: FileManager) {{
    fun download(url: String, destination: String) = destination
    fun cancelDownload(id: String) {{}}
}}

data class CacheEntry(val value: Any, val expiresAt: Long) {{
    fun isExpired() = System.currentTimeMillis() > expiresAt
}}
""")

# --- core/config/ConfigServices.kt ---
write(f"{BASE}/core/config/ConfigServices.kt", f"""package {PKG}.core.config

import {PKG}.core.analytics.RemoteConfigManager
import {PKG}.core.storage.PreferencesManager
import javax.inject.Inject

class FeatureFlagManager @Inject constructor(private val remoteConfig: RemoteConfigManager) {{
    fun isEnabled(flag: String) = remoteConfig.getBoolean("ff_$flag", false)
    fun getValue(flag: String) = remoteConfig.getString("ff_$flag", "")
}}

class AppConfigProvider @Inject constructor(private val remoteConfig: RemoteConfigManager) {{
    fun getApiVersion() = remoteConfig.getString("api_version", "v3")
    fun getMinAppVersion() = remoteConfig.getString("min_app_version", "1.0.0")
    fun getMaxCartItems() = remoteConfig.getLong("max_cart_items", 50).toInt()
}}

class ThemeManager @Inject constructor(private val prefs: PreferencesManager) {{
    fun isDarkMode() = prefs.getBoolean("dark_mode", false)
    fun setDarkMode(enabled: Boolean) {{ prefs.putBoolean("dark_mode", enabled) }}
}}

class LocaleManager @Inject constructor(private val prefs: PreferencesManager) {{
    fun getCurrentLocale() = prefs.getString("locale") ?: "en"
    fun setLocale(locale: String) {{ prefs.putString("locale", locale) }}
    fun getSupportedLocales() = listOf("en", "es", "fr", "de", "ja", "zh")
}}

class EnvironmentManager @Inject constructor() {{
    fun getEnvironment() = "production"
    fun getBaseUrl() = "https://api.shopapp.com"
    fun isDebug() = false
}}
""")

# --- core/logging/LoggingServices.kt ---
write(f"{BASE}/core/logging/LoggingServices.kt", f"""package {PKG}.core.logging

import {PKG}.core.analytics.CrashReporter
import javax.inject.Inject

interface AppLogger {{
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warn(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}}

class RealAppLogger @Inject constructor(private val crashReporter: CrashReporter) : AppLogger {{
    override fun debug(tag: String, message: String) {{}}
    override fun info(tag: String, message: String) {{}}
    override fun warn(tag: String, message: String) {{}}
    override fun error(tag: String, message: String, throwable: Throwable?) {{ throwable?.let {{ crashReporter.report(it) }} }}
}}

class AuditLogger @Inject constructor(private val appLogger: AppLogger) {{
    fun logUserAction(action: String, details: Map<String, Any> = emptyMap()) {{ appLogger.info("AUDIT", "$action: $details") }}
    fun logSecurityEvent(event: String) {{ appLogger.warn("SECURITY", event) }}
}}
""")

# --- core/image/ImageServices.kt ---
write(f"{BASE}/core/image/ImageServices.kt", f"""package {PKG}.core.image

import {PKG}.core.network.HttpClient
import {PKG}.core.storage.CacheManager
import javax.inject.Inject

class ImageLoader @Inject constructor(private val httpClient: HttpClient, private val cacheManager: CacheManager) {{
    fun load(url: String): ByteArray = cacheManager.get(url) as? ByteArray ?: ByteArray(0)
}}

class ImageProcessor @Inject constructor() {{
    fun resize(data: ByteArray, width: Int, height: Int): ByteArray = data
    fun compress(data: ByteArray, quality: Int): ByteArray = data
}}

class ThumbnailGenerator @Inject constructor(private val imageProcessor: ImageProcessor) {{
    fun generate(data: ByteArray, size: Int): ByteArray = imageProcessor.resize(data, size, size)
}}
""")

# --- core/notification/NotificationServices.kt ---
write(f"{BASE}/core/notification/NotificationServices.kt", f"""package {PKG}.core.notification

import {PKG}.core.auth.SessionManager
import {PKG}.core.storage.PreferencesManager
import {PKG}.core.config.FeatureFlagManager
import {PKG}.core.analytics.AnalyticsTracker
import javax.inject.Inject

class NotificationManager @Inject constructor(private val prefs: PreferencesManager) {{
    fun isEnabled() = prefs.getBoolean("notifications_enabled", true)
    fun setEnabled(enabled: Boolean) {{ prefs.putBoolean("notifications_enabled", enabled) }}
    fun scheduleLocal(title: String, body: String, delayMs: Long) {{}}
    fun cancelAll() {{}}
}}

class PushTokenManager @Inject constructor(private val sessionManager: SessionManager) {{
    fun registerToken(token: String) {{}}
    fun unregisterToken() {{}}
    fun getToken(): String? = null
}}

class DeepLinkHandler @Inject constructor() {{
    fun handle(uri: String) = DeepLinkResult(
        when {{
            uri.contains("/product/") -> "product_detail"
            uri.contains("/order/") -> "order_detail"
            else -> "home"
        }},
        emptyMap()
    )
}}

class InAppMessageManager @Inject constructor(
    private val featureFlagManager: FeatureFlagManager,
    private val analyticsTracker: AnalyticsTracker
) {{
    fun showBanner(message: String, type: String) {{ analyticsTracker.track("banner_shown", mapOf("type" to type)) }}
}}

data class DeepLinkResult(val destination: String, val params: Map<String, String>)
""")

# --- core/location/LocationServices.kt ---
write(f"{BASE}/core/location/LocationServices.kt", f"""package {PKG}.core.location

import {PKG}.core.storage.PreferencesManager
import javax.inject.Inject

class LocationManager @Inject constructor(private val prefs: PreferencesManager) {{
    fun getLastKnownLocation(): Location? = null
    fun requestLocationUpdate() {{}}
}}

class GeocodingService @Inject constructor() {{
    fun getAddress(lat: Double, lng: Double) = "123 Main St, San Francisco, CA"
    fun getCoordinates(address: String) = Location(37.7749, -122.4194)
}}

class StoreLocator @Inject constructor(private val locationManager: LocationManager, private val geocoding: GeocodingService) {{
    fun findNearbyStores(radiusKm: Double = 10.0) = listOf(Store("Store 1", 37.78, -122.41, 1.2))
}}

data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
""")

print("Core layer written")

# ═══════════════════════════════════════
# STEP 3: DI Modules (Google's @Provides pattern)
# ═══════════════════════════════════════

write(f"{BASE}/di/NetworkModule.kt", f"""package {PKG}.di

import {PKG}.core.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {{

    @Provides @Singleton
    fun providesHttpClient(): HttpClient = HttpClient("https://api.shopapp.com", 30_000)

    @Provides @Singleton
    fun providesApiResponseParser(): ApiResponseParser = ApiResponseParser()

    @Provides @Singleton
    fun providesNetworkMonitor(): NetworkMonitor = NetworkMonitor()

    @Provides @Singleton
    fun providesRetryPolicy(): RetryPolicy = RetryPolicy(3, 1000)

    @Provides @Singleton
    fun providesSslPinningConfig(): SslPinningConfig = SslPinningConfig(listOf("sha256/abc123"))

    @Provides @Singleton
    fun providesTokenProvider(impl: RealTokenProvider): TokenProvider = impl

    @Provides @Singleton
    fun providesNetworkLogger(impl: RealNetworkLogger): NetworkLogger = impl

    @Provides @Singleton
    fun providesCachePolicy(impl: RealCachePolicy): CachePolicy = impl
}}
""")

write(f"{BASE}/di/AuthModule.kt", f"""package {PKG}.di

import {PKG}.core.auth.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AuthModule {{

    @Provides @Singleton
    fun providesAuthManager(impl: RealAuthManager): AuthManager = impl

    @Provides @Singleton
    fun providesTokenStorage(impl: RealTokenStorage): TokenStorage = impl

    @Provides @Singleton
    fun providesSessionManager(impl: RealSessionManager): SessionManager = impl
}}
""")

write(f"{BASE}/di/AnalyticsModule.kt", f"""package {PKG}.di

import {PKG}.core.analytics.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AnalyticsModule {{

    @Provides @Singleton
    fun providesAnalyticsTracker(impl: RealAnalyticsTracker): AnalyticsTracker = impl

    @Provides @Singleton
    fun providesCrashReporter(impl: RealCrashReporter): CrashReporter = impl
}}
""")

write(f"{BASE}/di/StorageModule.kt", f"""package {PKG}.di

import {PKG}.core.storage.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object StorageModule {{

    @Provides @Singleton
    fun providesDatabaseManager(impl: RealDatabaseManager): DatabaseManager = impl

    @Provides @Singleton
    fun providesPreferencesManager(impl: RealPreferencesManager): PreferencesManager = impl

    @Provides @Singleton
    fun providesSecureStorage(impl: RealSecureStorage): SecureStorage = impl

    @Provides @Singleton
    fun providesCacheManager(impl: RealCacheManager): CacheManager = impl
}}
""")

write(f"{BASE}/di/LoggingModule.kt", f"""package {PKG}.di

import {PKG}.core.logging.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LoggingModule {{

    @Provides @Singleton
    fun providesAppLogger(impl: RealAppLogger): AppLogger = impl
}}
""")

write(f"{BASE}/di/DispatchersModule.kt", f"""package {PKG}.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Module
@InstallIn(SingletonComponent::class)
internal object DispatchersModule {{

    @Provides @IoDispatcher
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides @DefaultDispatcher
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}}
""")

write(f"{BASE}/di/CoroutineScopesModule.kt", f"""package {PKG}.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopesModule {{

    @Provides @Singleton @ApplicationScope
    fun providesCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}}
""")

print("DI modules written")

# ═══════════════════════════════════════
# STEP 4: Data Layer — Repository interface + OfflineFirst impl
# ═══════════════════════════════════════

for domain in DOMAINS:
    cap = upper(domain)

    # Models (unchanged)
    write(f"{BASE}/data/{domain}/Models.kt", f"""package {PKG}.data.{domain}

data class {cap}Entity(
    val id: String, val name: String, val description: String = "",
    val createdAt: Long = System.currentTimeMillis(), val updatedAt: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(), val isActive: Boolean = true
)

data class {cap}Response(val items: List<{cap}Entity>, val totalCount: Int, val page: Int, val hasMore: Boolean)

data class {cap}Request(val query: String = "", val page: Int = 0, val pageSize: Int = 20, val sortBy: String = "createdAt", val filters: Map<String, String> = emptyMap())
""")

    # Remote data source (no @Singleton)
    write(f"{BASE}/data/{domain}/remote/{cap}RemoteDataSource.kt", f"""package {PKG}.data.{domain}.remote

import {PKG}.core.network.*
import {PKG}.data.{domain}.*
import javax.inject.Inject

class {cap}RemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {{
    fun getAll(request: {cap}Request) = {cap}Response(emptyList(), 0, request.page, false)
    fun getById(id: String) = {cap}Entity(id, "{cap} $id")
    fun create(entity: {cap}Entity) = entity.copy(id = "new_${{System.currentTimeMillis()}}")
    fun update(id: String, entity: {cap}Entity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = {cap}Response(emptyList(), 0, page, false)
}}
""")

    # Local data source (no @Singleton)
    write(f"{BASE}/data/{domain}/local/{cap}LocalDataSource.kt", f"""package {PKG}.data.{domain}.local

import {PKG}.core.storage.*
import {PKG}.data.{domain}.*
import javax.inject.Inject

class {cap}LocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {{
    fun getAll(): List<{cap}Entity> = emptyList()
    fun getById(id: String): {cap}Entity? = null
    fun save(entity: {cap}Entity) {{ databaseManager.insert("{domain}s", mapOf("id" to entity.id, "name" to entity.name)) }}
    fun saveAll(entities: List<{cap}Entity>) {{ databaseManager.transaction {{ entities.forEach {{ save(it) }} }} }}
    fun delete(id: String) {{ databaseManager.delete("{domain}s", "id = '$id'") }}
    fun clear() {{ databaseManager.delete("{domain}s", "1=1") }}
}}
""")

    # Mapper (no @Singleton)
    write(f"{BASE}/data/{domain}/mapper/{cap}Mapper.kt", f"""package {PKG}.data.{domain}.mapper

import {PKG}.data.{domain}.*
import {PKG}.domain.{domain}.*
import javax.inject.Inject

class {cap}Mapper @Inject constructor() {{
    fun toDomain(entity: {cap}Entity) = {cap}DomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<{cap}Entity>) = entities.map {{ toDomain(it) }}
    fun toEntity(domain: {cap}DomainModel) = {cap}Entity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: {cap}Response) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}}
""")

    # Repository INTERFACE
    write(f"{BASE}/data/{domain}/{cap}Repository.kt", f"""package {PKG}.data.{domain}

import {PKG}.domain.{domain}.*

interface {cap}Repository {{
    fun getAll(request: {cap}Request = {cap}Request()): PagedResult<{cap}DomainModel>
    fun getById(id: String): {cap}DomainModel?
    fun create(model: {cap}DomainModel): {cap}DomainModel
    fun update(id: String, model: {cap}DomainModel): {cap}DomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<{cap}DomainModel>
    fun clearCache()
}}
""")

    # Repository IMPLEMENTATION (OfflineFirst pattern)
    write(f"{BASE}/data/{domain}/OfflineFirst{cap}Repository.kt", f"""package {PKG}.data.{domain}

import {PKG}.data.{domain}.remote.{cap}RemoteDataSource
import {PKG}.data.{domain}.local.{cap}LocalDataSource
import {PKG}.data.{domain}.mapper.{cap}Mapper
import {PKG}.domain.{domain}.*
import {PKG}.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirst{cap}Repository @Inject constructor(
    private val remoteDataSource: {cap}RemoteDataSource,
    private val localDataSource: {cap}LocalDataSource,
    private val mapper: {cap}Mapper,
    private val logger: AppLogger
) : {cap}Repository {{

    override fun getAll(request: {cap}Request): PagedResult<{cap}DomainModel> {{
        return try {{
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        }} catch (e: Exception) {{
            logger.error("{cap}Repo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }}
    }}

    override fun getById(id: String): {cap}DomainModel? {{
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }}

    override fun create(model: {cap}DomainModel): {cap}DomainModel {{
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }}

    override fun update(id: String, model: {cap}DomainModel): {cap}DomainModel {{
        val entity = mapper.toEntity(model)
        val updated = remoteDataSource.update(id, entity)
        localDataSource.save(updated)
        return mapper.toDomain(updated)
    }}

    override fun delete(id: String): Boolean {{
        val result = remoteDataSource.delete(id)
        if (result) localDataSource.delete(id)
        return result
    }}

    override fun search(query: String, page: Int) = mapper.toPagedResult(remoteDataSource.search(query, page))
    override fun clearCache() {{ localDataSource.clear() }}
}}
""")

print("Data layer written")

# ═══════════════════════════════════════
# STEP 5: Data Module (@Provides for repositories)
# ═══════════════════════════════════════

repo_provides = ""
repo_imports = ""
for domain in DOMAINS:
    cap = upper(domain)
    repo_imports += f"import {PKG}.data.{domain}.{cap}Repository\n"
    repo_imports += f"import {PKG}.data.{domain}.OfflineFirst{cap}Repository\n"
    repo_provides += f"""
    @Provides @Singleton
    fun provides{cap}Repository(impl: OfflineFirst{cap}Repository): {cap}Repository = impl
"""

write(f"{BASE}/di/DataModule.kt", f"""package {PKG}.di

{repo_imports}import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {{
{repo_provides}}}
""")

print("Data module written")

# ═══════════════════════════════════════
# STEP 6: Domain Layer (unchanged — UseCases already correct)
# ═══════════════════════════════════════

for domain in DOMAINS:
    cap = upper(domain)

    write(f"{BASE}/domain/{domain}/{cap}DomainModel.kt", f"""package {PKG}.domain.{domain}

data class {cap}DomainModel(
    val id: String, val name: String, val description: String = "",
    val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap()
)

data class PagedResult<T>(val items: List<T>, val totalCount: Int, val page: Int, val hasMore: Boolean)
""")

    write(f"{BASE}/domain/{domain}/{cap}UseCases.kt", f"""package {PKG}.domain.{domain}

import {PKG}.data.{domain}.{cap}Repository
import {PKG}.core.analytics.AnalyticsTracker
import {PKG}.core.logging.AppLogger
import javax.inject.Inject

class Get{cap}ListUseCase @Inject constructor(
    private val repository: {cap}Repository, private val analytics: AnalyticsTracker
) {{
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<{cap}DomainModel> {{
        analytics.track("get_{domain}_list", mapOf("page" to page))
        return repository.getAll()
    }}
}}

class Get{cap}DetailUseCase @Inject constructor(
    private val repository: {cap}Repository, private val analytics: AnalyticsTracker
) {{
    fun execute(id: String): {cap}DomainModel? {{
        analytics.track("get_{domain}_detail", mapOf("id" to id))
        return repository.getById(id)
    }}
}}

class Create{cap}UseCase @Inject constructor(private val repository: {cap}Repository, private val logger: AppLogger) {{
    fun execute(model: {cap}DomainModel) = repository.create(model)
}}

class Update{cap}UseCase @Inject constructor(private val repository: {cap}Repository, private val logger: AppLogger) {{
    fun execute(id: String, model: {cap}DomainModel) = repository.update(id, model)
}}

class Delete{cap}UseCase @Inject constructor(private val repository: {cap}Repository, private val logger: AppLogger) {{
    fun execute(id: String) = repository.delete(id)
}}

class Search{cap}UseCase @Inject constructor(private val repository: {cap}Repository, private val analytics: AnalyticsTracker) {{
    fun execute(query: String, page: Int = 0) = repository.search(query, page)
}}

class Validate{cap}UseCase @Inject constructor(private val logger: AppLogger) {{
    fun execute(model: {cap}DomainModel): ValidationResult {{
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }}
}}

class Refresh{cap}CacheUseCase @Inject constructor(private val repository: {cap}Repository, private val logger: AppLogger) {{
    fun execute() {{ repository.clearCache(); repository.getAll() }}
}}

class Get{cap}CountUseCase @Inject constructor(private val repository: {cap}Repository) {{
    fun execute() = repository.getAll().totalCount
}}

class Filter{cap}UseCase @Inject constructor(private val repository: {cap}Repository) {{
    fun execute(predicate: ({cap}DomainModel) -> Boolean) = repository.getAll().items.filter(predicate)
}}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
""")

print("Domain layer written")

# ═══════════════════════════════════════
# STEP 7: Feature Layer — Real @HiltViewModel
# ═══════════════════════════════════════

# Home
write(f"{BASE}/feature/home/HomeViewModel.kt", f"""package {PKG}.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import {PKG}.domain.product.*
import {PKG}.domain.category.*
import {PKG}.domain.promotion.*
import {PKG}.domain.feed.*
import {PKG}.core.analytics.AnalyticsTracker
import {PKG}.core.config.FeatureFlagManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductList: GetProductListUseCase,
    private val getCategoryList: GetCategoryListUseCase,
    private val getPromotionList: GetPromotionListUseCase,
    private val getFeedList: GetFeedListUseCase,
    private val analytics: AnalyticsTracker,
    private val featureFlags: FeatureFlagManager
) : ViewModel() {{

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {{
        loadHome()
    }}

    fun loadHome() {{
        viewModelScope.launch {{
            _uiState.value = HomeUiState.Loading
            try {{
                analytics.screen("home")
                val products = getProductList.execute(pageSize = 10)
                val categories = getCategoryList.execute()
                val promotions = getPromotionList.execute()
                _uiState.value = HomeUiState.Success(
                    products = products.items,
                    categories = categories.items,
                    promotions = promotions.items,
                    showNewBanner = featureFlags.isEnabled("new_home_banner")
                )
            }} catch (e: Exception) {{
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }}
        }}
    }}
}}

sealed interface HomeUiState {{
    data object Loading : HomeUiState
    data class Success(
        val products: List<ProductDomainModel>,
        val categories: List<CategoryDomainModel>,
        val promotions: List<PromotionDomainModel>,
        val showNewBanner: Boolean
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}}
""")

# Helper to generate simpler ViewModels
def vm(feature, name, deps_str, imports_str, load_body, ui_state_class):
    write(f"{BASE}/feature/{feature}/{name}.kt", f"""package {PKG}.feature.{feature}

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
{imports_str}import {PKG}.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class {name} @Inject constructor(
{deps_str}    private val analytics: AnalyticsTracker
) : ViewModel() {{

    private val _uiState = MutableStateFlow<{name}UiState>({name}UiState.Loading)
    val uiState: StateFlow<{name}UiState> = _uiState.asStateFlow()

    init {{ load() }}

    fun load() {{
        viewModelScope.launch {{
            _uiState.value = {name}UiState.Loading
            try {{
                analytics.screen("{feature}")
{load_body}
            }} catch (e: Exception) {{
                _uiState.value = {name}UiState.Error(e.message ?: "Unknown error")
            }}
        }}
    }}
}}

{ui_state_class}
""")

# Search
vm("search", "SearchViewModel",
    f"    private val searchProduct: Search{upper('product')}UseCase,\n",
    f"import {PKG}.domain.product.*\n",
    f"""                val results = searchProduct.execute("", 0)
                _uiState.value = SearchViewModelUiState.Success(results.items, results.totalCount)""",
    f"""sealed interface SearchViewModelUiState {{
    data object Loading : SearchViewModelUiState
    data class Success(val results: List<ProductDomainModel>, val totalCount: Int) : SearchViewModelUiState
    data class Error(val message: String) : SearchViewModelUiState
}}""")

# ProductDetail
vm("productdetail", "ProductDetailViewModel",
    f"    private val getProductDetail: Get{upper('product')}DetailUseCase,\n    private val getReviewList: Get{upper('review')}ListUseCase,\n",
    f"import {PKG}.domain.product.*\nimport {PKG}.domain.review.*\n",
    f"""                val product = getProductDetail.execute("1")
                val reviews = getReviewList.execute()
                _uiState.value = ProductDetailViewModelUiState.Success(product, reviews.items)""",
    f"""sealed interface ProductDetailViewModelUiState {{
    data object Loading : ProductDetailViewModelUiState
    data class Success(val product: ProductDomainModel?, val reviews: List<ReviewDomainModel>) : ProductDetailViewModelUiState
    data class Error(val message: String) : ProductDetailViewModelUiState
}}""")

# Cart
vm("cart", "CartViewModel",
    f"    private val getCartList: Get{upper('cart')}ListUseCase,\n    private val deleteCart: Delete{upper('cart')}UseCase,\n",
    f"import {PKG}.domain.cart.*\n",
    f"""                val items = getCartList.execute()
                _uiState.value = CartViewModelUiState.Success(items.items, items.totalCount)""",
    f"""sealed interface CartViewModelUiState {{
    data object Loading : CartViewModelUiState
    data class Success(val items: List<CartDomainModel>, val itemCount: Int) : CartViewModelUiState
    data class Error(val message: String) : CartViewModelUiState
}}""")

# Checkout
vm("checkout", "CheckoutViewModel",
    f"    private val createOrder: Create{upper('order')}UseCase,\n    private val getAddressList: Get{upper('address')}ListUseCase,\n    private val getPaymentList: Get{upper('payment')}ListUseCase,\n",
    f"import {PKG}.domain.order.*\nimport {PKG}.domain.address.*\nimport {PKG}.domain.payment.*\n",
    f"""                val addresses = getAddressList.execute()
                val payments = getPaymentList.execute()
                _uiState.value = CheckoutViewModelUiState.Success(addresses.items, payments.items)""",
    f"""sealed interface CheckoutViewModelUiState {{
    data object Loading : CheckoutViewModelUiState
    data class Success(val addresses: List<AddressDomainModel>, val payments: List<PaymentDomainModel>) : CheckoutViewModelUiState
    data class Error(val message: String) : CheckoutViewModelUiState
}}""")

# Profile
vm("profile", "ProfileViewModel",
    f"    private val getUserDetail: Get{upper('user')}DetailUseCase,\n    private val getOrderList: Get{upper('order')}ListUseCase,\n",
    f"import {PKG}.domain.user.*\nimport {PKG}.domain.order.*\n",
    f"""                val user = getUserDetail.execute("current")
                val orders = getOrderList.execute()
                _uiState.value = ProfileViewModelUiState.Success(user?.name ?: "Guest", orders.totalCount)""",
    f"""sealed interface ProfileViewModelUiState {{
    data object Loading : ProfileViewModelUiState
    data class Success(val userName: String, val orderCount: Int) : ProfileViewModelUiState
    data class Error(val message: String) : ProfileViewModelUiState
}}""")

# Orders
vm("orders", "OrderHistoryViewModel",
    f"    private val getOrderList: Get{upper('order')}ListUseCase,\n",
    f"import {PKG}.domain.order.*\n",
    f"""                val orders = getOrderList.execute()
                _uiState.value = OrderHistoryViewModelUiState.Success(orders.items)""",
    f"""sealed interface OrderHistoryViewModelUiState {{
    data object Loading : OrderHistoryViewModelUiState
    data class Success(val orders: List<OrderDomainModel>) : OrderHistoryViewModelUiState
    data class Error(val message: String) : OrderHistoryViewModelUiState
}}""")

# Settings
vm("settings", "SettingsViewModel",
    f"    private val themeManager: {PKG}.core.config.ThemeManager,\n    private val localeManager: {PKG}.core.config.LocaleManager,\n",
    f"",
    f"""                _uiState.value = SettingsViewModelUiState.Success(
                    isDarkMode = themeManager.isDarkMode(),
                    locale = localeManager.getCurrentLocale()
                )""",
    f"""sealed interface SettingsViewModelUiState {{
    data object Loading : SettingsViewModelUiState
    data class Success(val isDarkMode: Boolean, val locale: String) : SettingsViewModelUiState
    data class Error(val message: String) : SettingsViewModelUiState
}}""")

# Chat
vm("chat", "ChatViewModel",
    f"    private val getChatList: Get{upper('chat')}ListUseCase,\n",
    f"import {PKG}.domain.chat.*\n",
    f"""                val chats = getChatList.execute()
                _uiState.value = ChatViewModelUiState.Success(chats.items)""",
    f"""sealed interface ChatViewModelUiState {{
    data object Loading : ChatViewModelUiState
    data class Success(val conversations: List<ChatDomainModel>) : ChatViewModelUiState
    data class Error(val message: String) : ChatViewModelUiState
}}""")

# Notifications
vm("notifications", "NotificationsViewModel",
    f"    private val deepLinkHandler: {PKG}.core.notification.DeepLinkHandler,\n",
    f"",
    f"""                _uiState.value = NotificationsViewModelUiState.Success(emptyList())""",
    f"""sealed interface NotificationsViewModelUiState {{
    data object Loading : NotificationsViewModelUiState
    data class Success(val notifications: List<String>) : NotificationsViewModelUiState
    data class Error(val message: String) : NotificationsViewModelUiState
}}""")

# Onboarding
vm("onboarding", "OnboardingViewModel",
    f"    private val authManager: {PKG}.core.auth.AuthManager,\n",
    f"",
    f"""                _uiState.value = OnboardingViewModelUiState.Success(authManager.isLoggedIn())""",
    f"""sealed interface OnboardingViewModelUiState {{
    data object Loading : OnboardingViewModelUiState
    data class Success(val isLoggedIn: Boolean) : OnboardingViewModelUiState
    data class Error(val message: String) : OnboardingViewModelUiState
}}""")

# Reviews
vm("reviews", "ReviewsViewModel",
    f"    private val getReviewList: Get{upper('review')}ListUseCase,\n",
    f"import {PKG}.domain.review.*\n",
    f"""                val reviews = getReviewList.execute()
                _uiState.value = ReviewsViewModelUiState.Success(reviews.items)""",
    f"""sealed interface ReviewsViewModelUiState {{
    data object Loading : ReviewsViewModelUiState
    data class Success(val reviews: List<ReviewDomainModel>) : ReviewsViewModelUiState
    data class Error(val message: String) : ReviewsViewModelUiState
}}""")

# Wishlist
vm("wishlist", "WishlistViewModel",
    f"    private val getWishlistList: Get{upper('wishlist')}ListUseCase,\n",
    f"import {PKG}.domain.wishlist.*\n",
    f"""                val items = getWishlistList.execute()
                _uiState.value = WishlistViewModelUiState.Success(items.items)""",
    f"""sealed interface WishlistViewModelUiState {{
    data object Loading : WishlistViewModelUiState
    data class Success(val items: List<WishlistDomainModel>) : WishlistViewModelUiState
    data class Error(val message: String) : WishlistViewModelUiState
}}""")

print("Feature layer (ViewModels) written")

# ═══════════════════════════════════════
# STEP 8: Clean up old files
# ═══════════════════════════════════════

# Remove old BenchmarkEntryPoint if exists
ep = f"{BASE}/di/BenchmarkEntryPoint.kt"
if os.path.exists(ep):
    os.remove(ep)
    print("Removed BenchmarkEntryPoint.kt")

# Remove old HiltLargeModule.kt from module/ dir
old_module = f"{BASE}/module/HiltLargeModule.kt"
if os.path.exists(old_module):
    os.remove(old_module)
    print("Removed old HiltLargeModule.kt")

print("\n✅ benchmark-hilt-large rewritten as production-quality Hilt app")
print(f"  Domains: {len(DOMAINS)}")
print(f"  DI Modules: 8 (@Provides pattern)")
print(f"  ViewModels: 12 (@HiltViewModel, extends ViewModel())")
print(f"  Repository interfaces: {len(DOMAINS)}")
print(f"  OfflineFirst implementations: {len(DOMAINS)}")
