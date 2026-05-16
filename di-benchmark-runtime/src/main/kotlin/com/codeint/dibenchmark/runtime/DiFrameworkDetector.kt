package com.codeint.dibenchmark.runtime

import com.codeint.dibenchmark.annotations.FrameworkType

object DiFrameworkDetector {

    private val hiltMarkers = listOf(
        "dagger.hilt.android.HiltAndroidApp",
        "dagger.hilt.android.AndroidEntryPoint",
        "dagger.hilt.InstallIn"
    )

    private val metroMarkers = listOf(
        "dev.zacsweers.metro.DependencyGraph",
        "dev.zacsweers.metro.Inject",
        "dev.zacsweers.metro.Provides"
    )

    fun detect(): FrameworkType {
        val hasHilt = hiltMarkers.any { isClassAvailable(it) }
        val hasMetro = metroMarkers.any { isClassAvailable(it) }

        return when {
            hasHilt && hasMetro -> FrameworkType.UNKNOWN // Both present - needs per-module detection
            hasHilt -> FrameworkType.HILT
            hasMetro -> FrameworkType.METRO
            else -> FrameworkType.UNKNOWN
        }
    }

    fun detectForModule(moduleName: String): FrameworkType {
        // Check if compile-time detection result is available as a resource
        val resourceName = "di_benchmark_${moduleName.replace(":", "_").replace("-", "_")}"
        try {
            val classLoader = Thread.currentThread().contextClassLoader
            val resource = classLoader?.getResource("$resourceName.json")
            if (resource != null) {
                val content = resource.readText()
                return when {
                    content.contains("HILT") -> FrameworkType.HILT
                    content.contains("METRO") -> FrameworkType.METRO
                    else -> FrameworkType.UNKNOWN
                }
            }
        } catch (_: Exception) {
            // Fall through to runtime detection
        }
        return detect()
    }

    private fun isClassAvailable(className: String): Boolean = try {
        Class.forName(className)
        true
    } catch (_: ClassNotFoundException) {
        false
    }
}
