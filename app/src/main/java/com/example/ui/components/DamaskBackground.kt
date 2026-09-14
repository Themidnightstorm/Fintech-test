package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.theme.RichBurgundy

/**
 * Renders a rich burgundy base background (#7A3B3A) with an authentic,
 * subtle damask pattern overlay using a GPU-accelerated repeating tile shader.
 */
@Composable
fun DamaskBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val damaskBrush = remember {
        createDamaskShaderBrush()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RichBurgundy)
            .drawBehind {
                drawRect(brush = damaskBrush)
            }
    ) {
        content()
    }
}

private fun createDamaskShaderBrush(): ShaderBrush {
    val tileWidth = 160
    val tileHeight = 200
    val bitmap = ImageBitmap(tileWidth, tileHeight)
    val canvas = Canvas(bitmap)
    val drawScope = CanvasDrawScope()

    val damaskColor = Color(0xFFF5E6D3).copy(alpha = 0.15f)
    val damaskGoldAccent = Color(0xFFC9A84C).copy(alpha = 0.12f)
    val damaskShadow = Color(0xFF3B1E1E).copy(alpha = 0.15f)

    drawScope.draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(tileWidth.toFloat(), tileHeight.toFloat())
    ) {
        // Draw primary motif in center
        drawDamaskMotif(
            center = Offset(80f, 100f),
            color = damaskColor,
            accentColor = damaskGoldAccent,
            shadowColor = damaskShadow
        )
        // Draw 4 corner motifs for seamless tiling
        drawDamaskMotif(
            center = Offset(0f, 0f),
            color = damaskColor,
            accentColor = damaskGoldAccent,
            shadowColor = damaskShadow
        )
        drawDamaskMotif(
            center = Offset(160f, 0f),
            color = damaskColor,
            accentColor = damaskGoldAccent,
            shadowColor = damaskShadow
        )
        drawDamaskMotif(
            center = Offset(0f, 200f),
            color = damaskColor,
            accentColor = damaskGoldAccent,
            shadowColor = damaskShadow
        )
        drawDamaskMotif(
            center = Offset(160f, 200f),
            color = damaskColor,
            accentColor = damaskGoldAccent,
            shadowColor = damaskShadow
        )
    }

    val shader = ImageShader(
        image = bitmap,
        tileModeX = TileMode.Repeated,
        tileModeY = TileMode.Repeated
    )
    return ShaderBrush(shader)
}

/**
 * Draws an intricate Baroque/Damask diamond & fleur-de-lis floral scroll medallion.
 */
private fun DrawScope.drawDamaskMotif(
    center: Offset,
    color: Color,
    accentColor: Color,
    shadowColor: Color
) {
    val cx = center.x
    val cy = center.y
    val scale = 0.85f

    // 1. Subtle Diamond Lattice curves connecting centers
    val latticePath = Path().apply {
        moveTo(cx, cy - 36f * scale)
        cubicTo(
            cx + 24f * scale, cy - 18f * scale,
            cx + 24f * scale, cy + 18f * scale,
            cx, cy + 36f * scale
        )
        cubicTo(
            cx - 24f * scale, cy + 18f * scale,
            cx - 24f * scale, cy - 18f * scale,
            cx, cy - 36f * scale
        )
        close()
    }
    drawPath(latticePath, shadowColor, style = Stroke(width = 1.8f))
    drawPath(latticePath, color, style = Stroke(width = 1.2f))

    // 2. Central Fleur-de-lis / Palmette floral crest
    val crestPath = Path().apply {
        // Center Petal / Spearhead
        moveTo(cx, cy - 22f * scale)
        cubicTo(
            cx + 6f * scale, cy - 12f * scale,
            cx + 4f * scale, cy - 4f * scale,
            cx, cy
        )
        cubicTo(
            cx - 4f * scale, cy - 4f * scale,
            cx - 6f * scale, cy - 12f * scale,
            cx, cy - 22f * scale
        )

        // Left Flourish Wing
        moveTo(cx - 2f * scale, cy - 3f * scale)
        cubicTo(
            cx - 14f * scale, cy - 14f * scale,
            cx - 18f * scale, cy - 2f * scale,
            cx - 8f * scale, cy + 8f * scale
        )
        cubicTo(
            cx - 4f * scale, cy + 10f * scale,
            cx - 2f * scale, cy + 4f * scale,
            cx, cy + 2f * scale
        )

        // Right Flourish Wing
        moveTo(cx + 2f * scale, cy - 3f * scale)
        cubicTo(
            cx + 14f * scale, cy - 14f * scale,
            cx + 18f * scale, cy - 2f * scale,
            cx + 8f * scale, cy + 8f * scale
        )
        cubicTo(
            cx + 4f * scale, cy + 10f * scale,
            cx + 2f * scale, cy + 4f * scale,
            cx, cy + 2f * scale
        )

        // Bottom Acanthus Tail Droplet
        moveTo(cx, cy + 3f * scale)
        cubicTo(
            cx + 5f * scale, cy + 12f * scale,
            cx, cy + 20f * scale,
            cx, cy + 22f * scale
        )
        cubicTo(
            cx, cy + 20f * scale,
            cx - 5f * scale, cy + 12f * scale,
            cx, cy + 3f * scale
        )
    }

    drawPath(crestPath, accentColor, style = Stroke(width = 1.4f))
    drawPath(crestPath, color, style = Stroke(width = 0.9f))

    // 3. Four corner decorative florets
    val dotRadius = 1.4f * scale
    drawCircle(color, radius = dotRadius, center = Offset(cx, cy - 30f * scale))
    drawCircle(color, radius = dotRadius, center = Offset(cx, cy + 30f * scale))
    drawCircle(color, radius = dotRadius, center = Offset(cx - 24f * scale, cy))
    drawCircle(color, radius = dotRadius, center = Offset(cx + 24f * scale, cy))
}
