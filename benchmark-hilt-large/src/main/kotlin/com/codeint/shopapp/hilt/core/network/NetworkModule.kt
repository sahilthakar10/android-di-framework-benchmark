package com.codeint.shopapp.hilt.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

class HttpClient(val baseUrl: String, val timeout: Long)
class ApiResponseParser
class NetworkMonitor
class RetryPolicy(val maxRetries: Int, val backoffMs: Long)
class SslPinningConfig(val pins: List<String>)

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideHttpClient(): HttpClient = HttpClient("https://api.shopapp.com", 30_000)

    @Provides @Singleton
    fun provideApiResponseParser(): ApiResponseParser = ApiResponseParser()

    @Provides @Singleton
    fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor()

    @Provides @Singleton
    fun provideRetryPolicy(): RetryPolicy = RetryPolicy(3, 1000)

    @Provides @Singleton
    fun provideSslPinningConfig(): SslPinningConfig = SslPinningConfig(listOf("sha256/abc123"))
}
