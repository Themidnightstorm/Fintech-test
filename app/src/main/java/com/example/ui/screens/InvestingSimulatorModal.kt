package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultCurrency
import com.example.ui.components.ThinGoldDivider
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold
import com.example.ui.viewmodel.InvestingSimState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestingSimulatorModal(
    simState: InvestingSimState,
    currency: VaultCurrency = VaultCurrency.USD,
    onUpdateSim: (
        initialCapital: Double?,
        monthlyDeposit: Double?,
        years: Int?,
        stocksWeight: Float?,
        realEstateWeight: Float?,
        goldWeight: Float?,
        bondsWeight: Float?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianBlack,
        dragHandle = null,
        modifier = Modifier.testTag("investing_simulator_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Simulator",
                        tint = SovereignGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "INVESTING SIMULATOR",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            color = SovereignGold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_simulator_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SovereignGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ThinGoldDivider(alpha = 0.35f)
            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Projection Outcome Card in Monospace Gold
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = DeepOakBrown,
                    border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "PROJECTED INVESTMENT BALANCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SovereignGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currency.format(simState.futureNetWorth, false),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = LustrousGold,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total Contributed",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MutedCream)
                                )
                                Text(
                                    text = currency.format(simState.totalDeposited, false),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = ParchmentCream
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Growth & Interest",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MutedCream)
                                )
                                Text(
                                    text = "+${currency.format(simState.compoundGain, false)}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = SovereignGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        ThinGoldDivider(alpha = 0.2f)
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Weighted Annual Yield: ${(simState.weightedAnnualReturn * 100).toInt()}% • Horizon: ${simState.years} Years",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LustrousGold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Slider 1: Initial Starting Amount
                Text(
                    text = "Starting Amount: ${currency.format(simState.initialCapital, false)}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Serif,
                        color = SovereignGold
                    )
                )
                Slider(
                    value = simState.initialCapital.toFloat(),
                    onValueChange = { onUpdateSim(it.toDouble(), null, null, null, null, null, null) },
                    valueRange = 0f..250000f,
                    steps = 24,
                    colors = SliderDefaults.colors(
                        thumbColor = SovereignGold,
                        activeTrackColor = SovereignGold,
                        inactiveTrackColor = DarkOakBrown
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Slider 2: Monthly Savings
                Text(
                    text = "Monthly Savings: ${currency.format(simState.monthlyDeposit, false)}/mo",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Serif,
                        color = SovereignGold
                    )
                )
                Slider(
                    value = simState.monthlyDeposit.toFloat(),
                    onValueChange = { onUpdateSim(null, it.toDouble(), null, null, null, null, null) },
                    valueRange = 100f..10000f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = SovereignGold,
                        activeTrackColor = SovereignGold,
                        inactiveTrackColor = DarkOakBrown
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Slider 3: Years Horizon
                Text(
                    text = "Investment Timeline: ${simState.years} Years",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Serif,
                        color = SovereignGold
                    )
                )
                Slider(
                    value = simState.years.toFloat(),
                    onValueChange = { onUpdateSim(null, null, it.toInt(), null, null, null, null) },
                    valueRange = 5f..40f,
                    steps = 34,
                    colors = SliderDefaults.colors(
                        thumbColor = SovereignGold,
                        activeTrackColor = SovereignGold,
                        inactiveTrackColor = DarkOakBrown
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                ThinGoldDivider(alpha = 0.25f)
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "INVESTMENT ASSET MIX",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SovereignGold,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                AssetWeightRow(
                    name = "Stock Index Funds (10% avg)",
                    weight = simState.stocksWeight,
                    onWeightChange = { onUpdateSim(null, null, null, it, null, null, null) }
                )
                AssetWeightRow(
                    name = "Real Estate (8% avg)",
                    weight = simState.realEstateWeight,
                    onWeightChange = { onUpdateSim(null, null, null, null, it, null, null) }
                )
                AssetWeightRow(
                    name = "Gold & Commodities (6% avg)",
                    weight = simState.goldWeight,
                    onWeightChange = { onUpdateSim(null, null, null, null, null, it, null) }
                )
                AssetWeightRow(
                    name = "Bonds & Treasury Notes (4% avg)",
                    weight = simState.bondsWeight,
                    onWeightChange = { onUpdateSim(null, null, null, null, null, null, it) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AssetWeightRow(
    name: String,
    weight: Float,
    onWeightChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall.copy(color = ParchmentCream)
            )
            Text(
                text = "${(weight * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = SovereignGold,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Slider(
            value = weight,
            onValueChange = onWeightChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = LustrousGold,
                activeTrackColor = SovereignGold,
                inactiveTrackColor = DarkOakBrown
            )
        )
    }
}
