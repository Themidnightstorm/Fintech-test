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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountabilityPartnerEntity
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GreenSealInk
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RedStampInk
import com.example.ui.theme.SovereignGold

@Composable
fun AccountabilityPartnerCard(
    partner: AccountabilityPartnerEntity?,
    onOpenPartnerModal: () -> Unit,
    onQuickCheckIn: () -> Unit,
    onQuickNudge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLinked = partner?.isLinked == true
    val cardInteraction = remember { MutableInteractionSource() }

    WoodCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = cardInteraction,
                indication = null,
                onClick = onOpenPartnerModal
            )
            .testTag("accountability_partner_card"),
        goldAccentBorder = isLinked
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Section Header: Title & Pact Status
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DeepOakBrown)
                            .border(1.dp, SovereignGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = "Accountability Pact",
                            tint = SovereignGold,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "ACCOUNTABILITY PARTNER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 13.sp,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = if (isLinked) "Shared Daily Financial Pact" else "Mutual Discipline & Daily Check-Ins",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            if (!isLinked) {
                // Unlinked state: Call to action to invite a friend
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepOakBrown.copy(alpha = 0.7f))
                        .border(1.dp, GoldBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Invite a trusted friend or partner by email to share daily safe spend check-ins, keep each other accountable, and build consecutive financial discipline streaks together.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = MutedCream,
                            lineHeight = 16.sp,
                            fontSize = 11.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        GoldButton(
                            text = "INVITE PARTNER",
                            onClick = onOpenPartnerModal,
                            modifier = Modifier.testTag("invite_partner_button")
                        )
                    }
                }
            } else {
                // Linked state: Partner summary & Today's check-in status
                val partnerName = partner?.partnerName?.ifBlank { "Partner" } ?: "Partner"
                val userDone = partner?.userCheckedInToday == true
                val partnerDone = partner?.partnerCheckedInToday == true

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepOakBrown.copy(alpha = 0.8f))
                        .border(1.dp, GoldBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Partner identity bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = SovereignGold.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "$partnerName (${partner?.partnerEmail})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = MutedCream,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    // Daily Check-In Dual Status Trackers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // User Check-In Tile
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (userDone) GreenSealInk.copy(alpha = 0.25f) else DarkOakBrown)
                                .border(
                                    1.dp,
                                    if (userDone) SovereignGold.copy(alpha = 0.8f) else GoldBorder.copy(alpha = 0.3f),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    if (!userDone) onQuickCheckIn() else onOpenPartnerModal()
                                }
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "YOUR CHECK-IN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = SovereignGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (userDone) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = if (userDone) SovereignGold else MutedCream,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (userDone) "✓ Completed" else "Tap to Check In",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = if (userDone) FontWeight.Bold else FontWeight.Normal,
                                            color = if (userDone) ParchmentCream else SovereignGold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Partner Check-In Tile
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (partnerDone) GreenSealInk.copy(alpha = 0.25f) else DarkOakBrown)
                                .border(
                                    1.dp,
                                    if (partnerDone) SovereignGold.copy(alpha = 0.8f) else GoldBorder.copy(alpha = 0.3f),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onOpenPartnerModal() }
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "$partnerName".uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = SovereignGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (partnerDone) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = if (partnerDone) SovereignGold else MutedCream,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (partnerDone) "✓ Checked In" else "Pending",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = if (partnerDone) FontWeight.Bold else FontWeight.Normal,
                                            color = if (partnerDone) ParchmentCream else MutedCream,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Bar: Quick Nudge & View Dashboard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val nudgeInteraction = remember { MutableInteractionSource() }
                    val isNudgePressed by nudgeInteraction.collectIsPressedAsState()
                    val nudgeBorder by animateColorAsState(
                        targetValue = if (isNudgePressed) BurgundyGlow else SovereignGold.copy(alpha = 0.6f),
                        animationSpec = tween(150),
                        label = "nudge_border"
                    )

                    // Quick Nudge Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepOakBrown)
                            .border(1.dp, nudgeBorder, RoundedCornerShape(6.dp))
                            .clickable(
                                interactionSource = nudgeInteraction,
                                indication = null,
                                onClick = onQuickNudge
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("quick_nudge_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Send Nudge",
                            tint = SovereignGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Send Nudge",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = "Open Partner Dashboard →",
                        style = MaterialTheme.typography.labelSmall.copy(
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
