package com.codeint.dibenchmark.annotations

/**
 * Marks a class whose dependency injection should be benchmarked.
 * When applied, the SDK will automatically measure injection time,
 * memory allocation, and scope resolution for this class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BenchmarkInjection
