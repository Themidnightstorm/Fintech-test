package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FinancialRoadmapData
import com.example.data.model.MilestoneStatus
import com.example.data.model.RecommendedAction
import com.example.data.model.RoadmapMilestone
import com.example.data.model.VaultCurrency
import com.example.ui.components.GoldButton
import com.example.ui.components.ThinGoldDivider
import com.example.ui.components.WoodCard
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PolishedOak
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizedRoadmapModal(
    roadmapData: FinancialRoadmapData,
    isPremium: Boolean,
    onUpgradeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val checkedActions = remember { mutableStateMapOf<String, Boolean>() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkOakBrown,
        dragHandle = null,
        modifier = Modifier.testTag("personalized_roadmap_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DeepOakBrown)
                            .border(1.dp, SovereignGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = SovereignGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "PREMIER WEALTH ROADMAP",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                letterSpacing = 1.3.sp
                            )
                        )
                        Text(
                            text = "Personalized Wealth Trajectory & Milestones",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_roadmap_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SovereignGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ThinGoldDivider(alpha = 0.4f)
            Spacer(modifier = Modifier.height(14.dp))

            if (!isPremium) {
                // Non-Premium Locked Preview
                RoadmapPremiumLockView(
                    onUpgradeClick = onUpgradeClick,
                    currency = roadmapData.currency
                )
            } else {
                // Full Interactive Roadmap Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Section 1: Financial Independence Progress Bar & Target Header
                    FiProgressHeroCard(
                        roadmapData = roadmapData
                    )

                    // Section 2: Recommended Monthly Actions
                    RecommendedActionsSection(
                        actions = roadmapData.recommendedActions,
                        checkedActions = checkedActions,
                        currency = roadmapData.currency,
                        onToggleAction = { id ->
                            checkedActions[id] = !(checkedActions[id] ?: false)
                        }
                    )

                    // Section 3: Visual Milestone Timeline
                    VisualMilestoneTimelineSection(
                        milestones = roadmapData.milestones,
                        currency = roadmapData.currency
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun FiProgressHeroCard(
    roadmapData: FinancialRoadmapData
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (roadmapData.fiProgressPercent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "fi_progress"
    )

    WoodCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fi_progress_hero_card"),
        goldAccentBorder = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "FINANCIAL INDEPENDENCE (FI) TRAJECTORY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 11.sp,
                            letterSpacing = 1.1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format(Locale.US, "%.1f", roadmapData.fiProgressPercent)}% to Freedom",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 24.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DeepOakBrown,
                    border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "EST. HORIZON",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = SovereignGold,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "~${String.format(Locale.US, "%.1f", roadmapData.estimatedYearsToFi)} Yrs",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = ParchmentCream,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            // Glowing Dual-Tone Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(DeepOakBrown)
                        .border(1.dp, SovereignGold.copy(alpha = 0.5f), RoundedCornerShape(7.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF8A2E2B),
                                        SovereignGold,
                                        LustrousGold
                                    )
                                )
                            )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current Net Worth: ${roadmapData.currency.format(roadmapData.currentNetWorth, false)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = ParchmentCream,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "FI Target (25x): ${roadmapData.currency.format(roadmapData.fiTargetNumber, false)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = BurgundyDeep.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SovereignGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Based on your monthly surplus capacity of ${roadmapData.currency.format(roadmapData.monthlySavingsCapacity, false)}/mo deployed into private compounding reserves.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedActionsSection(
    actions: List<RecommendedAction>,
    checkedActions: Map<String, Boolean>,
    currency: VaultCurrency,
    onToggleAction: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECOMMENDED MONTHLY DIRECTIVES",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                    fontSize = 12.sp,
                    color = SovereignGold
                )
            )
            Text(
                text = "Based on Current Stage",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    color = ParchmentCream,
                    fontSize = 10.sp
                )
            )
        }

        actions.forEach { action ->
            val isChecked = checkedActions[action.id] ?: false
            WoodCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleAction(action.id) }
                    .testTag("action_card_${action.id}"),
                goldAccentBorder = isChecked
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isChecked) SovereignGold else DeepOakBrown)
                            .border(1.dp, SovereignGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChecked) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = ObsidianBlack,
                                modifier = Modifier.size(15.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Pending",
                                tint = SovereignGold.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = action.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChecked) MutedCream else SovereignGold,
                                    fontSize = 13.sp
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DeepOakBrown,
                                border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = action.priorityTag,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 8.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = action.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualMilestoneTimelineSection(
    milestones: List<RoadmapMilestone>,
    currency: VaultCurrency
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STRATEGIC MILESTONE TIMELINE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                    fontSize = 12.sp,
                    color = SovereignGold
                )
            )
            Text(
                text = "Chronological Milestones",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    color = ParchmentCream,
                    fontSize = 10.sp
                )
            )
        }

        milestones.forEachIndexed { index, milestone ->
            val isLast = index == milestones.size - 1
            MilestoneTimelineRow(
                milestone = milestone,
                isLast = isLast,
                currency = currency
            )
        }
    }
}

