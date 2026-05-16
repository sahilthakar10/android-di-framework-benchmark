package com.codeint.dibenchmark.gradle.tasks

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class DetectFrameworkTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val moduleName: Property<String>

    init {
        group = "di-benchmark"
        description = "Detects which DI framework (Hilt or Metro) is used in this module"
    }

    @TaskAction
    fun detect() {
        val configurations = project.configurations.filter { it.isCanBeResolved }
        var hasHilt = false
        var hasMetro = false

        for (config in configurations) {
            try {
                val resolved = config.resolvedConfiguration.lenientConfiguration.allModuleDependencies
                for (dep in resolved) {
                    val group = dep.moduleGroup
                    val name = dep.moduleName
                    if (group == "com.google.dagger" && name.contains("hilt")) {
                        hasHilt = true
                    }
                    if (group == "dev.zacsweers.metro") {
                        hasMetro = true
                    }
                }
            } catch (_: Exception) {
                // Skip unresolvable configurations
            }
        }

        val framework = when {
            hasHilt && hasMetro -> "BOTH"
            hasHilt -> "HILT"
            hasMetro -> "METRO"
            else -> "NONE"
        }

        val detection = FrameworkDetection(
            moduleName = moduleName.get(),
            framework = framework,
            hasHilt = hasHilt,
            hasMetro = hasMetro,
            timestamp = System.currentTimeMillis()
        )

        val outputFile = outputDir.get().file("framework-detection.json").asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(Json { prettyPrint = true }.encodeToString(detection))

        logger.lifecycle("[DiBenchmark] Module ${moduleName.get()} -> Framework: $framework")
    }
}

@Serializable
data class FrameworkDetection(
    val moduleName: String,
    val framework: String,
    val hasHilt: Boolean,
    val hasMetro: Boolean,
    val timestamp: Long
)
