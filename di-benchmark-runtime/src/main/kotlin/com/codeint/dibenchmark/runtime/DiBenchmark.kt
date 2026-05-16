package com.codeint.dibenchmark.runtime

import android.app.Application
import android.util.Log
import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.annotations.ScopeType

object DiBenchmark {

    internal var config: BenchmarkConfig = BenchmarkConfig.DEFAULT
        private set

    var isEnabled: Boolean = false
        private set

    private var currentSession: BenchmarkSession? = null
    private var isInitialized = false

    fun initialize(application: Application, config: BenchmarkConfig = BenchmarkConfig.DEFAULT) {
        if (isInitialized) return

        this.config = config
        this.isEnabled = config.enabled
        DeviceInfoProvider.init(application)
        isInitialized = true

        Log.i("DiBenchmark", "SDK initialized. Detected framework: ${DiFrameworkDetector.detect()}")
    }

    fun startSession(): BenchmarkSession {
        check(isInitialized) { "DiBenchmark must be initialized before starting a session" }
        BenchmarkRegistry.reset()
        val session = BenchmarkSession()
        currentSession = session
        Log.i("DiBenchmark", "Session started: ${session.sessionId}")
        return session
    }

    fun currentSession(): BenchmarkSession? = currentSession

    fun endSession(): SessionReport? {
        val session = currentSession ?: return null
        session.end()
        val report = session.getReport()
        currentSession = null
        Log.i("DiBenchmark", "Session ended. Total injections: ${report.overallSummary.totalInjections}")
        return report
    }

    fun getRegistry(): BenchmarkRegistry = BenchmarkRegistry

    // Convenience method for benchmarking any injection
    inline fun <T> injection(
        className: String,
        moduleName: String,
        framework: FrameworkType = DiFrameworkDetector.detect(),
        scopeType: ScopeType = ScopeType.UNKNOWN,
        crossinline block: () -> T
    ): T = InjectionTimer.measure(className, moduleName, framework, scopeType, block)

    // Convenience method for Metro graph creation
    inline fun <reified T> createMetroGraph(
        moduleName: String,
        crossinline factory: () -> T
    ): T = InjectionTimer.measureGraphCreation(
        graphName = T::class.simpleName ?: "UnknownGraph",
        moduleName = moduleName,
        framework = FrameworkType.METRO,
        block = factory
    )

    // Track a provider call
    inline fun <T> provider(
        providerName: String,
        moduleName: String,
        framework: FrameworkType = DiFrameworkDetector.detect(),
        crossinline block: () -> T
    ): T = ProviderCallCounter.track(providerName, moduleName, framework, block)
}
