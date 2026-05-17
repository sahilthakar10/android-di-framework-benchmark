package com.codeint.shopapp.hilt.di

import com.codeint.shopapp.hilt.core.analytics.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AnalyticsModule {

    @Provides @Singleton
    fun providesAnalyticsTracker(impl: RealAnalyticsTracker): AnalyticsTracker = impl

    @Provides @Singleton
    fun providesCrashReporter(impl: RealCrashReporter): CrashReporter = impl
}
