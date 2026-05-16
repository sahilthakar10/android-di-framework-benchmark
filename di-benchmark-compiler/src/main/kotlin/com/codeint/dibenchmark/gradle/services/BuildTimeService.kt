package com.codeint.dibenchmark.gradle.services

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import java.util.concurrent.ConcurrentHashMap

abstract class BuildTimeService : BuildService<BuildServiceParameters.None>, OperationCompletionListener {

    private val taskStartTimes = ConcurrentHashMap<String, Long>()
    private val taskDurations = ConcurrentHashMap<String, Long>()
    private val taskCategories = ConcurrentHashMap<String, TaskCategory>()

    enum class TaskCategory {
        KSP, KAPT, KOTLIN_COMPILE, JAVA_COMPILE, OTHER
    }

    fun recordTaskStart(taskPath: String) {
        taskStartTimes[taskPath] = System.currentTimeMillis()
        taskCategories[taskPath] = categorize(taskPath)
    }

    fun recordTaskEnd(taskPath: String) {
        val startTime = taskStartTimes[taskPath] ?: return
        taskDurations[taskPath] = System.currentTimeMillis() - startTime
    }

    override fun onFinish(event: FinishEvent) {
        if (event is TaskFinishEvent) {
            val taskPath = event.descriptor.taskPath
            val duration = event.result.endTime - event.result.startTime
            taskDurations[taskPath] = duration
            taskCategories.putIfAbsent(taskPath, categorize(taskPath))
        }
    }

    fun getTaskDurations(): Map<String, Long> = taskDurations.toMap()

    fun getKspDuration(modulePath: String): Long {
        return taskDurations.entries
            .filter { it.key.startsWith(modulePath) && categorize(it.key) == TaskCategory.KSP }
            .sumOf { it.value }
    }

    fun getKaptDuration(modulePath: String): Long {
        return taskDurations.entries
            .filter { it.key.startsWith(modulePath) && categorize(it.key) == TaskCategory.KAPT }
            .sumOf { it.value }
    }

    fun getProcessorDuration(modulePath: String): Long {
        return getKspDuration(modulePath) + getKaptDuration(modulePath)
    }

    private fun categorize(taskPath: String): TaskCategory {
        val taskName = taskPath.substringAfterLast(":")
        return when {
            taskName.contains("ksp", ignoreCase = true) -> TaskCategory.KSP
            taskName.contains("kapt", ignoreCase = true) -> TaskCategory.KAPT
            taskName.contains("compileKotlin", ignoreCase = true) -> TaskCategory.KOTLIN_COMPILE
            taskName.contains("compileJava", ignoreCase = true) -> TaskCategory.JAVA_COMPILE
            else -> TaskCategory.OTHER
        }
    }
}
