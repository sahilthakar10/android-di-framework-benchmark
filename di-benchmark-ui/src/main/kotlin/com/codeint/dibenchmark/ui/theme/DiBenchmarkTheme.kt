package com.codeint.dibenchmark.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HiltGreen = Color(0xFF4CAF50)
private val MetroBlue = Color(0xFF2196F3)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF388E3C),
    tertiary = Color(0xFFF57C00)
)

@Composable
fun DiBenchmarkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}

object BenchmarkColors {
    val Hilt = HiltGreen
    val Metro = MetroBlue
    val Warning = Color(0xFFFFC107)
    val Error = Color(0xFFF44336)
    val Success = Color(0xFF4CAF50)
}
