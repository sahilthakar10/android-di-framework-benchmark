package com.codeint.dibenchmark.runtime

import android.os.Debug

object MemoryTracker {

    fun currentAllocation(): Long {
        return Debug.getNativeHeapAllocatedSize() + usedJvmMemory()
    }

    fun usedJvmMemory(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() - runtime.freeMemory()
    }

    fun nativeHeapSize(): Long = Debug.getNativeHeapAllocatedSize()

    fun snapshot(): MemorySnapshot {
        val runtime = Runtime.getRuntime()
        return MemorySnapshot(
            nativeHeapBytes = Debug.getNativeHeapAllocatedSize(),
            jvmUsedBytes = runtime.totalMemory() - runtime.freeMemory(),
            jvmTotalBytes = runtime.totalMemory(),
            jvmMaxBytes = runtime.maxMemory()
        )
    }
}

data class MemorySnapshot(
    val nativeHeapBytes: Long,
    val jvmUsedBytes: Long,
    val jvmTotalBytes: Long,
    val jvmMaxBytes: Long
) {
    val totalUsedBytes: Long get() = nativeHeapBytes + jvmUsedBytes
}
