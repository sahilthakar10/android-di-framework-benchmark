package com.codeint.dibenchmark.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BarChartData(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun BarChart(
    data: List<BarChartData>,
    unit: String,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOf { it.value }
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier) {
        data.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(80.dp),
                    maxLines = 1
                )

                Box(modifier = Modifier.weight(1f).height(20.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = if (maxValue > 0) (item.value / maxValue) * size.width else 0f
                        drawRect(
                            color = item.color,
                            topLeft = Offset.Zero,
                            size = Size(barWidth, size.height)
                        )
                    }
                }

                Text(
                    text = "${"%.1f".format(item.value)}$unit",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(60.dp).padding(start = 4.dp)
                )
            }
        }
    }
}
