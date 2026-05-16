package com.codeint.dibenchmark.gradle.tasks

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class AggregateMetricsTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        group = "di-benchmark"
        description = "Aggregates DI benchmark metrics from all modules into a single report"
        // Always re-run
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun aggregate() {
        val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
        val allModuleMetrics = mutableListOf<ModuleCompileMetrics>()

        // Collect from all subprojects
        project.rootProject.allprojects.forEach { subproject ->
            val benchmarkDir = File(subproject.layout.buildDirectory.get().asFile, "di-benchmark")
            if (!benchmarkDir.exists()) return@forEach

            val compileMetricsFile = File(benchmarkDir, "compile-metrics.json")
            val frameworkFile = File(benchmarkDir, "framework-detection.json")

            var framework = "UNKNOWN"
            if (frameworkFile.exists()) {
                try {
                    val detection = json.decodeFromStream<FrameworkDetection>(frameworkFile.inputStream())
                    framework = detection.framework
                } catch (_: Exception) {}
            }

            if (compileMetricsFile.exists()) {
                try {
                    val metrics = json.decodeFromStream<CompileMetricsOutput>(compileMetricsFile.inputStream())
                    allModuleMetrics.add(
                        ModuleCompileMetrics(
                            moduleName = metrics.moduleName,
                            framework = framework,
                            kspDurationMs = metrics.kspDurationMs,
                            kaptDurationMs = metrics.kaptDurationMs,
                            totalProcessorMs = metrics.totalProcessorMs,
                            generatedFileCount = metrics.generatedFileCount,
                            generatedCodeSizeBytes = metrics.generatedCodeSizeBytes,
                            generatedLineCount = metrics.generatedLineCount,
                            incrementalBuild = metrics.incrementalBuild
                        )
                    )
                } catch (_: Exception) {}
            }
        }

        val aggregateReport = AggregateReport(
            timestamp = System.currentTimeMillis(),
            totalModules = allModuleMetrics.size,
            hiltModules = allModuleMetrics.count { it.framework == "HILT" },
            metroModules = allModuleMetrics.count { it.framework == "METRO" },
            totalProcessorTimeMs = allModuleMetrics.sumOf { it.totalProcessorMs },
            totalGeneratedFiles = allModuleMetrics.sumOf { it.generatedFileCount },
            totalGeneratedSizeBytes = allModuleMetrics.sumOf { it.generatedCodeSizeBytes },
            modules = allModuleMetrics
        )

        val outputFile = outputDir.get().file("aggregate-report.json").asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(json.encodeToString(aggregateReport))

        // Print summary
        logger.lifecycle("")
        logger.lifecycle("═".repeat(60))
        logger.lifecycle("DI BENCHMARK - COMPILE TIME REPORT")
        logger.lifecycle("═".repeat(60))
        logger.lifecycle("Total modules: ${aggregateReport.totalModules}")
        logger.lifecycle("  Hilt:  ${aggregateReport.hiltModules}")
        logger.lifecycle("  Metro: ${aggregateReport.metroModules}")
        logger.lifecycle("Total processor time: ${aggregateReport.totalProcessorTimeMs}ms")
        logger.lifecycle("Total generated files: ${aggregateReport.totalGeneratedFiles}")
        logger.lifecycle("Total generated size: ${aggregateReport.totalGeneratedSizeBytes / 1024}KB")
        logger.lifecycle("")

        for (module in allModuleMetrics) {
            logger.lifecycle("  ${module.moduleName} [${module.framework}]: ${module.totalProcessorMs}ms, ${module.generatedFileCount} files")
        }
        logger.lifecycle("═".repeat(60))
    }
}

@Serializable
data class ModuleCompileMetrics(
    val moduleName: String,
    val framework: String,
    val kspDurationMs: Long,
    val kaptDurationMs: Long,
    val totalProcessorMs: Long,
    val generatedFileCount: Int,
    val generatedCodeSizeBytes: Long,
    val generatedLineCount: Int,
    val incrementalBuild: Boolean
)

@Serializable
data class AggregateReport(
    val timestamp: Long,
    val totalModules: Int,
    val hiltModules: Int,
    val metroModules: Int,
    val totalProcessorTimeMs: Long,
    val totalGeneratedFiles: Int,
    val totalGeneratedSizeBytes: Long,
    val modules: List<ModuleCompileMetrics>
)
