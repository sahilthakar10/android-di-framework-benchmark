package com.codeint.dibenchmark.runtime

import android.app.ActivityManager
import android.content.Context
import android.os.Build

object DeviceInfoProvider {

    private var cachedInfo: DeviceInfo? = null
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun get(): DeviceInfo {
        cachedInfo?.let { return it }

        val info = DeviceInfo(
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            sdkVersion = Build.VERSION.SDK_INT,
            cpuCores = Runtime.getRuntime().availableProcessors(),
            totalMemoryBytes = getTotalMemory()
        )
        cachedInfo = info
        return info
    }

    private fun getTotalMemory(): Long {
        val context = appContext ?: return Runtime.getRuntime().maxMemory()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memInfo)
        return memInfo.totalMem
    }
}
