package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SlipType
import com.example.data.model.VaultCurrency
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.GreenSealInk
import com.example.ui.theme.LightGoldShimmer
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PolishedOak
import com.example.ui.theme.RedStampInk
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

@Composable
fun BankSlipCard(
    slipType: SlipType,
    amount: Double,
    monthlyTarget: Double,
    entryCount: Int,
    currency: VaultCurrency = VaultCurrency.USD,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val (title, codeNumber, stampText, stampColor) = when (slipType) {
        SlipType.EXPENSES -> Quadruple(
            "EXPENSES SLIP",
            "SLIP #01-EXP",
            "SPENDING",
            RedStampInk
        )
        SlipType.SAVINGS -> Quadruple(
            "SAVINGS SLIP",
            "SLIP #02-SAV",
            "SAVINGS",
            GreenSealInk
        )
        SlipType.WANTS -> Quadruple(
            "WANTS SLIP",
            "SLIP #03-WNT",
            "WANTS",
            SovereignGold
        )
    }

    val progressFraction = if (monthlyTarget > 0) (amount / monthlyTarget).toFloat().coerceIn(0f, 1f) else 0.5f
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "slip_progress"
    )

    // Slip Card Container - Dark Oak wood texture (#3B2A1E) with slight shadow and border
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp), ambientColor = ObsidianBlack, spotColor = ObsidianBlack)
            .testTag("bank_slip_${slipType.name.lowercase()}"),
        color = DarkOakBrown,
        border = BorderStroke(1.dp, RichOakBorder)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Subtle wood grain texture and ticket perforation
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height

                // Subtle wood grain lines
                val grainColor1 = PolishedOak.copy(alpha = 0.22f)
                val grainColor2 = DeepOakBrown.copy(alpha = 0.35f)
                var y = 6f
                var i = 0
                while (y < h) {
                    val waveOffset = (y * 0.35f) % 30f
                    drawLine(
                        color = if (i % 2 == 0) grainColor1 else grainColor2,
                        start = Offset(0f, y),
                        end = Offset(w, y + (waveOffset - 15f) * 0.15f),
                        strokeWidth = 1.0f
                    )
                    y += 10f
                    i++
                }

                // Left perforated ticket edge line
                val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f), 0f)
                drawLine(
                    color = SovereignGold.copy(alpha = 0.3f),
                    start = Offset(24f, 0f),
                    end = Offset(24f, h),
                    strokeWidth = 1f,
                    pathEffect = dashedEffect
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 34.dp, end = 18.dp, top = 16.dp, bottom = 16.dp)
            ) {
                // Header Row: Code & Stamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = codeNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MutedCream,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        )
                    )

                    // Aristocratic Ledger Stamp Badge
                    Box(
                        modifier = Modifier
                            .border(1.dp, stampColor.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                            .background(stampColor.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stampText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (slipType == SlipType.WANTS) SovereignGold else stampColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title and Main Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "$entryCount recorded entries",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Monospace Gold Amount with Dark Shadow/Plate
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepOakBrown)
                            .border(1.dp, SovereignGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = currency.format(amount, false),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = LustrousGold,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Special Feature for SAVINGS SLIP: Interactive Gold Coin Counter!
                if (slipType == SlipType.SAVINGS) {
                    GoldCoinCounterRow(
                        currentAmount = amount,
                        targetAmount = monthlyTarget,
                        progress = animatedProgress
                    )
                } else {
                    // Standard Ledger Allocation Bar
                    StandardAllocationBar(
                        progress = animatedProgress,
                        label = if (slipType == SlipType.EXPENSES) "Monthly Limit: ${currency.format(monthlyTarget, false)}" else "Monthly Limit: ${currency.format(monthlyTarget, false)}"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom row: tap hint (View Slip Details link)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View Slip Details",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Inspect",
                        tint = SovereignGold,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoldCoinCounterRow(
    currentAmount: Double,
    targetAmount: Double,
    progress: Float
) {
    val totalCoins = 10
    val filledCoins = (progress * totalCoins).toInt().coerceIn(0, totalCoins)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Gold Coins",
                    tint = SovereignGold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "GOLD COIN COUNTER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SovereignGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }

            Text(
                text = "${(progress * 100).toInt()}% Stored",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = LustrousGold,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Visual Gold Coin Tokens Array that fills up
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(DeepOakBrown)
                .border(1.dp, RichOakBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until totalCoins) {
                val isFilled = i < filledCoins
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFilled) SovereignGold else Color(0x333B2A1E)
                        )
                        .border(
                            1.dp,
                            if (isFilled) LustrousGold else RichOakBorder,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isFilled) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Coin shine
                            drawCircle(
                                color = LightGoldShimmer,
                                radius = size.minDimension / 4f,
                                center = Offset(size.width * 0.35f, size.height * 0.35f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StandardAllocationBar(
    progress: Float,
    label: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    color = ParchmentCream,
                    fontSize = 10.sp
                )
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = LustrousGold,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DeepOakBrown)
                .border(0.5.dp, RichOakBorder, RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SovereignGold)
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
