package com.codeint.shopapp.hilt.di

import com.codeint.shopapp.hilt.core.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

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
}
