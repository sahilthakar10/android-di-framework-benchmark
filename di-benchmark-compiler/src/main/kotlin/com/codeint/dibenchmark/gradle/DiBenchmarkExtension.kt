package com.codeint.dibenchmark.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

abstract class DiBenchmarkExtension {
    abstract val enabled: Property<Boolean>
    abstract val outputDir: DirectoryProperty
    abstract val measureCompileTime: Property<Boolean>
    abstract val measureRuntime: Property<Boolean>
    abstract val warmupIterations: Property<Int>
    abstract val ciMode: Property<Boolean>
    abstract val frameworkOverride: Property<String>

    init {
        enabled.convention(true)
        measureCompileTime.convention(true)
        measureRuntime.convention(true)
        warmupIterations.convention(3)
        ciMode.convention(false)
    }
}
