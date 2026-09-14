package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountabilityPartnerEntity
import com.example.data.local.entity.PartnerNudgeEntity
import com.example.ui.components.GoldButton
import com.example.ui.components.ThinGoldDivider
import com.example.ui.components.WoodCard
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DarkOakSurface
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GreenSealInk
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RedStampInk
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.SovereignGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountabilityPartnerModal(
    partner: AccountabilityPartnerEntity?,
    recentNudges: List<PartnerNudgeEntity>,
    userDailySafeSpend: Double,
    onInvitePartner: (email: String, name: String) -> Unit,
    onCompleteCheckIn: () -> Unit,
    onTogglePartnerCheckIn: () -> Unit,
    onSendNudge: (message: String) -> Unit,
    onUnlinkPartner: () -> Unit,
    onResetStreak: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isLinked = partner?.isLinked == true

    var inviteEmail by remember { mutableStateOf("") }
    var inviteName by remember { mutableStateOf("") }
    var customNudgeText by remember { mutableStateOf("") }
    var inviteSuccessNotice by remember { mutableStateOf(false) }

    val quickNudges = listOf(
        "🔔 Don't forget to log your daily safe spend before midnight!",
        "🛡️ Let's protect our consecutive streak! Log your transactions.",
        "🔥 Keep the pact alive! Logged my safe spend today.",
        "👏 Great discipline today! Staying within budget."
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepOakBrown,
        dragHandle = null,
        modifier = Modifier
            .fillMaxHeight(0.92f)
            .testTag("accountability_partner_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(DeepOakBrown, DarkOakSurface, BurgundyDeep.copy(alpha = 0.6f))
                    )
                )
        ) {
            // Modal Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkOakBrown)
                            .border(1.5.dp, SovereignGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = "Pact Icon",
                            tint = SovereignGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "ACCOUNTABILITY PACT",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 16.sp,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = if (isLinked) "Mutual Daily Check-In & Streak" else "Invite a Financial Partner",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_partner_modal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SovereignGold
                    )
                }
            }

            ThinGoldDivider(alpha = 0.4f, showEmblem = true)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (!isLinked) {
                    // ==========================================
                    // UNLINKED STATE: INVITATION FORM & EXPLANATION
                    // ==========================================
                    item {
                        WoodCard(
                            modifier = Modifier.fillMaxWidth(),
                            goldAccentBorder = true
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "FORGE A FINANCIAL PACT",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 14.sp
                                    )
                                )

                                Text(
                                    text = "Studies show people are 95% more likely to achieve their financial goals when holding regular accountability check-ins with a partner. Enter your partner's email below to link accounts.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                )

                                // Email Input
                                OutlinedTextField(
                                    value = inviteEmail,
                                    onValueChange = { inviteEmail = it },
                                    label = {
                                        Text(
                                            "Partner's Email Address",
                                            color = SovereignGold,
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 12.sp
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            "e.g. friend@example.com",
                                            color = MutedCream.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            tint = SovereignGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = DeepOakBrown,
                                        unfocusedContainerColor = DeepOakBrown,
                                        focusedTextColor = ParchmentCream,
                                        unfocusedTextColor = ParchmentCream,
                                        focusedIndicatorColor = SovereignGold,
                                        unfocusedIndicatorColor = GoldBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("partner_email_input")
                                )

                                // Optional Name Input
                                OutlinedTextField(
                                    value = inviteName,
                                    onValueChange = { inviteName = it },
                                    label = {
                                        Text(
                                            "Partner's Name (Optional)",
                                            color = SovereignGold,
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 12.sp
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            "e.g. Alexandra Sterling",
                                            color = MutedCream.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = SovereignGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = DeepOakBrown,
                                        unfocusedContainerColor = DeepOakBrown,
                                        focusedTextColor = ParchmentCream,
                                        unfocusedTextColor = ParchmentCream,
                                        focusedIndicatorColor = SovereignGold,
                                        unfocusedIndicatorColor = GoldBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("partner_name_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Quick demo autofill button
                                    Text(
                                        text = "⚡ Quick Demo Partner",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = SovereignGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                inviteEmail = "alexandra.sterling@vaultcapital.org"
                                                inviteName = "Alexandra Sterling"
                                            }
                                            .padding(4.dp)
                                    )

                                    GoldButton(
                                        text = "SEND INVITATION",
                                        onClick = {
                                            if (inviteEmail.isNotBlank()) {
                                                onInvitePartner(inviteEmail, inviteName)
                                            }
                                        },
                                        enabled = inviteEmail.isNotBlank(),
                                        modifier = Modifier.testTag("send_partner_invite_button")
                                    )
                                }
                            }
                        }
                    }

                    // Pact Golden Principles
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepOakBrown)
                                .border(1.dp, GoldBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "HOW THE ACCOUNTABILITY PACT WORKS",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold,
                                    fontSize = 11.sp
                                )
                            )

                            val principles = listOf(
                                "1. Daily Safe Spend Check-In" to "Both partners log in daily and complete their check-in to confirm they stayed within their daily safe spend budget.",
                                "2. Shared Streak Protection" to "Your consecutive streak increases only when BOTH partners complete their daily check-in.",
                                "3. Direct Nudges & Encouragement" to "Send quick reminders when midnight approaches to ensure neither partner lets the streak break."
                            )

                            for ((title, desc) in principles) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = MutedCream,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // ==========================================
                    // LINKED STATE: SHARED DASHBOARD & STREAK
                    // ==========================================
                    val partnerName = partner.partnerName.ifBlank { "Partner" }
                    val streak = partner.sharedStreak
                    val userDone = partner.userCheckedInToday
                    val partnerDone = partner.partnerCheckedInToday

                    // 1. Shared Streak Hero Card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF5A2525),
                                            DeepOakBrown
                                        )
                                    )
                                )
                                .border(1.5.dp, SovereignGold, RoundedCornerShape(12.dp))
                                .padding(20.dp)
                                .testTag("shared_streak_banner")
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak Flame",
                                        tint = SovereignGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "$streak CONSECUTIVE DAYS",
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            color = SovereignGold,
                                            letterSpacing = 1.5.sp
                                        )
                                    )
                                }

                                Text(
                                    text = if (userDone && partnerDone) {
                                        "✓ Both Partners Checked In Today • Streak Protected!"
                                    } else if (userDone) {
                                        "You are checked in • Waiting on $partnerName"
                                    } else if (partnerDone) {
                                        "$partnerName checked in • Complete your check-in to advance streak"
                                    } else {
                                        "Both check-ins pending for today • Complete before midnight"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Medium,
                                        color = if (userDone && partnerDone) SovereignGold else ParchmentCream,
                                        fontSize = 11.sp
                                    )
                                )

                                // Milestone tracker
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DeepOakBrown)
                                        .border(1.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Milestone",
                                        tint = SovereignGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    val nextMilestone = if (streak < 7) 7 else if (streak < 14) 14 else if (streak < 30) 30 else streak + 10
                                    val daysLeft = nextMilestone - streak
                                    Text(
                                        text = "Next Target: $nextMilestone Days ($daysLeft days remaining)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = SovereignGold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // 2. Dual Daily Check-In Dashboard
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "TODAY'S DAILY STATUS",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 12.sp
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // User Tile
                                WoodCard(
                                    modifier = Modifier.weight(1f),
                                    goldAccentBorder = userDone
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "YOU (VAULT MASTER)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 10.sp
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
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = if (userDone) "✓ Checked In" else "Pending",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (userDone) ParchmentCream else SovereignGold,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }

                                        Text(
                                            text = "Daily Safe Spend: $%.2f/day".format(userDailySafeSpend.coerceAtLeast(partner.userDailySafeSpend)),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream,
                                                fontSize = 10.sp
                                            )
                                        )

                                        if (!userDone) {
                                            val checkInInteraction = remember { MutableInteractionSource() }
                                            val isCheckInPressed by checkInInteraction.collectIsPressedAsState()
                                            val checkInBg by animateColorAsState(
                                                targetValue = if (isCheckInPressed) BurgundyGlow else SovereignGold,
                                                animationSpec = tween(150),
                                                label = "check_in_btn"
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(checkInBg)
                                                    .clickable(
                                                        interactionSource = checkInInteraction,
                                                        indication = null,
                                                        onClick = onCompleteCheckIn
                                                    )
                                                    .padding(vertical = 8.dp)
                                                    .testTag("complete_daily_checkin_button"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "CHECK IN TODAY",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.SansSerif,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ObsidianBlack,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                // Partner Tile
                                WoodCard(
                                    modifier = Modifier.weight(1f),
                                    goldAccentBorder = partnerDone
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = partnerName.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 10.sp
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
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = if (partnerDone) "✓ Checked In" else "Pending",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (partnerDone) ParchmentCream else MutedCream,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }

                                        Text(
                                            text = "Daily Safe Spend: $%.2f/day".format(partner.partnerDailySafeSpend),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream,
                                                fontSize = 10.sp
                                            )
                                        )

                                        // Demo toggle for simulation
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(DeepOakBrown)
                                                .border(1.dp, GoldBorder.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                .clickable { onTogglePartnerCheckIn() }
                                                .padding(horizontal = 6.dp, vertical = 6.dp)
                                                .testTag("toggle_partner_checkin_button"),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SwapHoriz,
                                                contentDescription = "Toggle",
                                                tint = SovereignGold,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (partnerDone) "Simulate Pending" else "Simulate Check-In",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    color = SovereignGold,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Send Nudge Hub
                    item {
                        WoodCard(
                            modifier = Modifier.fillMaxWidth(),
                            goldAccentBorder = true
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "SEND NUDGE TO $partnerName".uppercase(),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                Text(
                                    text = "Send an encouraging prompt or reminder to keep the daily streak active:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream,
                                        fontSize = 11.sp
                                    )
                                )

                                // 1-Tap Quick Nudge Chips
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(quickNudges) { nudge ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DeepOakBrown)
                                                .border(1.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                .clickable {
                                                    onSendNudge(nudge)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = nudge,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    color = SovereignGold,
                                                    fontSize = 11.sp
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }

                                // Custom Nudge Input
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = customNudgeText,
                                        onValueChange = { customNudgeText = it },
                                        placeholder = {
                                            Text(
                                                "Type custom encouragement...",
                                                color = MutedCream.copy(alpha = 0.6f),
                                                fontSize = 11.sp
                                            )
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                        keyboardActions = KeyboardActions(
                                            onSend = {
                                                if (customNudgeText.isNotBlank()) {
                                                    onSendNudge(customNudgeText)
                                                    customNudgeText = ""
                                                }
                                            }
                                        ),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = DeepOakBrown,
                                            unfocusedContainerColor = DeepOakBrown,
                                            focusedTextColor = ParchmentCream,
                                            unfocusedTextColor = ParchmentCream,
                                            focusedIndicatorColor = SovereignGold,
                                            unfocusedIndicatorColor = GoldBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("custom_nudge_input")
                                    )

                                    val sendInteraction = remember { MutableInteractionSource() }
                                    val isSendPressed by sendInteraction.collectIsPressedAsState()
                                    val sendBg by animateColorAsState(
                                        targetValue = if (isSendPressed) BurgundyGlow else SovereignGold,
                                        animationSpec = tween(150),
                                        label = "send_nudge_btn"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(sendBg)
                                            .clickable(
                                                interactionSource = sendInteraction,
                                                indication = null,
                                                onClick = {
                                                    if (customNudgeText.isNotBlank()) {
                                                        onSendNudge(customNudgeText)
                                                        customNudgeText = ""
                                                    }
                                                }
                                            )
                                            .testTag("send_custom_nudge_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send",
                                            tint = ObsidianBlack,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Recent Nudges Feed
                    if (recentNudges.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "NUDGE HISTORY & MESSAGES",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        letterSpacing = 1.2.sp,
                                        fontSize = 12.sp
                                    )
                                )

                                for (nudge in recentNudges) {
                                    val isMe = nudge.isFromUser
                                    val timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date(nudge.timestamp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isMe) DeepOakBrown else BurgundyDeep.copy(alpha = 0.5f))
                                            .border(
                                                1.dp,
                                                if (isMe) GoldBorder.copy(alpha = 0.3f) else SovereignGold.copy(alpha = 0.6f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (isMe) "YOU" else partnerName.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SovereignGold,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = nudge.message,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.SansSerif,
                                                        color = ParchmentCream,
                                                        fontSize = 11.sp,
                                                        lineHeight = 15.sp
                                                    )
                                                )
                                            }

                                            Text(
                                                text = timeStr,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    color = MutedCream.copy(alpha = 0.6f),
                                                    fontSize = 9.sp
                                                ),
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5. Pact Settings / Unlink Option
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reset Streak",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = MutedCream.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier
                                    .clickable { onResetStreak() }
                                    .padding(4.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepOakBrown)
                                    .border(1.dp, RedStampInk.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .clickable { onUnlinkPartner() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("unlink_partner_button"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LinkOff,
                                    contentDescription = "Unlink",
                                    tint = RedStampInk,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Unlink Partner",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = RedStampInk,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
