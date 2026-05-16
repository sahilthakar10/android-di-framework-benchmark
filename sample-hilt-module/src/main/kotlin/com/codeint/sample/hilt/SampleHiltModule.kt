package com.codeint.sample.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SampleHiltModule {

    @Provides
    fun provideSampleService(): SampleHiltService {
        return SampleHiltService()
    }

    @Provides
    fun provideConfig(): SampleHiltConfig {
        return SampleHiltConfig(timeout = 5000, retries = 3)
    }
}

class SampleHiltService {
    fun execute(): String = "Service executed at ${System.nanoTime()}"
}

data class SampleHiltConfig(val timeout: Int, val retries: Int)
