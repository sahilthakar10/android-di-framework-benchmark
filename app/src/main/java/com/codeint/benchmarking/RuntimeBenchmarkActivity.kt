package com.codeint.benchmarking

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Headless activity that runs the Hilt vs Metro vs Koin runtime benchmark
 * and prints results to logcat. Launch via:
 *   adb shell am start -n com.codeint.benchmarking/.RuntimeBenchmarkActivity
 */
class RuntimeBenchmarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val iterations = intent.getIntExtra("iterations", 100)

        Log.i(TAG, "Starting runtime benchmark ($iterations iterations)...")

        // Background coroutine: prevents ANR, auto-cancels on Activity destroy, lifecycle-safe
        lifecycleScope.launch(Dispatchers.Default) {
            val result = RuntimeBenchmark.runFullComparison(application, iterations)

            Log.i(TAG, "")
            Log.i(TAG, result.summary)

            Log.i(TAG, "=== RAW METRICS ===")
            Log.i(TAG, "INIT|Hilt|${result.hilt.initTimeNanos}")
            Log.i(TAG, "INIT|Metro|${result.metro.initTimeNanos}")
            Log.i(TAG, "INIT|Koin|${result.koin.initTimeNanos}")

            for (key in result.hilt.firstInjectionNanos.keys) {
                Log.i(TAG, "COLD|Hilt|$key|${result.hilt.firstInjectionNanos[key]}")
                Log.i(TAG, "COLD|Metro|$key|${result.metro.firstInjectionNanos[key]}")
                Log.i(TAG, "COLD|Koin|$key|${result.koin.firstInjectionNanos[key]}")
            }

            for (key in result.hilt.warmInjectionAvgNanos.keys) {
                Log.i(TAG, "WARM|Hilt|$key|${result.hilt.warmInjectionAvgNanos[key]}")
                Log.i(TAG, "WARM|Metro|$key|${result.metro.warmInjectionAvgNanos[key]}")
                Log.i(TAG, "WARM|Koin|$key|${result.koin.warmInjectionAvgNanos[key]}")
            }

            Log.i(TAG, "MEMORY|Hilt|${result.hilt.memoryDeltaBytes}")
            Log.i(TAG, "MEMORY|Metro|${result.metro.memoryDeltaBytes}")
            Log.i(TAG, "MEMORY|Koin|${result.koin.memoryDeltaBytes}")
            Log.i(TAG, "TOTAL_WARM|Hilt|${result.hilt.totalWarmNanos}")
            Log.i(TAG, "TOTAL_WARM|Metro|${result.metro.totalWarmNanos}")
            Log.i(TAG, "TOTAL_WARM|Koin|${result.koin.totalWarmNanos}")
            Log.i(TAG, "=== END ===")

            withContext(Dispatchers.Main) { finish() }
        }
    }

    companion object {
        private const val TAG = "RuntimeBenchmark"
    }
}
