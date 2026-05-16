package com.codeint.sample.metro

import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides

@DependencyGraph
interface SampleMetroGraph {
    val repository: SampleMetroRepository
    val service: SampleMetroService
    val analytics: SampleMetroAnalytics

    @Provides
    fun provideConfig(): SampleMetroConfig = SampleMetroConfig(batchSize = 100, debug = true)
}

class SampleMetroRepository @Inject constructor(
    private val dataSource: SampleMetroDataSource
) {
    fun getData(): String = dataSource.fetch()
}

class SampleMetroDataSource @Inject constructor() {
    fun fetch(): String {
        var sum = 0L
        for (i in 1..1000) sum += i
        return "Metro data (checksum=$sum)"
    }
}

class SampleMetroService @Inject constructor(
    private val repository: SampleMetroRepository,
    private val config: SampleMetroConfig
) {
    fun process(): String = "Metro service: ${repository.getData()} [batch=${config.batchSize}]"
}

class SampleMetroAnalytics @Inject constructor() {
    fun track(event: String) { /* no-op */ }
}

class SampleMetroCache @Inject constructor() {
    private val store = mutableMapOf<String, String>()
    fun put(key: String, value: String) { store[key] = value }
    fun get(key: String): String? = store[key]
}

class SampleMetroLogger @Inject constructor() {
    fun log(message: String) { /* no-op */ }
}

data class SampleMetroConfig(val batchSize: Int, val debug: Boolean)
