package com.codeint.dibenchmark.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.codeint.dibenchmark.annotations.FrameworkType
import com.codeint.dibenchmark.ui.theme.BenchmarkColors

@Composable
fun FrameworkBadge(
    framework: FrameworkType,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (framework) {
        FrameworkType.HILT -> BenchmarkColors.Hilt to "Hilt"
        FrameworkType.METRO -> BenchmarkColors.Metro to "Metro"
        FrameworkType.UNKNOWN -> BenchmarkColors.Warning to "?"
    }

    Text(
        text = if (compact) label.first().toString() else label,
        style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(horizontal = if (compact) 4.dp else 8.dp, vertical = 2.dp)
    )
}
