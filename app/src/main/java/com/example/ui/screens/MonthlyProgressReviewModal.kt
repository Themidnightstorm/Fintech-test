package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MonthlyReviewData
import com.example.data.model.ReviewMilestoneBadge
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
import com.example.util.NotificationHelper
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyProgressReviewModal(
    reviewData: MonthlyReviewData,
    isPremium: Boolean,
    onUpgradeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    var notificationSentText by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkOakBrown,
        dragHandle = null,
        modifier = Modifier.testTag("monthly_progress_review_bottom_sheet")
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
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = SovereignGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "MONTHLY AUDIT REPORT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                letterSpacing = 1.3.sp
                            )
                        )
                        Text(
                            text = reviewData.reviewPeriod,
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
                    modifier = Modifier.testTag("close_monthly_review_button")
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
                // Non-Premium Locked View
                MonthlyReviewPremiumLockView(
                    onUpgradeClick = onUpgradeClick,
                    currency = reviewData.currency
                )
            } else {
                // Full Report Card View
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Aristocratic Report Card Header with Seal & Standing Grade
                    ReportCardStandingBanner(
                        reviewData = reviewData
                    )

                    // 4 Core Financial Metrics Grid
                    MonthlyMetricsBreakdownSection(
                        reviewData = reviewData
                    )

                    // Milestones Achieved (Badge Collection)
                    MilestonesAchievedSection(
                        badges = reviewData.badgesEarned
                    )

                    // Recommendations for Next Month
                    NextMonthDirectivesSection(
                        directives = reviewData.nextMonthDirectives
                    )

                    // Notification Trigger / Status Card
                    MonthlyNotificationCalloutCard(
                        notificationSentText = notificationSentText,
                        onSendNotification = {
                            NotificationHelper.sendMonthlyReviewNotification(
                                context = context,
                                monthName = reviewData.monthTitle
                            )
                            notificationSentText = "Notification dispatched to your notification tray!"
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ReportCardStandingBanner(
    reviewData: MonthlyReviewData
) {
    WoodCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("monthly_report_card_banner"),
        goldAccentBorder = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Gold Seal Emblem
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(DeepOakBrown)
                    .border(2.dp, SovereignGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.the_vault_circular_badge_1788026210267),
                    contentDescription = "Vault Executive Seal",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Text(
                text = reviewData.monthTitle.uppercase(Locale.US),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = SovereignGold,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp
                )
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BurgundyDeep.copy(alpha = 0.6f),
                border = BorderStroke(1.5.dp, SovereignGold)
            ) {
                Text(
                    text = reviewData.overallGrade,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = SovereignGold,
                        fontSize = 16.sp,
                        letterSpacing = 1.1.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Text(
                text = reviewData.gradeSubtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    color = ParchmentCream,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun MonthlyMetricsBreakdownSection(
    reviewData: MonthlyReviewData
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "PERFORMANCE AUDIT METRICS",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.3.sp,
                fontSize = 12.sp,
                color = SovereignGold
            )
        )

        // Metric 1: Total Saved vs Goal
        WoodCard(modifier = Modifier.fillMaxWidth()) {
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = SovereignGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "TOTAL SAVED VS. GOAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = "${reviewData.savingsGoalPercentage}% of Target",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = reviewData.currency.format(reviewData.totalSavedThisMonth, false),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Monthly Target: ${reviewData.currency.format(reviewData.savingsMonthlyTarget, false)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream,
                            fontSize = 11.sp
                        )
                    )
                }

                LinearProgressIndicator(
                    progress = { (reviewData.savingsGoalPercentage / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = SovereignGold,
                    trackColor = DeepOakBrown,
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = reviewData.primaryGoalProgressText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = ParchmentCream.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                )
            }
        }

        // Metric 2: Debt Paid Down
        WoodCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = SovereignGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "DEBT ELIMINATION STATUS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = if (reviewData.remainingDebt <= 0.0) "DEBT FREE" else "ACTIVE REPAYMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (reviewData.remainingDebt <= 0.0) SovereignGold else LustrousGold,
                            fontSize = 10.sp
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = if (reviewData.remainingDebt <= 0.0) "Zero Debt" else "-${reviewData.currency.format(reviewData.debtPaidDownThisMonth, false)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Remaining: ${reviewData.currency.format(reviewData.remainingDebt, false)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = reviewData.debtFreeStatusText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = ParchmentCream.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Metric 3: Net Worth Change
        WoodCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = SovereignGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "NET WORTH CHANGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = "+${String.format(Locale.US, "%.1f", reviewData.netWorthGrowthRatePercent)}% This Cycle",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = reviewData.currency.format(reviewData.currentNetWorth, false),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "+${reviewData.currency.format(reviewData.netWorthGrowthAmount, false)} gained",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = SovereignGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Metric 4: Streak Consistency
        WoodCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SovereignGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "STREAK & DISCIPLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = "${reviewData.streakConsistencyPercent}% Consistency",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = "${reviewData.currentStreakDays} Consecutive Days Logged",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = SovereignGold,
                        fontSize = 16.sp
                    )
                )

                Text(
                    text = reviewData.ledgerDisciplineText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = ParchmentCream,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun MilestonesAchievedSection(
    badges: List<ReviewMilestoneBadge>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "MILESTONES & RECOGNITIONS ACHIEVED",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.3.sp,
                fontSize = 12.sp,
                color = SovereignGold
            )
        )

        badges.forEach { badge ->
            WoodCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("badge_card_${badge.id}"),
                goldAccentBorder = true
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeepOakBrown)
                            .border(1.dp, SovereignGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = SovereignGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = badge.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = badge.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NextMonthDirectivesSection(
    directives: List<String>
) {
    WoodCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("next_month_recommendations_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = SovereignGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "STRATEGIC RECOMMENDATIONS FOR NEXT MONTH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = SovereignGold,
                        fontSize = 11.sp,
                        letterSpacing = 1.1.sp
                    )
                )
            }

            directives.forEachIndexed { idx, dir ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${idx + 1}.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = dir,
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
private fun MonthlyNotificationCalloutCard(
    notificationSentText: String?,
    onSendNotification: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = BurgundyDeep.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = SovereignGold,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "MONTHLY REVIEW NOTIFICATION DISPATCH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = SovereignGold,
                        fontSize = 11.sp
                    )
                )
            }

            Text(
                text = "Vault+ automatically compiles your report card at the end of each calendar month and issues a system notification directly to your device.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    color = ParchmentCream,
                    fontSize = 11.sp
                )
            )

            GoldButton(
                text = "SEND REVIEW NOTIFICATION NOW",
                onClick = onSendNotification,
                leadingIcon = Icons.Default.Notifications,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_monthly_notification_button")
            )

            notificationSentText?.let { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = SovereignGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MonthlyReviewPremiumLockView(
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
            text = "Monthly Executive Wealth Review",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = SovereignGold,
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Exclusive to Vault+ Members. Receive a certified monthly financial report card summarizing your total saved vs. goals, debt elimination velocity, net worth growth delta, ledger streak discipline, and custom strategic directives for the coming month.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.SansSerif,
                color = ParchmentCream,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Feature highlights
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
                ReviewPerkItem("Executive Report Card & Premier Standing Audit Grade")
                ReviewPerkItem("Savings vs. Goal, Debt Paydown, & Net Worth Velocity Delta")
                ReviewPerkItem("Monthly Notification Dispatch with Tailored Directives")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GoldButton(
            text = "UNLOCK WITH PRO • $7.99/MO",
            onClick = onUpgradeClick,
            leadingIcon = Icons.Default.Shield,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("unlock_monthly_review_upgrade_button")
        )
    }
}

@Composable
private fun ReviewPerkItem(text: String) {
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
