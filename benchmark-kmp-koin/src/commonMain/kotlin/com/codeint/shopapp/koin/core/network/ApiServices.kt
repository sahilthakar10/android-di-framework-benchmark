package com.codeint.shopapp.koin.core.network


class AuthInterceptor constructor(private val tokenProvider: TokenProvider) {
    fun intercept(url: String): Map<String, String> = mapOf("Authorization" to "Bearer ${tokenProvider.getAccessToken()}")
}

class CacheInterceptor constructor(private val cachePolicy: CachePolicy) {
    fun shouldCache(url: String): Boolean = cachePolicy.isCacheable(url)
}

class LoggingInterceptor constructor(private val networkLogger: NetworkLogger) {
    fun log(method: String, url: String, status: Int) { networkLogger.logRequest(method, url, status) }
}

class RateLimiter constructor(private val config: RetryPolicy) {
    private val requestCounts = mutableMapOf<String, Int>()
    fun shouldThrottle(endpoint: String): Boolean = (requestCounts[endpoint] ?: 0) > 100
}

class NetworkLogger constructor() {
    fun logRequest(method: String, url: String, status: Int) {}
    fun logError(url: String, error: String) {}
}

class CachePolicy constructor() {
    fun isCacheable(url: String): Boolean = url.contains("/products") || url.contains("/categories")
    fun getTtl(url: String): Long = if (url.contains("/products")) 300_000 else 60_000
}

class TokenProvider constructor() {
    fun getAccessToken(): String = "mock_access_token"
    fun getRefreshToken(): String = "mock_refresh_token"
    fun isExpired(): Boolean = false
}

class WebSocketManager constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {
    fun connect(channel: String) {}
    fun disconnect() {}
    fun send(message: String) {}
}

class GraphQLClient constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) {
    fun query(query: String): String = "{}"
    fun mutate(mutation: String): String = "{}"
}

class FileUploader constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {
    fun upload(filePath: String, endpoint: String): String = "upload_id_123"
}
