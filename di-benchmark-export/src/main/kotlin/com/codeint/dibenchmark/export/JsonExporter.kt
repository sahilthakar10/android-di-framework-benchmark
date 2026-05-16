package com.codeint.dibenchmark.export

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonExporter(
    private val prettyPrint: Boolean = true
) {

    private val json = Json {
        this.prettyPrint = this@JsonExporter.prettyPrint
        encodeDefaults = true
    }

    fun export(report: BenchmarkReportExport): String {
        return json.encodeToString(report)
    }

    fun exportToFile(report: BenchmarkReportExport, filePath: String) {
        val content = export(report)
        java.io.File(filePath).apply {
            parentFile?.mkdirs()
            writeText(content)
        }
    }
}
