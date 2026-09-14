package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.model.VaultCurrency
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.BurnishedGold
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepAntiqueGold
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.LightGoldShimmer
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

@Composable
fun CoinPileProgressSection(
    primaryGoal: SavingsGoalEntity?,
    currency: VaultCurrency = VaultCurrency.USD,
    onDepositGold: (Double) -> Unit,
    modifier: Modifier = Modifier
) {

    val rawTitle = primaryGoal?.title ?: "Total Savings & Reserves"
    val goalTitle = if (rawTitle == "The Grand Treasury Reserve") "Total Savings & Reserves" else rawTitle
    val currentAmount = primaryGoal?.currentAmount ?: 32800.00
    val targetAmount = primaryGoal?.targetAmount ?: 50000.00
    val progressFraction = (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "coin_pile_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "gold_shimmer")
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_phase"
    )

    WoodCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("savings_coin_pile_section"),
        goldAccentBorder = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Treasury",
                        tint = SovereignGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAVINGS & RESERVES",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 12.sp,
                            color = SovereignGold
                        )
                    )
                }

                Text(
                    text = "${(animatedProgress * 100).toInt()}% COMPILED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = SovereignGold
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = goalTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SovereignGold
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Two Labeled Numbers: Total Saved & Monthly Progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DeepOakBrown)
                    .border(1.dp, RichOakBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Saved",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currency.format(currentAmount, false),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 18.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(RichOakBorder)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Monthly Progress",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${(animatedProgress * 100).toInt()}% (${currency.format(targetAmount, false)})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rich Custom Canvas: Gold Coins Piling Up!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DeepOakBrown, Color(0xFF1E130B))
                        )
                    )
                    .border(1.dp, RichOakBorder, RoundedCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val platformY = h - 22f

                    // Draw golden shelf line
                    drawLine(
                        color = SovereignGold.copy(alpha = 0.5f),
                        start = Offset(20f, platformY - 2f),
                        end = Offset(w - 20f, platformY - 2f),
                        strokeWidth = 1f
                    )

                    // Draw Stacks of Gold Coins dynamically based on progress
                    val numColumns = 9
                    val colSpacing = (w - 60f) / (numColumns - 1)
                    val maxCoinsPerCol = 14

                    val coinWidth = 26f
                    val coinHeight = 7f

                    val totalMaxCoins = numColumns * maxCoinsPerCol
                    val coinsToDraw = (animatedProgress * totalMaxCoins).toInt().coerceAtLeast(4)

                    var drawn = 0
                    for (level in 0 until maxCoinsPerCol) {
                        for (col in 0 until numColumns) {
                            if (drawn >= coinsToDraw) break

                            // Add a pyramid / organic pile distribution
                            val colOffsetFromCenter = kotlin.math.abs(col - (numColumns / 2))
                            if (level > (maxCoinsPerCol - colOffsetFromCenter * 2)) continue

                            val cx = 30f + col * colSpacing + (if (level % 2 == 0) 0f else 3f)
                            val cy = platformY - 6f - (level * 6.5f)

                            val goldGrad = Brush.verticalGradient(
                                listOf(
                                    LightGoldShimmer,
                                    LustrousGold,
                                    SovereignGold,
                                    DeepAntiqueGold
                                ),
                                startY = cy - coinHeight / 2f,
                                endY = cy + coinHeight / 2f
                            )

                            // Coin 3D Cylinder Ellipse body
                            drawOval(
                                brush = goldGrad,
                                topLeft = Offset(cx - coinWidth / 2f, cy - coinHeight / 2f),
                                size = Size(coinWidth, coinHeight)
                            )
                            // Outer golden rim
                            drawOval(
                                color = BurnishedGold,
                                topLeft = Offset(cx - coinWidth / 2f, cy - coinHeight / 2f),
                                size = Size(coinWidth, coinHeight),
                                style = Stroke(width = 1f)
                            )

                            drawn++
                        }
                    }

                    // Shimmer Sparkle particles near top of pile
                    if (coinsToDraw > 10) {
                        val sparkleX = (w * 0.4f) + (shimmerPhase * (w * 0.2f))
                        val sparkleY = platformY - (animatedProgress * 65f) - 10f
                        drawCircle(
                            color = LightGoldShimmer.copy(alpha = 0.8f),
                            radius = 3.5f,
                            center = Offset(sparkleX, sparkleY)
                        )
                    }
                }

                // Overlay Status in Monospace Gold
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currency.format(currentAmount)}  /  ${currency.format(targetAmount)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Deposit Gold Action Buttons (+ $250, + $500, + $1,000)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(250.0, 500.0, 1000.0).forEach { depositAmount ->
                    val interaction = remember { MutableInteractionSource() }
                    val isPressed by interaction.collectIsPressedAsState()
                    val btnBorder by animateColorAsState(
                        targetValue = if (isPressed) BurgundyGlow else SovereignGold.copy(alpha = 0.5f),
                        animationSpec = tween(150),
                        label = "coin_btn_glow"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepOakBrown)
                            .border(
                                width = if (isPressed) 2.dp else 1.dp,
                                color = btnBorder,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                onClick = { onDepositGold(depositAmount) }
                            )
                            .padding(vertical = 10.dp)
                            .testTag("deposit_gold_${depositAmount.toInt()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = SovereignGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "+${currency.symbol}${depositAmount.toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = SovereignGold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
