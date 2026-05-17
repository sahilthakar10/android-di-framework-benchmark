#!/usr/bin/env python3
"""
Rewrites benchmark-koin-large as production-quality Koin app.
Same architecture as hilt-large, only DI wiring changes.
"""

import os
import shutil

BASE = "/Users/sahilthakar/AndroidStudioProjects/BenchMarking/benchmark-koin-large/src/main/kotlin/com/codeint/shopapp/koin"
PKG = "com.codeint.shopapp.koin"

DOMAINS = ["product", "user", "cart", "order", "payment", "chat", "search",
           "review", "category", "address", "wishlist", "promotion", "shipping", "feed"]

def upper(s):
    return s[0].upper() + s[1:]

def write(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(content)

# Clean old source
if os.path.exists(BASE):
    shutil.rmtree(BASE)
os.makedirs(BASE)

# ═══════════════════════════════════════
# CORE LAYER — Same interfaces as Hilt, no annotations
# ═══════════════════════════════════════

# core/network
write(f"{BASE}/core/network/NetworkModule.kt", f"""package {PKG}.core.network

class HttpClient(val baseUrl: String, val timeout: Long)
class ApiResponseParser
class NetworkMonitor
class RetryPolicy(val maxRetries: Int, val backoffMs: Long)
class SslPinningConfig(val pins: List<String>)
""")

write(f"{BASE}/core/network/ApiServices.kt", f"""package {PKG}.core.network

interface TokenProvider {{ fun getAccessToken(): String; fun getRefreshToken(): String; fun isExpired(): Boolean }}
interface NetworkLogger {{ fun logRequest(method: String, url: String, status: Int); fun logError(url: String, error: String) }}
interface CachePolicy {{ fun isCacheable(url: String): Boolean; fun getTtl(url: String): Long }}

class RealTokenProvider : TokenProvider {{
    override fun getAccessToken() = "mock_access_token"
    override fun getRefreshToken() = "mock_refresh_token"
    override fun isExpired() = false
}}
class RealNetworkLogger : NetworkLogger {{
    override fun logRequest(method: String, url: String, status: Int) {{}}
    override fun logError(url: String, error: String) {{}}
}}
class RealCachePolicy : CachePolicy {{
    override fun isCacheable(url: String) = url.contains("/products")
    override fun getTtl(url: String) = 300_000L
}}
class AuthInterceptor(private val tokenProvider: TokenProvider) {{ fun intercept(url: String) = mapOf("Auth" to tokenProvider.getAccessToken()) }}
class CacheInterceptor(private val cachePolicy: CachePolicy) {{ fun shouldCache(url: String) = cachePolicy.isCacheable(url) }}
class LoggingInterceptor(private val networkLogger: NetworkLogger) {{ fun log(m: String, u: String, s: Int) {{ networkLogger.logRequest(m, u, s) }} }}
class RateLimiter(private val config: RetryPolicy) {{ fun shouldThrottle(endpoint: String) = false }}
class WebSocketManager(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {{ fun connect(ch: String) {{}}; fun send(msg: String) {{}} }}
class GraphQLClient(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) {{ fun query(q: String) = "{{}}"; fun mutate(m: String) = "{{}}" }}
class FileUploader(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {{ fun upload(f: String, e: String) = "id_123" }}
""")

# core/auth
write(f"{BASE}/core/auth/AuthServices.kt", f"""package {PKG}.core.auth

import {PKG}.core.analytics.AnalyticsTracker
import {PKG}.core.storage.PreferencesManager
import {PKG}.core.storage.SecureStorage
import {PKG}.core.network.HttpClient
import {PKG}.core.network.TokenProvider

interface AuthManager {{ fun login(email: String, password: String): Boolean; fun logout(); fun isLoggedIn(): Boolean }}
interface TokenStorage {{ fun saveTokens(a: String, r: String); fun getAccessToken(): String?; fun hasValidToken(): Boolean; fun clear() }}
interface SessionManager {{ fun startSession(userId: String); fun getCurrentUserId(): String?; fun invalidate() }}

class RealAuthManager(private val tokenStorage: TokenStorage, private val sessionManager: SessionManager, private val analytics: AnalyticsTracker) : AuthManager {{
    override fun login(email: String, password: String): Boolean {{ analytics.track("login"); return true }}
    override fun logout() {{ tokenStorage.clear(); sessionManager.invalidate() }}
    override fun isLoggedIn() = tokenStorage.hasValidToken()
}}
class RealTokenStorage(private val secureStorage: SecureStorage) : TokenStorage {{
    override fun saveTokens(a: String, r: String) {{ secureStorage.put("token", a) }}
    override fun getAccessToken() = secureStorage.get("token")
    override fun hasValidToken() = getAccessToken() != null
    override fun clear() {{ secureStorage.remove("token") }}
}}
class RealSessionManager(private val prefs: PreferencesManager) : SessionManager {{
    override fun startSession(userId: String) {{ prefs.putString("user", userId) }}
    override fun getCurrentUserId() = prefs.getString("user")
    override fun invalidate() {{ prefs.remove("user") }}
}}
class BiometricAuthProvider(private val secureStorage: SecureStorage) {{ fun isAvailable() = true }}
class OAuthManager(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {{ fun authorizeWithGoogle() = true }}
class PasswordValidator {{ fun validate(password: String) = password.length >= 8 }}
class TwoFactorAuthManager(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {{ fun requestCode(phone: String) = true }}
""")

# core/analytics
write(f"{BASE}/core/analytics/AnalyticsServices.kt", f"""package {PKG}.core.analytics

import {PKG}.core.storage.PreferencesManager

interface AnalyticsTracker {{ fun track(event: String, params: Map<String, Any> = emptyMap()); fun setUserId(userId: String); fun screen(name: String) }}
interface CrashReporter {{ fun report(t: Throwable); fun log(msg: String); fun setUserId(userId: String) }}

class EventBus {{ private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>(); fun post(e: AnalyticsEvent) {{ listeners.forEach {{ it(e) }} }}; fun subscribe(l: (AnalyticsEvent) -> Unit) {{ listeners.add(l) }} }}
class UserPropertyTracker {{ private val props = mutableMapOf<String, Any>(); fun set(k: String, v: Any) {{ props[k] = v }}; fun get(k: String) = props[k] }}
class RemoteConfigManager {{ fun getString(k: String, d: String) = d; fun getBoolean(k: String, d: Boolean) = d; fun getLong(k: String, d: Long) = d }}

class RealAnalyticsTracker(private val eventBus: EventBus, private val userPropertyTracker: UserPropertyTracker) : AnalyticsTracker {{
    override fun track(event: String, params: Map<String, Any>) {{ eventBus.post(AnalyticsEvent(event, params)) }}
    override fun setUserId(userId: String) {{ userPropertyTracker.set("user_id", userId) }}
    override fun screen(name: String) {{ track("screen_view", mapOf("screen" to name)) }}
}}
class RealCrashReporter : CrashReporter {{ override fun report(t: Throwable) {{}}; override fun log(msg: String) {{}}; override fun setUserId(userId: String) {{}} }}
class PerformanceMonitor {{ fun startTrace(name: String) = TraceHandle(name, System.nanoTime()) }}
class ABTestManager(private val rc: RemoteConfigManager) {{ fun getVariant(id: String) = rc.getString("exp_$id", "control") }}
class ConsentManager(private val prefs: PreferencesManager) {{ fun hasAnalyticsConsent() = true; fun hasAdsConsent() = false }}

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
""")

# core/storage
write(f"{BASE}/core/storage/StorageServices.kt", f"""package {PKG}.core.storage

interface DatabaseManager {{ fun query(table: String, where: String = ""): List<Map<String, Any>>; fun insert(table: String, values: Map<String, Any>): Long; fun update(table: String, values: Map<String, Any>, where: String): Int; fun delete(table: String, where: String): Int; fun transaction(block: () -> Unit) }}
interface PreferencesManager {{ fun putString(k: String, v: String); fun getString(k: String): String?; fun putBoolean(k: String, v: Boolean); fun getBoolean(k: String, default: Boolean = false): Boolean; fun putLong(k: String, v: Long); fun getLong(k: String, default: Long = 0): Long; fun remove(k: String) }}
interface SecureStorage {{ fun put(k: String, v: String); fun get(k: String): String?; fun remove(k: String) }}
interface CacheManager {{ fun get(k: String): Any?; fun put(k: String, v: Any, ttlMs: Long = 300_000); fun evict(k: String); fun clear() }}

class RealDatabaseManager : DatabaseManager {{
    override fun query(table: String, where: String) = emptyList<Map<String, Any>>()
    override fun insert(table: String, values: Map<String, Any>) = 1L
    override fun update(table: String, values: Map<String, Any>, where: String) = 1
    override fun delete(table: String, where: String) = 1
    override fun transaction(block: () -> Unit) {{ block() }}
}}
class RealPreferencesManager : PreferencesManager {{
    private val store = mutableMapOf<String, Any>()
    override fun putString(k: String, v: String) {{ store[k] = v }}; override fun getString(k: String) = store[k] as? String
    override fun putBoolean(k: String, v: Boolean) {{ store[k] = v }}; override fun getBoolean(k: String, default: Boolean) = store[k] as? Boolean ?: default
    override fun putLong(k: String, v: Long) {{ store[k] = v }}; override fun getLong(k: String, default: Long) = store[k] as? Long ?: default
    override fun remove(k: String) {{ store.remove(k) }}
}}
class RealSecureStorage : SecureStorage {{ private val store = mutableMapOf<String, String>(); override fun put(k: String, v: String) {{ store[k] = v }}; override fun get(k: String) = store[k]; override fun remove(k: String) {{ store.remove(k) }} }}
class RealCacheManager(private val prefs: PreferencesManager) : CacheManager {{
    private val cache = mutableMapOf<String, CacheEntry>()
    override fun get(k: String): Any? = cache[k]?.takeIf {{ !it.isExpired() }}?.value
    override fun put(k: String, v: Any, ttlMs: Long) {{ cache[k] = CacheEntry(v, System.currentTimeMillis() + ttlMs) }}
    override fun evict(k: String) {{ cache.remove(k) }}; override fun clear() {{ cache.clear() }}
}}
class FileManager {{ fun read(p: String) = ByteArray(0); fun write(p: String, d: ByteArray) {{}}; fun delete(p: String) = true }}
class DownloadManager(private val fm: FileManager) {{ fun download(url: String, dest: String) = dest }}

data class CacheEntry(val value: Any, val expiresAt: Long) {{ fun isExpired() = System.currentTimeMillis() > expiresAt }}
""")

# core/config
write(f"{BASE}/core/config/ConfigServices.kt", f"""package {PKG}.core.config

import {PKG}.core.analytics.RemoteConfigManager
import {PKG}.core.storage.PreferencesManager

class FeatureFlagManager(private val rc: RemoteConfigManager) {{ fun isEnabled(flag: String) = rc.getBoolean("ff_$flag", false) }}
class AppConfigProvider(private val rc: RemoteConfigManager) {{ fun getApiVersion() = rc.getString("api_version", "v3") }}
class ThemeManager(private val prefs: PreferencesManager) {{ fun isDarkMode() = prefs.getBoolean("dark_mode", false); fun setDarkMode(e: Boolean) {{ prefs.putBoolean("dark_mode", e) }} }}
class LocaleManager(private val prefs: PreferencesManager) {{ fun getCurrentLocale() = prefs.getString("locale") ?: "en"; fun setLocale(l: String) {{ prefs.putString("locale", l) }} }}
class EnvironmentManager {{ fun getEnvironment() = "production"; fun getBaseUrl() = "https://api.shopapp.com" }}
""")

# core/logging
write(f"{BASE}/core/logging/LoggingServices.kt", f"""package {PKG}.core.logging

import {PKG}.core.analytics.CrashReporter

interface AppLogger {{ fun debug(tag: String, msg: String); fun info(tag: String, msg: String); fun warn(tag: String, msg: String); fun error(tag: String, msg: String, t: Throwable? = null) }}
class RealAppLogger(private val crashReporter: CrashReporter) : AppLogger {{
    override fun debug(tag: String, msg: String) {{}}; override fun info(tag: String, msg: String) {{}}
    override fun warn(tag: String, msg: String) {{}}; override fun error(tag: String, msg: String, t: Throwable?) {{ t?.let {{ crashReporter.report(it) }} }}
}}
class AuditLogger(private val appLogger: AppLogger) {{ fun logUserAction(a: String) {{ appLogger.info("AUDIT", a) }} }}
""")

# core/image
write(f"{BASE}/core/image/ImageServices.kt", f"""package {PKG}.core.image

import {PKG}.core.network.HttpClient
import {PKG}.core.storage.CacheManager

class ImageLoader(private val httpClient: HttpClient, private val cacheManager: CacheManager) {{ fun load(url: String) = ByteArray(0) }}
class ImageProcessor {{ fun resize(data: ByteArray, w: Int, h: Int) = data }}
class ThumbnailGenerator(private val ip: ImageProcessor) {{ fun generate(data: ByteArray, size: Int) = ip.resize(data, size, size) }}
""")

# core/notification
write(f"{BASE}/core/notification/NotificationServices.kt", f"""package {PKG}.core.notification

import {PKG}.core.auth.SessionManager
import {PKG}.core.storage.PreferencesManager
import {PKG}.core.config.FeatureFlagManager
import {PKG}.core.analytics.AnalyticsTracker

class NotificationManager(private val prefs: PreferencesManager) {{ fun isEnabled() = prefs.getBoolean("notif", true); fun setEnabled(e: Boolean) {{ prefs.putBoolean("notif", e) }} }}
class PushTokenManager(private val sm: SessionManager) {{ fun registerToken(t: String) {{}}; fun getToken(): String? = null }}
class DeepLinkHandler {{ fun handle(uri: String) = DeepLinkResult(if (uri.contains("/product/")) "product" else "home", emptyMap()) }}
class InAppMessageManager(private val ff: FeatureFlagManager, private val at: AnalyticsTracker) {{ fun showBanner(msg: String, type: String) {{ at.track("banner", mapOf("type" to type)) }} }}
data class DeepLinkResult(val destination: String, val params: Map<String, String>)
""")

# core/location
write(f"{BASE}/core/location/LocationServices.kt", f"""package {PKG}.core.location

import {PKG}.core.storage.PreferencesManager

class LocationManager(private val prefs: PreferencesManager) {{ fun getLastKnownLocation(): Location? = null }}
class GeocodingService {{ fun getAddress(lat: Double, lng: Double) = "123 Main St"; fun getCoordinates(addr: String) = Location(37.77, -122.41) }}
class StoreLocator(private val lm: LocationManager, private val gc: GeocodingService) {{ fun findNearbyStores() = listOf(Store("Store 1", 37.78, -122.41, 1.2)) }}
data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
""")

print("Core layer written")

# ═══════════════════════════════════════
# DATA LAYER — Same structure, no DI annotations
# ═══════════════════════════════════════

for domain in DOMAINS:
    cap = upper(domain)

    write(f"{BASE}/data/{domain}/Models.kt", f"""package {PKG}.data.{domain}

data class {cap}Entity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class {cap}Response(val items: List<{cap}Entity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class {cap}Request(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
""")

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

    write(f"{BASE}/data/{domain}/remote/{cap}RemoteDataSource.kt", f"""package {PKG}.data.{domain}.remote

import {PKG}.core.network.*
import {PKG}.data.{domain}.*

class {cap}RemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {{
    fun getAll(req: {cap}Request) = {cap}Response(emptyList(), 0, req.page, false)
    fun getById(id: String) = {cap}Entity(id, "{cap} $id")
    fun create(e: {cap}Entity) = e.copy(id = "new_${{System.currentTimeMillis()}}")
    fun update(id: String, e: {cap}Entity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = {cap}Response(emptyList(), 0, page, false)
}}
""")

    write(f"{BASE}/data/{domain}/local/{cap}LocalDataSource.kt", f"""package {PKG}.data.{domain}.local

import {PKG}.core.storage.*
import {PKG}.data.{domain}.*

class {cap}LocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {{
    fun getAll(): List<{cap}Entity> = emptyList()
    fun getById(id: String): {cap}Entity? = null
    fun save(entity: {cap}Entity) {{ db.insert("{domain}s", mapOf("id" to entity.id, "name" to entity.name)) }}
    fun saveAll(entities: List<{cap}Entity>) {{ db.transaction {{ entities.forEach {{ save(it) }} }} }}
    fun delete(id: String) {{ db.delete("{domain}s", "id = '$id'") }}
    fun clear() {{ db.delete("{domain}s", "1=1") }}
}}
""")

    write(f"{BASE}/data/{domain}/mapper/{cap}Mapper.kt", f"""package {PKG}.data.{domain}.mapper

import {PKG}.data.{domain}.*
import {PKG}.domain.{domain}.*

class {cap}Mapper {{
    fun toDomain(e: {cap}Entity) = {cap}DomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<{cap}Entity>) = entities.map {{ toDomain(it) }}
    fun toEntity(d: {cap}DomainModel) = {cap}Entity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: {cap}Response) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}}
""")

    write(f"{BASE}/data/{domain}/OfflineFirst{cap}Repository.kt", f"""package {PKG}.data.{domain}

import {PKG}.data.{domain}.remote.{cap}RemoteDataSource
import {PKG}.data.{domain}.local.{cap}LocalDataSource
import {PKG}.data.{domain}.mapper.{cap}Mapper
import {PKG}.domain.{domain}.*
import {PKG}.core.logging.AppLogger

class OfflineFirst{cap}Repository(
    private val remote: {cap}RemoteDataSource,
    private val local: {cap}LocalDataSource,
    private val mapper: {cap}Mapper,
    private val logger: AppLogger
) : {cap}Repository {{
    override fun getAll(request: {cap}Request): PagedResult<{cap}DomainModel> {{
        return try {{ val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }}
        catch (e: Exception) {{ logger.error("{cap}Repo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }}
    }}
    override fun getById(id: String) = local.getById(id)?.let {{ mapper.toDomain(it) }} ?: remote.getById(id).let {{ local.save(it); mapper.toDomain(it) }}
    override fun create(model: {cap}DomainModel) = remote.create(mapper.toEntity(model)).let {{ local.save(it); mapper.toDomain(it) }}
    override fun update(id: String, model: {cap}DomainModel) = remote.update(id, mapper.toEntity(model)).let {{ local.save(it); mapper.toDomain(it) }}
    override fun delete(id: String): Boolean {{ val r = remote.delete(id); if (r) local.delete(id); return r }}
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() {{ local.clear() }}
}}
""")

print("Data layer written")

# ═══════════════════════════════════════
# DOMAIN LAYER — Same as Hilt, no annotations
# ═══════════════════════════════════════

for domain in DOMAINS:
    cap = upper(domain)

    write(f"{BASE}/domain/{domain}/{cap}DomainModel.kt", f"""package {PKG}.domain.{domain}

data class {cap}DomainModel(val id: String, val name: String, val description: String = "", val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class PagedResult<T>(val items: List<T>, val totalCount: Int, val page: Int, val hasMore: Boolean)
""")

    write(f"{BASE}/domain/{domain}/{cap}UseCases.kt", f"""package {PKG}.domain.{domain}

import {PKG}.data.{domain}.{cap}Repository
import {PKG}.core.analytics.AnalyticsTracker
import {PKG}.core.logging.AppLogger

class Get{cap}ListUseCase(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{
    fun execute(page: Int = 0) = repo.getAll().also {{ analytics.track("get_{domain}_list") }}
}}
class Get{cap}DetailUseCase(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{
    fun execute(id: String) = repo.getById(id).also {{ analytics.track("get_{domain}_detail") }}
}}
class Create{cap}UseCase(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(m: {cap}DomainModel) = repo.create(m) }}
class Update{cap}UseCase(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(id: String, m: {cap}DomainModel) = repo.update(id, m) }}
class Delete{cap}UseCase(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(id: String) = repo.delete(id) }}
class Search{cap}UseCase(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{ fun execute(q: String, p: Int = 0) = repo.search(q, p) }}
class Validate{cap}UseCase(private val logger: AppLogger) {{
    fun execute(m: {cap}DomainModel): ValidationResult {{ val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }}
}}
class Refresh{cap}CacheUseCase(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute() {{ repo.clearCache(); repo.getAll() }} }}
class Get{cap}CountUseCase(private val repo: {cap}Repository) {{ fun execute() = repo.getAll().totalCount }}
class Filter{cap}UseCase(private val repo: {cap}Repository) {{ fun execute(p: ({cap}DomainModel) -> Boolean) = repo.getAll().items.filter(p) }}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
""")

print("Domain layer written")

# ═══════════════════════════════════════
# FEATURE LAYER — Real ViewModels
# ═══════════════════════════════════════

vms = [
    ("home", "HomeViewModel", ["GetProductListUseCase", "GetCategoryListUseCase", "GetPromotionListUseCase", "GetFeedListUseCase"], ["product", "category", "promotion", "feed"], "FeatureFlagManager"),
    ("search", "SearchViewModel", ["SearchProductUseCase"], ["product"], None),
    ("productdetail", "ProductDetailViewModel", ["GetProductDetailUseCase", "GetReviewListUseCase"], ["product", "review"], None),
    ("cart", "CartViewModel", ["GetCartListUseCase", "DeleteCartUseCase"], ["cart"], None),
    ("checkout", "CheckoutViewModel", ["CreateOrderUseCase", "GetAddressListUseCase", "GetPaymentListUseCase"], ["order", "address", "payment"], None),
    ("profile", "ProfileViewModel", ["GetUserDetailUseCase", "GetOrderListUseCase"], ["user", "order"], None),
    ("chat", "ChatViewModel", ["GetChatListUseCase"], ["chat"], None),
    ("orders", "OrderHistoryViewModel", ["GetOrderListUseCase"], ["order"], None),
    ("settings", "SettingsViewModel", [], [], "ThemeManager,LocaleManager"),
    ("notifications", "NotificationsViewModel", [], [], "DeepLinkHandler"),
    ("onboarding", "OnboardingViewModel", [], [], "AuthManager"),
    ("reviews", "ReviewsViewModel", ["GetReviewListUseCase"], ["review"], None),
    ("wishlist", "WishlistViewModel", ["GetWishlistListUseCase"], ["wishlist"], None),
]

for feature, vm_name, uc_names, domains_used, extra_dep in vms:
    imports = "import androidx.lifecycle.ViewModel\nimport androidx.lifecycle.viewModelScope\n"
    imports += f"import {PKG}.core.analytics.AnalyticsTracker\n"
    for d in domains_used:
        imports += f"import {PKG}.domain.{d}.*\n"
    if extra_dep:
        for dep in extra_dep.split(","):
            dep = dep.strip()
            if dep == "FeatureFlagManager": imports += f"import {PKG}.core.config.FeatureFlagManager\n"
            elif dep == "ThemeManager": imports += f"import {PKG}.core.config.ThemeManager\n"
            elif dep == "LocaleManager": imports += f"import {PKG}.core.config.LocaleManager\n"
            elif dep == "DeepLinkHandler": imports += f"import {PKG}.core.notification.DeepLinkHandler\n"
            elif dep == "AuthManager": imports += f"import {PKG}.core.auth.AuthManager\n"

    imports += "import kotlinx.coroutines.flow.MutableStateFlow\nimport kotlinx.coroutines.flow.StateFlow\nimport kotlinx.coroutines.flow.asStateFlow\nimport kotlinx.coroutines.launch\n"

    # Build constructor params
    params = []
    for uc in uc_names:
        param_name = uc[0].lower() + uc[1:]
        params.append(f"    private val {param_name}: {uc}")
    if extra_dep:
        for dep in extra_dep.split(","):
            dep = dep.strip()
            param_name = dep[0].lower() + dep[1:]
            params.append(f"    private val {param_name}: {dep}")
    params.append(f"    private val analytics: AnalyticsTracker")
    params_str = ",\n".join(params)

    write(f"{BASE}/feature/{feature}/{vm_name}.kt", f"""package {PKG}.feature.{feature}

{imports}
class {vm_name}(
{params_str}
) : ViewModel() {{

    private val _uiState = MutableStateFlow<{vm_name}UiState>({vm_name}UiState.Loading)
    val uiState: StateFlow<{vm_name}UiState> = _uiState.asStateFlow()

    init {{ load() }}

    fun load() {{
        viewModelScope.launch {{
            _uiState.value = {vm_name}UiState.Loading
            try {{
                analytics.screen("{feature}")
                _uiState.value = {vm_name}UiState.Success
            }} catch (e: Exception) {{
                _uiState.value = {vm_name}UiState.Error(e.message ?: "Unknown error")
            }}
        }}
    }}
}}

sealed interface {vm_name}UiState {{
    data object Loading : {vm_name}UiState
    data object Success : {vm_name}UiState
    data class Error(val message: String) : {vm_name}UiState
}}
""")

print("Feature layer (ViewModels) written")

# ═══════════════════════════════════════
# KOIN DI MODULES — Using Koin DSL
# ═══════════════════════════════════════

# Generate all Koin module definitions
module_content = f"""package {PKG}.di

import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import {PKG}.core.network.*
import {PKG}.core.auth.*
import {PKG}.core.analytics.*
import {PKG}.core.storage.*
import {PKG}.core.config.*
import {PKG}.core.logging.*
import {PKG}.core.image.*
import {PKG}.core.notification.*
import {PKG}.core.location.*
"""

for d in DOMAINS:
    c = upper(d)
    module_content += f"import {PKG}.data.{d}.*\nimport {PKG}.data.{d}.remote.*\nimport {PKG}.data.{d}.local.*\nimport {PKG}.data.{d}.mapper.*\n"
    module_content += f"import {PKG}.domain.{d}.*\n"

module_content += f"""import {PKG}.feature.home.HomeViewModel
import {PKG}.feature.search.SearchViewModel
import {PKG}.feature.productdetail.ProductDetailViewModel
import {PKG}.feature.cart.CartViewModel
import {PKG}.feature.checkout.CheckoutViewModel
import {PKG}.feature.profile.ProfileViewModel
import {PKG}.feature.chat.ChatViewModel
import {PKG}.feature.orders.OrderHistoryViewModel
import {PKG}.feature.settings.SettingsViewModel
import {PKG}.feature.notifications.NotificationsViewModel
import {PKG}.feature.onboarding.OnboardingViewModel
import {PKG}.feature.reviews.ReviewsViewModel
import {PKG}.feature.wishlist.WishlistViewModel

val coreNetworkModule = module {{
    single(createdAtStart = true) {{ HttpClient("https://api.shopapp.com", 30_000) }}
    single(createdAtStart = true) {{ ApiResponseParser() }}
    single {{ NetworkMonitor() }}
    single {{ RetryPolicy(3, 1000) }}
    single {{ SslPinningConfig(listOf("sha256/abc123")) }}
    single<TokenProvider>(createdAtStart = true) {{ RealTokenProvider() }}
    single<NetworkLogger> {{ RealNetworkLogger() }}
    single<CachePolicy> {{ RealCachePolicy() }}
    single {{ AuthInterceptor(get()) }}
    single {{ CacheInterceptor(get()) }}
    single {{ LoggingInterceptor(get()) }}
    single {{ RateLimiter(get()) }}
    single {{ WebSocketManager(get(), get()) }}
    single {{ GraphQLClient(get(), get(), get()) }}
    single {{ FileUploader(get(), get()) }}
}}

val coreStorageModule = module {{
    single<DatabaseManager>(createdAtStart = true) {{ RealDatabaseManager() }}
    single<PreferencesManager>(createdAtStart = true) {{ RealPreferencesManager() }}
    single<SecureStorage>(createdAtStart = true) {{ RealSecureStorage() }}
    single<CacheManager>(createdAtStart = true) {{ RealCacheManager(get()) }}
    single {{ FileManager() }}
    single {{ DownloadManager(get()) }}
}}

val coreAuthModule = module {{
    single<TokenStorage>(createdAtStart = true) {{ RealTokenStorage(get()) }}
    single<SessionManager>(createdAtStart = true) {{ RealSessionManager(get()) }}
    single<AuthManager>(createdAtStart = true) {{ RealAuthManager(get(), get(), get()) }}
    single {{ BiometricAuthProvider(get()) }}
    single {{ OAuthManager(get(), get()) }}
    single {{ PasswordValidator() }}
    single {{ TwoFactorAuthManager(get(), get()) }}
}}

val coreAnalyticsModule = module {{
    single(createdAtStart = true) {{ EventBus() }}
    single(createdAtStart = true) {{ UserPropertyTracker() }}
    single(createdAtStart = true) {{ RemoteConfigManager() }}
    single<AnalyticsTracker>(createdAtStart = true) {{ RealAnalyticsTracker(get(), get()) }}
    single<CrashReporter>(createdAtStart = true) {{ RealCrashReporter() }}
    single {{ PerformanceMonitor() }}
    single {{ ABTestManager(get()) }}
    single {{ ConsentManager(get()) }}
}}

val coreConfigModule = module {{
    single(createdAtStart = true) {{ FeatureFlagManager(get()) }}
    single {{ AppConfigProvider(get()) }}
    single {{ ThemeManager(get()) }}
    single {{ LocaleManager(get()) }}
    single {{ EnvironmentManager() }}
}}

val coreLoggingModule = module {{
    single<AppLogger>(createdAtStart = true) {{ RealAppLogger(get()) }}
    single {{ AuditLogger(get()) }}
}}

val coreImageModule = module {{
    single {{ ImageLoader(get(), get()) }}
    single {{ ImageProcessor() }}
    single {{ ThumbnailGenerator(get()) }}
}}

val coreNotificationModule = module {{
    single {{ NotificationManager(get()) }}
    single {{ PushTokenManager(get()) }}
    single {{ DeepLinkHandler() }}
    single {{ InAppMessageManager(get(), get()) }}
}}

val coreLocationModule = module {{
    single {{ LocationManager(get()) }}
    single {{ GeocodingService() }}
    single {{ StoreLocator(get(), get()) }}
}}

val dataModule = module {{
"""

for d in DOMAINS:
    c = upper(d)
    module_content += f"""    single {{ {c}RemoteDataSource(get(), get(), get(), get()) }}
    single {{ {c}LocalDataSource(get(), get()) }}
    single {{ {c}Mapper() }}
    single<{c}Repository> {{ OfflineFirst{c}Repository(get(), get(), get(), get()) }}
"""

module_content += "}\n\n"

module_content += "val domainModule = module {\n"
for d in DOMAINS:
    c = upper(d)
    module_content += f"""    factory {{ Get{c}ListUseCase(get(), get()) }}
    factory {{ Get{c}DetailUseCase(get(), get()) }}
    factory {{ Create{c}UseCase(get(), get()) }}
    factory {{ Update{c}UseCase(get(), get()) }}
    factory {{ Delete{c}UseCase(get(), get()) }}
    factory {{ Search{c}UseCase(get(), get()) }}
    factory {{ Validate{c}UseCase(get()) }}
    factory {{ Refresh{c}CacheUseCase(get(), get()) }}
    factory {{ Get{c}CountUseCase(get()) }}
    factory {{ Filter{c}UseCase(get()) }}
"""
module_content += "}\n\n"

module_content += """val featureModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { ProductDetailViewModel(get(), get(), get()) }
    viewModel { CartViewModel(get(), get(), get()) }
    viewModel { CheckoutViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { OrderHistoryViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { NotificationsViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { ReviewsViewModel(get(), get()) }
    viewModel { WishlistViewModel(get(), get()) }
}

val allShopAppModules = listOf(
    coreNetworkModule, coreStorageModule, coreAuthModule, coreAnalyticsModule,
    coreConfigModule, coreLoggingModule, coreImageModule, coreNotificationModule,
    coreLocationModule, dataModule, domainModule, featureModule
)
"""

write(f"{BASE}/di/KoinModules.kt", module_content)

print("Koin DI modules written")
print()
print("✅ benchmark-koin-large rewritten as production-quality Koin app")
print(f"  Domains: {len(DOMAINS)}")
print(f"  Koin modules: 12")
print(f"  ViewModels: {len(vms)} (extending ViewModel())")
print(f"  Repository interfaces: {len(DOMAINS)}")
print(f"  OfflineFirst implementations: {len(DOMAINS)}")