@Composable
private fun MilestoneTimelineRow(
    milestone: RoadmapMilestone,
    isLast: Boolean,
    currency: VaultCurrency
) {
    val nodeColor = when (milestone.status) {
        MilestoneStatus.COMPLETED -> SovereignGold
        MilestoneStatus.IN_PROGRESS -> LustrousGold
        MilestoneStatus.UPCOMING -> RichOakBorder
    }

    val iconVector: ImageVector = when (milestone.iconType) {
        "emergency" -> Icons.Default.Shield
        "debt" -> Icons.Default.CreditCard
        "citadel" -> Icons.Default.AccountBalance
        "investing" -> Icons.AutoMirrored.Filled.ShowChart
        "goal" -> Icons.Default.Flag
        "fire" -> Icons.Default.Stars
        else -> Icons.Default.MonetizationOn
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Timeline Spine + Node Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DeepOakBrown)
                    .border(2.dp, nodeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (milestone.status == MilestoneStatus.COMPLETED) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = SovereignGold,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = if (milestone.status == MilestoneStatus.IN_PROGRESS) SovereignGold else MutedCream.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(86.dp)
                        .background(
                            if (milestone.status == MilestoneStatus.COMPLETED) SovereignGold.copy(alpha = 0.8f)
                            else RichOakBorder.copy(alpha = 0.6f)
                        )
                )
            }
        }

        // Milestone Content Card
        WoodCard(
            modifier = Modifier
                .weight(1f)
                .testTag("milestone_card_${milestone.id}"),
            goldAccentBorder = milestone.status == MilestoneStatus.IN_PROGRESS
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 14.sp
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (milestone.status) {
                            MilestoneStatus.COMPLETED -> DeepOakBrown
                            MilestoneStatus.IN_PROGRESS -> BurgundyDeep
                            MilestoneStatus.UPCOMING -> DeepOakBrown
                        },
                        border = BorderStroke(
                            1.dp,
                            when (milestone.status) {
                                MilestoneStatus.COMPLETED -> SovereignGold
                                MilestoneStatus.IN_PROGRESS -> SovereignGold
                                MilestoneStatus.UPCOMING -> RichOakBorder
                            }
                        )
                    ) {
                        Text(
                            text = when (milestone.status) {
                                MilestoneStatus.COMPLETED -> "COMPLETED"
                                MilestoneStatus.IN_PROGRESS -> "CURRENT FOCUS"
                                MilestoneStatus.UPCOMING -> "UPCOMING"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = when (milestone.status) {
                                    MilestoneStatus.COMPLETED -> SovereignGold
                                    MilestoneStatus.IN_PROGRESS -> LustrousGold
                                    MilestoneStatus.UPCOMING -> ParchmentCream.copy(alpha = 0.7f)
                                },
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = milestone.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = ParchmentCream,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                // Progress Bar for In-Progress/Completed items
                if (milestone.status != MilestoneStatus.UPCOMING) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { milestone.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = SovereignGold,
                            trackColor = DeepOakBrown,
                            strokeCap = StrokeCap.Round
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${milestone.progressPercent}% Funded",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = SovereignGold,
                                    fontSize = 9.sp
                                )
                            )
                            Text(
                                text = milestone.estimatedDateText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = ParchmentCream,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Horizon:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = MutedCream,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = milestone.estimatedDateText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = SovereignGold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Text(
                    text = milestone.guidance,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = ParchmentCream.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun RoadmapPremiumLockView(
    onUpgradeClick: () -> Unit,
    currency: VaultCurrency
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(DeepOakBrown)
                .border(2.dp, SovereignGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.the_vault_circular_badge_1788026210267),
                contentDescription = "Premier Vault Seal",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Text(
            text = "Personalized Financial Roadmap",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = SovereignGold,
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Exclusive to Vault+ Members. Unlock your complete mathematical milestone timeline, Financial Independence horizon progress bar, and custom monthly action directives calculated from your real income, expenses, and savings goals.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.SansSerif,
                color = ParchmentCream,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Feature Highlights
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = DeepOakBrown,
            border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RoadmapPerkItem("Milestone Timeline: Emergency Fund, Debt-Free Date, & Investments")
                RoadmapPerkItem("Financial Independence (FI) Progress Bar with 25x Expense Multiplier")
                RoadmapPerkItem("Actionable Monthly Directives Tailored to Your Living Surplus")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GoldButton(
            text = "UNLOCK WITH PRO • $7.99/MO",
            onClick = onUpgradeClick,
            leadingIcon = Icons.Default.Shield,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("unlock_roadmap_upgrade_button")
        )
    }
}

@Composable
private fun RoadmapPerkItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SovereignGold,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.SansSerif,
                color = ParchmentCream,
                fontSize = 12.sp
            )
        )
    }
}
