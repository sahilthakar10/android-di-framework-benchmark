package com.codeint.dibenchmark.annotations

/**
 * Manually specifies which DI framework a module or class uses.
 * Use this when auto-detection isn't sufficient or when you want
 * to override the detected framework type.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DIFramework(val type: FrameworkType)
