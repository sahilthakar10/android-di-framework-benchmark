package com.codeint.dibenchmark.runtime

import com.codeint.dibenchmark.annotations.ScopeType
import java.util.concurrent.ConcurrentHashMap

object ScopeTracker {

    private val scopeMap = ConcurrentHashMap<String, ScopeInfo>()

    fun registerScope(className: String, moduleName: String, scopeType: ScopeType) {
        scopeMap[className] = ScopeInfo(className, moduleName, scopeType)
    }

    fun getScopeType(className: String): ScopeType {
        return scopeMap[className]?.scopeType ?: ScopeType.UNKNOWN
    }

    fun getAllScopes(): Map<String, ScopeInfo> = scopeMap.toMap()

    fun getScopeSummary(): Map<ScopeType, Int> {
        return scopeMap.values.groupBy { it.scopeType }.mapValues { it.value.size }
    }

    fun reset() {
        scopeMap.clear()
    }
}

data class ScopeInfo(
    val className: String,
    val moduleName: String,
    val scopeType: ScopeType
)
