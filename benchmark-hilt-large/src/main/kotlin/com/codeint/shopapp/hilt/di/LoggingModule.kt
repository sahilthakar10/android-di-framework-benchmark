package com.codeint.shopapp.hilt.di

import com.codeint.shopapp.hilt.core.logging.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LoggingModule {

    @Provides @Singleton
    fun providesAppLogger(impl: RealAppLogger): AppLogger = impl
}
