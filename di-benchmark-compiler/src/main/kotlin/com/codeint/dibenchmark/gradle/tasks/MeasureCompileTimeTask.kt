package com.codeint.dibenchmark.gradle.tasks

import com.codeint.dibenchmark.gradle.services.BuildTimeService
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class MeasureCompileTimeTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val moduleName: Property<String>

    @get:Internal
    abstract val buildTimeService: Property<BuildTimeService>

    init {
        group = "di-benchmark"
        description = "Measures compile-time metrics for DI code generation"
        // Always re-run
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun measure() {
        val service = buildTimeService.get()
        val module = moduleName.get()

        // Get processor durations from the build service
        val kspDuration = service.getKspDuration(module)
        val kaptDuration = service.getKaptDuration(module)
        val totalProcessorDuration = service.getProcessorDuration(module)

        // Scan generated sources for code gen metrics
        val generatedMetrics = scanGeneratedSources()

        // Check if this was an incremental build
        val isIncremental = checkIncremental()

        val metrics = CompileMetricsOutput(
            moduleName = module,
            kspDurationMs = kspDuration,
            kaptDurationMs = kaptDuration,
            totalProcessorMs = totalProcessorDuration,
            generatedFileCount = generatedMetrics.fileCount,
            generatedCodeSizeBytes = generatedMetrics.totalSizeBytes,
            generatedLineCount = generatedMetrics.lineCount,
            incrementalBuild = isIncremental,
            buildTimestamp = System.currentTimeMillis()
        )

        val outputFile = outputDir.get().file("compile-metrics.json").asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(Json { prettyPrint = true }.encodeToString(metrics))

        logger.lifecycle(
            "[DiBenchmark] Compile metrics for $module: " +
                "processor=${totalProcessorDuration}ms, " +
                "files=${generatedMetrics.fileCount}, " +
                "size=${generatedMetrics.totalSizeBytes}B"
        )
    }

    private fun scanGeneratedSources(): GeneratedCodeMetrics {
        val generatedDirs = listOf(
            project.layout.buildDirectory.dir("generated/ksp").get().asFile,
            project.layout.buildDirectory.dir("generated/source/kapt").get().asFile,
            project.layout.buildDirectory.dir("generated/source/kaptKotlin").get().asFile
        )

        var fileCount = 0
        var totalSize = 0L
        var lineCount = 0

        for (dir in generatedDirs) {
            if (!dir.exists()) continue
            dir.walkTopDown()
                .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
                .forEach { file ->
                    fileCount++
                    totalSize += file.length()
                    lineCount += file.readLines().size
                }
        }

        return GeneratedCodeMetrics(fileCount, totalSize, lineCount)
    }

    private fun checkIncremental(): Boolean {
        // Check if Gradle ran in incremental mode by looking at task outputs
        val buildDir = project.layout.buildDirectory.get().asFile
        val incrementalDir = File(buildDir, "tmp")
        return incrementalDir.exists() && incrementalDir.listFiles()?.any {
            it.name.contains("incremental")
        } == true
    }

    private data class GeneratedCodeMetrics(
        val fileCount: Int,
        val totalSizeBytes: Long,
        val lineCount: Int
    )
}

@Serializable
data class CompileMetricsOutput(
    val moduleName: String,
    val kspDurationMs: Long,
    val kaptDurationMs: Long,
    val totalProcessorMs: Long,
    val generatedFileCount: Int,
    val generatedCodeSizeBytes: Long,
    val generatedLineCount: Int,
    val incrementalBuild: Boolean,
    val buildTimestamp: Long
)
