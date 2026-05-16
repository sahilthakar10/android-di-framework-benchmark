package com.codeint.sample.hilt

import javax.inject.Inject
import javax.inject.Singleton

class SampleHiltRepository @Inject constructor(
    private val dataSource: SampleHiltDataSource
) {
    fun getData(): String = dataSource.fetch()
}

class SampleHiltDataSource @Inject constructor() {
    fun fetch(): String {
        var sum = 0L
        for (i in 1..1000) sum += i
        return "Hilt data (checksum=$sum)"
    }
}

@Singleton
class SampleHiltSingleton @Inject constructor() {
    private val createdAt = System.nanoTime()
    fun process(): String = "Hilt singleton created at $createdAt"
}
