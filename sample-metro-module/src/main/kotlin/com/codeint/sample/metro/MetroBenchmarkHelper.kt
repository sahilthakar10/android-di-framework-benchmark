package com.codeint.sample.metro

import dev.zacsweers.metro.createGraph

object MetroBenchmarkHelper {
    fun createGraph(): SampleMetroGraph {
        return createGraph<SampleMetroGraph>()
    }
}
