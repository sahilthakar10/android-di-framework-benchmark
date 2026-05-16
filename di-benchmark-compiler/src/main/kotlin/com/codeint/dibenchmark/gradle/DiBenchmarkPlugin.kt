package com.codeint.dibenchmark.gradle

import com.codeint.dibenchmark.gradle.services.BuildTimeService
import com.codeint.dibenchmark.gradle.tasks.AggregateMetricsTask
import com.codeint.dibenchmark.gradle.tasks.DetectFrameworkTask
import com.codeint.dibenchmark.gradle.tasks.MeasureCompileTimeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

abstract class DiBenchmarkPlugin @Inject constructor(
    private val buildEventsListenerRegistry: BuildEventsListenerRegistry
) : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("diBenchmark", DiBenchmarkExtension::class.java)

        // Register build time tracking service
        val serviceProvider = project.gradle.sharedServices.registerIfAbsent(
            "diBenchmarkBuildTimeService",
            BuildTimeService::class.java
        ) {}
        buildEventsListenerRegistry.onTaskCompletion(serviceProvider)

        project.afterEvaluate {
            if (!extension.enabled.getOrElse(true)) return@afterEvaluate

            // Register framework detection task
            project.tasks.register("detectDiFramework", DetectFrameworkTask::class.java) { task ->
                task.outputDir.set(extension.outputDir.orElse(project.layout.buildDirectory.dir("di-benchmark")))
                task.moduleName.set(project.path)
            }

            // Register compile-time measurement task
            project.tasks.register("measureDiCompileTime", MeasureCompileTimeTask::class.java) { task ->
                task.outputDir.set(extension.outputDir.orElse(project.layout.buildDirectory.dir("di-benchmark")))
                task.moduleName.set(project.path)
                task.buildTimeService.set(serviceProvider)

                // Run after KSP/KAPT tasks
                project.tasks.matching { it.name.contains("ksp") || it.name.contains("kapt") }
                    .configureEach { processorTask ->
                        task.mustRunAfter(processorTask)
                    }
            }

            // Hook into KSP/KAPT tasks to record timing
            project.tasks.matching { it.name.contains("ksp") || it.name.contains("kapt") }
                .configureEach { processorTask ->
                    processorTask.doFirst {
                        serviceProvider.get().recordTaskStart(processorTask.path)
                    }
                    processorTask.doLast {
                        serviceProvider.get().recordTaskEnd(processorTask.path)
                    }
                }
        }

        // Register aggregate task on root project
        if (project == project.rootProject) {
            project.tasks.register("diBenchmarkReport", AggregateMetricsTask::class.java) { task ->
                task.outputDir.set(project.layout.buildDirectory.dir("di-benchmark/report"))
            }
        }
    }
}
