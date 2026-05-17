package com.codeint.shopapp.metro.core.network

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface TokenProvider { fun getAccessToken(): String; fun getRefreshToken(): String; fun isExpired(): Boolean }
interface NetworkLogger { fun logRequest(method: String, url: String, status: Int); fun logError(url: String, error: String) }
interface CachePolicy { fun isCacheable(url: String): Boolean; fun getTtl(url: String): Long }

@SingleIn(AppScope::class) class RealTokenProvider @Inject constructor() : TokenProvider {
    override fun getAccessToken() = "mock_access_token"; override fun getRefreshToken() = "mock_refresh_token"; override fun isExpired() = false
}
@SingleIn(AppScope::class) class RealNetworkLogger @Inject constructor() : NetworkLogger {
    override fun logRequest(method: String, url: String, status: Int) {}; override fun logError(url: String, error: String) {}
}
@SingleIn(AppScope::class) class RealCachePolicy @Inject constructor() : CachePolicy {
    override fun isCacheable(url: String) = url.contains("/products"); override fun getTtl(url: String) = 300_000L
}
@SingleIn(AppScope::class) class AuthInterceptor @Inject constructor(private val tokenProvider: TokenProvider) { fun intercept(url: String) = mapOf("Auth" to tokenProvider.getAccessToken()) }
@SingleIn(AppScope::class) class CacheInterceptor @Inject constructor(private val cachePolicy: CachePolicy) { fun shouldCache(url: String) = cachePolicy.isCacheable(url) }
@SingleIn(AppScope::class) class LoggingInterceptor @Inject constructor(private val networkLogger: NetworkLogger) { fun log(m: String, u: String, s: Int) { networkLogger.logRequest(m, u, s) } }
@SingleIn(AppScope::class) class RateLimiter @Inject constructor(private val config: RetryPolicy) { fun shouldThrottle(endpoint: String) = false }
@SingleIn(AppScope::class) class WebSocketManager @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) { fun connect(ch: String) {}; fun send(msg: String) {} }
@SingleIn(AppScope::class) class GraphQLClient @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) { fun query(q: String) = "{}"; fun mutate(m: String) = "{}" }
@SingleIn(AppScope::class) class FileUploader @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) { fun upload(f: String, e: String) = "id_123" }
