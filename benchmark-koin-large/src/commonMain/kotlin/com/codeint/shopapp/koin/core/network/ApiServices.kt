package com.codeint.shopapp.koin.core.network

interface TokenProvider { fun getAccessToken(): String; fun getRefreshToken(): String; fun isExpired(): Boolean }
interface NetworkLogger { fun logRequest(method: String, url: String, status: Int); fun logError(url: String, error: String) }
interface CachePolicy { fun isCacheable(url: String): Boolean; fun getTtl(url: String): Long }

class RealTokenProvider : TokenProvider {
    override fun getAccessToken() = "mock_access_token"
    override fun getRefreshToken() = "mock_refresh_token"
    override fun isExpired() = false
}
class RealNetworkLogger : NetworkLogger {
    override fun logRequest(method: String, url: String, status: Int) {}
    override fun logError(url: String, error: String) {}
}
class RealCachePolicy : CachePolicy {
    override fun isCacheable(url: String) = url.contains("/products")
    override fun getTtl(url: String) = 300_000L
}
class AuthInterceptor(private val tokenProvider: TokenProvider) { fun intercept(url: String) = mapOf("Auth" to tokenProvider.getAccessToken()) }
class CacheInterceptor(private val cachePolicy: CachePolicy) { fun shouldCache(url: String) = cachePolicy.isCacheable(url) }
class LoggingInterceptor(private val networkLogger: NetworkLogger) { fun log(m: String, u: String, s: Int) { networkLogger.logRequest(m, u, s) } }
class RateLimiter(private val config: RetryPolicy) { fun shouldThrottle(endpoint: String) = false }
class WebSocketManager(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) { fun connect(ch: String) {}; fun send(msg: String) {} }
class GraphQLClient(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) { fun query(q: String) = "{}"; fun mutate(m: String) = "{}" }
class FileUploader(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) { fun upload(f: String, e: String) = "id_123" }
