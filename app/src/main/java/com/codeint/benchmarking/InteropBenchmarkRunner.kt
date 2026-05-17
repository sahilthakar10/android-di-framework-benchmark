package com.codeint.benchmarking

import android.app.Application
import com.codeint.interop.hilt.HiltCoreEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * Runs all 3 interop examples using the SAME Hilt entry point.
 * Hilt provides 6 core services, each framework provides 3 feature services.
 */
object InteropBenchmarkRunner {

    fun runAll(application: Application) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            application,
            HiltCoreEntryPoint::class.java
        )

        // Metro — zero bridge code via @Includes
        com.codeint.interop.HiltMetroInteropExample.run(hiltEntryPoint)

        // Koin — manual bridge module (~20 lines)
        com.codeint.interop.koin.HiltKoinInteropExample.run(hiltEntryPoint)

        // kotlin-inject-anvil — @get:Provides constructor params (~12 lines)
        com.codeint.interop.kinject.HiltKotlinInjectInteropExample.run(hiltEntryPoint)
    }
}
