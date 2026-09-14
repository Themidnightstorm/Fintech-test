package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PolishedOak
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

/**
 * Aristocratic Dark Oak Card (#3B2A1E):
 * - Dark oak wood texture base
 * - Subtle wood grain texture lines
 * - Clear border and shadow separation against the burgundy damask canvas
 */
@Composable
fun WoodCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    border: BorderStroke? = BorderStroke(1.dp, RichOakBorder),
    goldAccentBorder: Boolean = false,
    tonalElevation: Dp = 4.dp,
    shadowElevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val finalBorder = if (goldAccentBorder) {
        BorderStroke(1.dp, SovereignGold.copy(alpha = 0.5f))
    } else {
        border ?: BorderStroke(1.dp, RichOakBorder)
    }

    Surface(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = ObsidianBlack,
                spotColor = ObsidianBlack
            )
            .clip(shape),
        shape = shape,
        color = DarkOakBrown,
        border = finalBorder,
        tonalElevation = tonalElevation
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle procedural wood grain texture overlay
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height

                // Subtle organic wood grain waves & rings
                val grainColor1 = PolishedOak.copy(alpha = 0.22f)
                val grainColor2 = DeepOakBrown.copy(alpha = 0.38f)
                val grainHighlight = SovereignGold.copy(alpha = 0.04f)

                var y = 4f
                var lineIndex = 0
                while (y < h + 10f) {
                    val waveOffset = (y * 0.35f) % 36f
                    val strokeColor = when (lineIndex % 4) {
                        0 -> grainHighlight
                        1, 3 -> grainColor1
                        else -> grainColor2
                    }

                    drawLine(
                        color = strokeColor,
                        start = Offset(0f, y),
                        end = Offset(w, y + (waveOffset - 18f) * 0.22f),
                        strokeWidth = if (lineIndex % 3 == 0) 1.5f else 1.0f
                    )
                    y += 9f
                    lineIndex++
                }
            }

            content()
        }
    }
}
