package com.codeint.benchmarking

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.codeint.benchmarking.ui.theme.BenchMarkingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BenchMarkingTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("DI Framework Benchmark", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("Production-quality e-commerce app", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("123 classes | 14 domains | 13 ViewModels | 3 frameworks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, HiltFullBenchmarkActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Hilt Full Benchmark", style = MaterialTheme.typography.titleMedium) }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, KoinFullBenchmarkActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) { Text("Koin Full Benchmark", style = MaterialTheme.typography.titleMedium, color = Color.White) }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, MetroFullBenchmarkActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
            ) { Text("Metro Full Benchmark", style = MaterialTheme.typography.titleMedium, color = Color.White) }
        }
    }
}
