package com.codeint.dibenchmark.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.codeint.dibenchmark.ui.dashboard.BenchmarkDashboardScreen
import com.codeint.dibenchmark.ui.theme.DiBenchmarkTheme

class BenchmarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiBenchmarkTheme {
                BenchmarkDashboardScreen()
            }
        }
    }
}
