package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultCurrency
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

@Composable
fun NetWorthCard(
    netWorth: Double,
    monthlyIncome: Double,
    currency: VaultCurrency = VaultCurrency.USD,
    onAddDeposit: () -> Unit,
    onOpenTutor: () -> Unit,
    modifier: Modifier = Modifier
) {

    WoodCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("net_worth_card"),
        goldAccentBorder = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Card Title with clean gold indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(SovereignGold, CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "TOTAL NET WORTH",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 1.8.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SovereignGold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Net Worth Display in Monospace Gold
            Text(
                text = currency.format(netWorth),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp,
                    color = SovereignGold
                ),
                modifier = Modifier.testTag("net_worth_value_text")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Stats Bar (Compounded Growth + Cash Flow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "Growth",
                        tint = SovereignGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "+4.8% Compounded (30d)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SovereignGold,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = "Income: ${currency.format(monthlyIncome, false)}/mo",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = ParchmentCream,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            ThinGoldDivider(alpha = 0.25f)
            Spacer(modifier = Modifier.height(16.dp))

            // Action Strip: Record Entry + Single The Curator AI access
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val depositInteraction = remember { MutableInteractionSource() }
                val isDepositPressed by depositInteraction.collectIsPressedAsState()
                val depositBorder by animateColorAsState(
                    targetValue = if (isDepositPressed) BurgundyGlow else Color(0x66C9A84C),
                    animationSpec = tween(150),
                    label = "dep_glow"
                )

                // Deposit / Add Entry Button with metallic gold gradient & burgundy glow on tap
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = if (isDepositPressed) 6.dp else 2.dp,
                            shape = RoundedCornerShape(8.dp),
                            ambientColor = if (isDepositPressed) BurgundyGlow else SovereignGold,
                            spotColor = if (isDepositPressed) BurgundyGlow else SovereignGold
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFB58E35),
                                    SovereignGold,
                                    Color(0xFFE5CC82),
                                    SovereignGold,
                                    Color(0xFFB58E35)
                                )
                            )
                        )
                        .border(
                            width = if (isDepositPressed) 2.dp else 1.dp,
                            color = depositBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            interactionSource = depositInteraction,
                            indication = null,
                            onClick = onAddDeposit
                        )
                        .padding(vertical = 12.dp)
                        .testTag("quick_deposit_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Record Entry",
                            tint = ObsidianBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "RECORD ENTRY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBlack,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                val aiInteraction = remember { MutableInteractionSource() }
                val isAiPressed by aiInteraction.collectIsPressedAsState()
                val aiBorder by animateColorAsState(
                    targetValue = if (isAiPressed) BurgundyGlow else SovereignGold.copy(alpha = 0.7f),
                    animationSpec = tween(150),
                    label = "ai_glow"
                )

                // The Curator AI Consultation Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = if (isAiPressed) 6.dp else 2.dp,
                            shape = RoundedCornerShape(8.dp),
                            ambientColor = if (isAiPressed) BurgundyGlow else ObsidianBlack,
                            spotColor = if (isAiPressed) BurgundyGlow else ObsidianBlack
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepOakBrown)
                        .border(
                            width = if (isAiPressed) 2.dp else 1.dp,
                            color = aiBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            interactionSource = aiInteraction,
                            indication = null,
                            onClick = onOpenTutor
                        )
                        .padding(vertical = 12.dp)
                        .testTag("open_sovereign_ai_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "The Curator AI",
                            tint = SovereignGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "THE CURATOR AI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
