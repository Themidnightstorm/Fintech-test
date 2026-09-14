package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.SovereignGold

@Composable
fun ThinGoldDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    alpha: Float = 0.5f,
    showEmblem: Boolean = false
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(if (showEmblem) 14.dp else thickness)
            .padding(horizontal = 8.dp)
    ) {
        val yCenter = size.height / 2f
        val w = size.width

        val goldBrush = Brush.linearGradient(
            0.0f to Color.Transparent,
            0.2f to SovereignGold.copy(alpha = alpha),
            0.5f to LustrousGold.copy(alpha = alpha + 0.3f),
            0.8f to SovereignGold.copy(alpha = alpha),
            1.0f to Color.Transparent,
            start = Offset(0f, yCenter),
            end = Offset(w, yCenter)
        )

        drawLine(
            brush = goldBrush,
            start = Offset(0f, yCenter),
            end = Offset(w, yCenter),
            strokeWidth = thickness.toPx()
        )

        if (showEmblem) {
            val midX = w / 2f
            // Small diamond crest in center
            drawCircle(
                color = SovereignGold.copy(alpha = 0.9f),
                radius = 3.dp.toPx(),
                center = Offset(midX, yCenter)
            )
        }
    }
}
