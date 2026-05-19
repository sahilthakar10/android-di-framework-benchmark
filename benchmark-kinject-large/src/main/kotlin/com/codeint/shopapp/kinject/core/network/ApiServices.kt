package com.codeint.shopapp.kinject.core.network

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface TokenProvider { fun getAccessToken(): String; fun getRefreshToken(): String; fun isExpired(): Boolean }
interface NetworkLogger { fun logRequest(method: String, url: String, status: Int); fun logError(url: String, error: String) }
interface CachePolicy { fun isCacheable(url: String): Boolean; fun getTtl(url: String): Long }

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealTokenProvider : TokenProvider {
    override fun getAccessToken() = "mock_access_token"; override fun getRefreshToken() = "mock_refresh_token"; override fun isExpired() = false
}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealNetworkLogger : NetworkLogger {
    override fun logRequest(method: String, url: String, status: Int) {}; override fun logError(url: String, error: String) {}
}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealCachePolicy : CachePolicy {
    override fun isCacheable(url: String) = url.contains("/products"); override fun getTtl(url: String) = 300_000L
}
@Inject @SingleIn(AppScope::class) class AuthInterceptor(private val tokenProvider: TokenProvider) { fun intercept(url: String) = mapOf("Auth" to tokenProvider.getAccessToken()) }
@Inject @SingleIn(AppScope::class) class CacheInterceptor(private val cachePolicy: CachePolicy) { fun shouldCache(url: String) = cachePolicy.isCacheable(url) }
@Inject @SingleIn(AppScope::class) class LoggingInterceptor(private val networkLogger: NetworkLogger) { fun log(m: String, u: String, s: Int) { networkLogger.logRequest(m, u, s) } }
@Inject @SingleIn(AppScope::class) class RateLimiter(private val config: RetryPolicy) { fun shouldThrottle(endpoint: String) = false }
@Inject @SingleIn(AppScope::class) class WebSocketManager(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) { fun connect(ch: String) {}; fun send(msg: String) {} }
@Inject @SingleIn(AppScope::class) class GraphQLClient(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) { fun query(q: String) = "{}"; fun mutate(m: String) = "{}" }
@Inject @SingleIn(AppScope::class) class FileUploader(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) { fun upload(f: String, e: String) = "id_123" }
