package com.codeint.benchmarking

import android.app.Application
import com.codeint.interop.hilt.HiltCoreEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * Runs all 3 interop demos using the SAME Hilt entry point.
 * Hilt provides 6 core services → each framework provides 3 feature services.
 */
object InteropBenchmarkRunner {

    data class InteropResult(
        val framework: String,
        val output: String,
        val bridgeLinesOfCode: Int,
        val durationMs: Long
    )

    fun runAll(application: Application): List<InteropResult> {
        // One shared Hilt entry point for all three demos
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            application,
            HiltCoreEntryPoint::class.java
        )

        val results = mutableListOf<InteropResult>()

        // ── Metro (zero bridge) ──
        val metroStart = System.nanoTime()
        val metroOutput = com.codeint.interop.InteropDemo.run(hiltEntryPoint)
        val metroDuration = (System.nanoTime() - metroStart) / 1_000_000
        results.add(InteropResult("Metro", metroOutput, 0, metroDuration))

        // ── Koin (manual bridge) ──
        val koinStart = System.nanoTime()
        val koinOutput = com.codeint.interop.koin.InteropDemo.run(hiltEntryPoint)
        val koinDuration = (System.nanoTime() - koinStart) / 1_000_000
        results.add(InteropResult("Koin", koinOutput, 20, koinDuration))

        // ── kotlin-inject-anvil (manual bridge) ──
        val kinjectStart = System.nanoTime()
        val kinjectOutput = com.codeint.interop.kinject.InteropDemo.run(hiltEntryPoint)
        val kinjectDuration = (System.nanoTime() - kinjectStart) / 1_000_000
        results.add(InteropResult("kotlin-inject", kinjectOutput, 12, kinjectDuration))

        return results
    }
}
