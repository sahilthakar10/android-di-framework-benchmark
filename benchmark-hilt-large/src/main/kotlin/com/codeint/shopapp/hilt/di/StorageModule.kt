package com.codeint.shopapp.hilt.di

import com.codeint.shopapp.hilt.core.storage.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object StorageModule {

    @Provides @Singleton
    fun providesDatabaseManager(impl: RealDatabaseManager): DatabaseManager = impl

    @Provides @Singleton
    fun providesPreferencesManager(impl: RealPreferencesManager): PreferencesManager = impl

    @Provides @Singleton
    fun providesSecureStorage(impl: RealSecureStorage): SecureStorage = impl

    @Provides @Singleton
    fun providesCacheManager(impl: RealCacheManager): CacheManager = impl
}
