package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SovereignGold

/**
 * Aristocratic Gold Button:
 * - Gold (#C9A84C) with a subtle metallic gradient
 * - On hover/tap: Rich Burgundy glow (#7A3B3B)
 */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Metallic gold gradient: #B58E35 -> #C9A84C -> #E5CC82 -> #C9A84C
    val metallicGoldGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFB58E35),
            SovereignGold,
            Color(0xFFE5CC82),
            SovereignGold,
            Color(0xFFB58E35)
        )
    )

    val pressedGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFA67D28),
            Color(0xFFB89339),
            Color(0xFFD6BC6E),
            Color(0xFFB89339),
            Color(0xFFA67D28)
        )
    )

    // Animated burgundy glow border when tapped/pressed
    val borderColor by animateColorAsState(
        targetValue = if (isPressed) BurgundyGlow else Color(0x66C9A84C),
        animationSpec = tween(150),
        label = "button_border_color"
    )

    val glowShadowElevation: Dp = if (isPressed) 8.dp else 4.dp

    Box(
        modifier = modifier
            .shadow(
                elevation = glowShadowElevation,
                shape = shape,
                ambientColor = if (isPressed) BurgundyGlow else SovereignGold,
                spotColor = if (isPressed) BurgundyGlow else SovereignGold
            )
            .clip(shape)
            .background(if (isPressed) pressedGradient else metallicGoldGradient)
            .border(
                BorderStroke(
                    width = if (isPressed) 2.dp else 1.dp,
                    color = borderColor
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ObsidianBlack,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
            )
            trailingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ObsidianBlack,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
