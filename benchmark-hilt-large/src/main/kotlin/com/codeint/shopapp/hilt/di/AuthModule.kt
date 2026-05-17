package com.codeint.shopapp.hilt.di

import com.codeint.shopapp.hilt.core.auth.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AuthModule {

    @Provides @Singleton
    fun providesAuthManager(impl: RealAuthManager): AuthManager = impl

    @Provides @Singleton
    fun providesTokenStorage(impl: RealTokenStorage): TokenStorage = impl

    @Provides @Singleton
    fun providesSessionManager(impl: RealSessionManager): SessionManager = impl
}
