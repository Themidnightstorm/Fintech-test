package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MembershipTier
import com.example.data.model.VaultCurrency
import com.example.ui.components.ThinGoldDivider
import com.example.ui.components.WoodCard
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DarkOakSurface
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GreenSealInk
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PolishedOak
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal(
    currentCurrency: VaultCurrency,
    membershipTier: MembershipTier,
    subscribedEmail: String = "",
    onSelectCurrency: (VaultCurrency) -> Unit,
    onUpgradeMembership: () -> Unit,
    onSelectTier: (MembershipTier) -> Unit,
    onSubscribeEmail: (String) -> Unit,
    onRecalibrateOnboarding: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current

    var emailInput by remember(subscribedEmail) { mutableStateOf(subscribedEmail) }
    var emailSubmitted by remember(subscribedEmail) { mutableStateOf(subscribedEmail.isNotBlank()) }

    val isPremium = membershipTier == MembershipTier.PREMIUM

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkOakBrown,
        dragHandle = null,
        modifier = Modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .background(DarkOakBrown)
        ) {
            // Header
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DeepOakBrown)
                            .border(1.5.dp, SovereignGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.the_vault_circular_badge_1788026210267),
                            contentDescription = "The Vault Emblem",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Column {
                        Text(
                            text = "VAULT SETTINGS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 16.sp,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "Tiers, Currency & Local Configuration",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = MutedCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Settings",
                        tint = SovereignGold
                    )
                }
            }

            ThinGoldDivider(alpha = 0.4f, showEmblem = true)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Membership Tiers (Full 3-Tier Overview)
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = SovereignGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "MEMBERSHIP TIERS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.2.sp
                                )
                            )
                        }

                        // Free Tier Card
                        SettingsTierRowCard(
                            tier = MembershipTier.FREE,
                            currentTier = membershipTier,
                            priceDisplay = "Free Forever",
                            onSelect = { onSelectTier(MembershipTier.FREE) }
                        )

                        // Pro Tier Card
                        SettingsTierRowCard(
                            tier = MembershipTier.PRO,
                            currentTier = membershipTier,
                            priceDisplay = "$7.99/mo",
                            onSelect = { onSelectTier(MembershipTier.PRO) }
                        )

                        // Premium Tier Card (Vault+)
                        SettingsTierRowCard(
                            tier = MembershipTier.PREMIUM,
                            currentTier = membershipTier,
                            priceDisplay = "$19.99/mo or $149/yr",
                            onSelect = { onSelectTier(MembershipTier.PREMIUM) }
                        )
                    }
                }

                // Section: Email Capture for Early Access & Exclusive Content
                item {
                    WoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_email_capture_card"),
                        goldAccentBorder = true
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(BurgundyDeep)
                                            .border(1.dp, SovereignGold, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            tint = SovereignGold,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    Text(
                                        text = "EARLY ACCESS & UPDATES",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.1.sp
                                        )
                                    )
                                }

                                if (emailSubmitted && emailInput.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = GreenSealInk.copy(alpha = 0.25f),
                                        border = BorderStroke(1.dp, GreenSealInk)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = GreenSealInk,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = "SUBSCRIBED",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GreenSealInk,
                                                    fontSize = 8.5.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Get early access to new features & exclusive content.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Medium,
                                    color = ParchmentCream,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = {
                                        emailInput = it
                                        emailSubmitted = false
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Enter your email address...",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MutedCream.copy(alpha = 0.6f),
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            if (emailInput.contains("@") && emailInput.contains(".")) {
                                                onSubscribeEmail(emailInput)
                                                emailSubmitted = true
                                                Toast.makeText(context, "Subscribed locally to early access!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = DarkOakSurface,
                                        unfocusedContainerColor = DarkOakSurface,
                                        focusedBorderColor = SovereignGold,
                                        unfocusedBorderColor = RichOakBorder,
                                        focusedTextColor = ParchmentCream,
                                        unfocusedTextColor = ParchmentCream,
                                        cursorColor = SovereignGold
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("settings_email_input")
                                )

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (emailInput.isNotBlank() && emailInput.contains("@")) SovereignGold else DeepOakBrown
                                        )
                                        .border(
                                            1.dp,
                                            if (emailInput.isNotBlank() && emailInput.contains("@")) SovereignGold else RichOakBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable(enabled = emailInput.isNotBlank() && emailInput.contains("@")) {
                                            focusManager.clearFocus()
                                            onSubscribeEmail(emailInput)
                                            emailSubmitted = true
                                            Toast.makeText(context, "Subscribed! Stored locally on device.", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("settings_email_subscribe_button"),
                                    color = Color.Transparent
                                ) {
                                    Text(
                                        text = if (emailSubmitted) "SAVED ✓" else "SUBSCRIBE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (emailInput.isNotBlank() && emailInput.contains("@")) ObsidianBlack else MutedCream,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 15.dp)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MutedCream,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Stored locally on your device (no external transmission).",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = MutedCream,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Currency Denomination Selector
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ACTIVE CURRENCY DENOMINATION",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 13.sp,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "Instantly re-denominate ledger balances, safe spend, and bullion goals into world reserve currencies with live forex rates.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Currency List
                items(VaultCurrency.values()) { currency ->
                    val isSelected = currency == currentCurrency
                    val borderAlpha by animateColorAsState(
                        targetValue = if (isSelected) SovereignGold else SovereignGold.copy(alpha = 0.25f),
                        animationSpec = tween(300),
                        label = "border_anim"
                    )

                    WoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onSelectCurrency(currency)
                            }
                            .testTag("currency_option_${currency.code.lowercase()}"),
                        goldAccentBorder = isSelected
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) BurgundyDeep else DeepOakBrown)
                                        .border(1.2.dp, borderAlpha, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currency.symbol,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 16.sp
                                        )
                                    )
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = currency.code,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "• ${currency.symbol}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                color = ParchmentCream,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = currency.displayName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = MutedCream,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }

                            if (isSelected) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GreenSealInk.copy(alpha = 0.25f))
                                        .border(1.dp, SovereignGold, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = SovereignGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Priority Support & Community Section (Exclusive for Premium Users)
                item {
                    var earlyAccessRequested by remember { mutableStateOf(false) }

                    if (isPremium) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("priority_support_section"),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Section Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = SovereignGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "PRIORITY CONCIERGE & SUPPORT",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 13.sp,
                                        letterSpacing = 1.2.sp
                                    )
                                )
                            }

                            // Priority Support Card
                            WoodCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("priority_support_card"),
                                goldAccentBorder = true
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Header with VIP Badge
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(BurgundyDeep)
                                                    .border(1.dp, SovereignGold, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Headphones,
                                                    contentDescription = "Concierge Desk",
                                                    tint = SovereignGold,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = "Private Banking Concierge Desk",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontFamily = FontFamily.Serif,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SovereignGold,
                                                        fontSize = 13.sp
                                                    )
                                                )
                                                Text(
                                                    text = "Fast-Track Technical & Financial Assistance",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.SansSerif,
                                                        color = MutedCream,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }

                                        // Status Chip
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = GreenSealInk.copy(alpha = 0.25f),
                                            border = BorderStroke(1.dp, GreenSealInk)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(GreenSealInk)
                                                )
                                                Text(
                                                    text = "ONLINE",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GreenSealInk,
                                                        fontSize = 8.5.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    // Explicit Required Note: 'Premium users receive priority support'
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        color = DeepOakBrown,
                                        border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.6f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Bolt,
                                                contentDescription = null,
                                                tint = SovereignGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = "Premium users receive priority support",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.SansSerif,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SovereignGold,
                                                        fontSize = 11.5.sp
                                                    )
                                                )
                                                Text(
                                                    text = "Your inquiries bypass standard queue delays with guaranteed expedited response times under 2 hours.",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.SansSerif,
                                                        color = ParchmentCream,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    // Turnaround Time Stats
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DarkOakSurface)
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = SovereignGold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "Average VIP Response Time",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    color = ParchmentCream,
                                                    fontSize = 10.5.sp
                                                )
                                            )
                                        }
                                        Text(
                                            text = "< 2 Hours (Instant VIP Queue)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }

                                    // 'Contact Support' Button
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, SovereignGold, RoundedCornerShape(8.dp))
                                            .clickable {
                                                val supportEmail = "concierge@thevault.app"
                                                clipboardManager.setText(AnnotatedString(supportEmail))
                                                
                                                try {
                                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                        data = Uri.parse("mailto:$supportEmail")
                                                        putExtra(Intent.EXTRA_SUBJECT, "Vault+ Priority Support Request [Private Tier]")
                                                        putExtra(Intent.EXTRA_TEXT, "Hello Private Concierge Team,\n\nI am reaching out regarding my Vault+ Premier account:\n\n[Please describe your request or inquiry here]")
                                                    }
                                                    context.startActivity(Intent.createChooser(intent, "Contact Priority Support"))
                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        context,
                                                        "Priority Desk: concierge@thevault.app (Copied)",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                            .testTag("contact_priority_support_button"),
                                        color = BurgundyDeep
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                tint = SovereignGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "CONTACT PRIORITY SUPPORT",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SovereignGold,
                                                    fontSize = 12.sp,
                                                    letterSpacing = 1.1.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Executive Private Hub (Placeholder with 'Coming Soon')
                            WoodCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("executive_private_hub_card"),
                                goldAccentBorder = false
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(DeepOakBrown)
                                                    .border(1.dp, SovereignGold.copy(alpha = 0.7f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Groups,
                                                    contentDescription = "Community Hub",
                                                    tint = SovereignGold,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = "Executive Private Hub",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontFamily = FontFamily.Serif,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SovereignGold,
                                                        fontSize = 13.sp
                                                    )
                                                )
                                                Text(
                                                    text = "Exclusive Member Salon & Masterminds",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.SansSerif,
                                                        color = MutedCream,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }

                                        // Required 'Coming Soon' Badge
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = DeepOakBrown,
                                            border = BorderStroke(1.dp, SovereignGold)
                                        ) {
                                            Text(
                                                text = "COMING SOON",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SovereignGold,
                                                    fontSize = 9.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "We are crafting a private sanctuary for Vault+ patrons to exchange capital allocation strategies, participate in monthly investing masterminds, and connect with peer private banking patrons.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = ParchmentCream,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    )

                                    // Teaser Highlights
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DarkOakSurface)
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Forum,
                                                contentDescription = null,
                                                tint = SovereignGold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "Patron-Only Asset Discussion Forums & Polls",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    color = ParchmentCream,
                                                    fontSize = 10.5.sp
                                                )
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = SovereignGold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "Quarterly Live Audio AMAs with Chief Wealth Curators",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    color = ParchmentCream,
                                                    fontSize = 10.5.sp
                                                )
                                            )
                                        }
                                    }

                                    // Interactive Early Access Notify Button
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(
                                                width = 1.dp,
                                                color = if (earlyAccessRequested) GreenSealInk else SovereignGold.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                earlyAccessRequested = !earlyAccessRequested
                                                if (earlyAccessRequested) {
                                                    Toast.makeText(
                                                        context,
                                                        "Registered for Executive Private Hub Early Access!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                            .testTag("early_access_community_button"),
                                        color = if (earlyAccessRequested) GreenSealInk.copy(alpha = 0.2f) else DeepOakBrown
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = if (earlyAccessRequested) Icons.Default.CheckCircle else Icons.Default.NotificationsActive,
                                                contentDescription = null,
                                                tint = if (earlyAccessRequested) GreenSealInk else SovereignGold,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (earlyAccessRequested) "EARLY ACCESS CONFIRMED ✓" else "NOTIFY ME WHEN COMMUNITY IS LIVE",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (earlyAccessRequested) GreenSealInk else SovereignGold,
                                                    fontSize = 9.5.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Locked preview for non-premium users showing that Priority Support is a Vault+ exclusive feature
                        WoodCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("priority_support_locked_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked Feature",
                                            tint = SovereignGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "PRIORITY SUPPORT & COMMUNITY",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 12.sp,
                                                letterSpacing = 1.1.sp
                                            )
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DeepOakBrown,
                                        border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.6f))
                                    ) {
                                        Text(
                                            text = "VAULT+ ONLY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 8.5.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Premium users receive priority support (<2-hour expedited queue) and exclusive access to the upcoming Executive Private Hub.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                )

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, SovereignGold, RoundedCornerShape(6.dp))
                                        .clickable {
                                            onDismiss()
                                            onUpgradeMembership()
                                        }
                                        .testTag("unlock_priority_support_button"),
                                    color = DeepOakBrown
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = SovereignGold,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "UNLOCK WITH VAULT+",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 9.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Ledger Calibration & Data Security
                item {
                    WoodCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "LEDGER CALIBRATION & PRIVACY",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.1.sp
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Recalibrate Baseline Finances",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.Bold,
                                            color = ParchmentCream,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = "Updating your numbers will recalculate your Daily Safe Spend and Three Slips.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = SovereignGold,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, SovereignGold, RoundedCornerShape(6.dp))
                                        .clickable {
                                            onDismiss()
                                            onRecalibrateOnboarding()
                                        }
                                        .testTag("settings_recalibrate_button"),
                                    color = DeepOakBrown
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RestartAlt,
                                            contentDescription = null,
                                            tint = SovereignGold,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "RECALIBRATE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }

                            ThinGoldDivider(alpha = 0.2f)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = SovereignGold.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "All financial records and currency preferences are stored on-device with SQLite Room persistence.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = MutedCream,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Terms of Service
                item {
                    WoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_terms_of_service_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Terms of Service",
                                    tint = SovereignGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "TERMS OF SERVICE",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.1.sp
                                    )
                                )
                            }

                            Text(
                                text = "The Vault provides educational financial tools and information only. It does not provide financial, investment, or legal advice. All financial decisions made using this app are your sole responsibility. The Vault stores all data locally on your device; no data is transmitted to external servers. Use of this app constitutes acceptance of these terms.",
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

                // Privacy Policy
                item {
                    WoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_privacy_policy_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Privacy Policy",
                                    tint = SovereignGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "PRIVACY POLICY",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.1.sp
                                    )
                                )
                            }

                            Text(
                                text = "The Vault does not collect, store, or transmit any personal or financial data to external servers. All data—including income, expenses, savings goals, and transaction history—remains exclusively on your device. No third parties have access to your information. This app does not use analytics, tracking, or cookies.",
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

                // Financial Advisory Disclaimer
                item {
                    WoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_disclaimer_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Legal Shield",
                                    tint = SovereignGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "FINANCIAL ADVISORY DISCLAIMER",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.1.sp
                                    )
                                )
                            }

                            Text(
                                text = "The Vault is a self-directed financial budgeting tool, wealth allocation ledger, and financial literacy simulator. It is designed for educational, illustrative, and organizational purposes only and does NOT constitute certified financial, investment, tax, or legal advice. Calculations, safe spend formulas, and compound projections are estimates based on user inputs. Please consult a licensed Financial Advisor, Certified Financial Planner (CFP®), CPA, or fiduciary expert for personalized financial planning and investment decisions.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = MutedCream,
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
}

@Composable
private fun SettingsTierRowCard(
    tier: MembershipTier,
    currentTier: MembershipTier,
    priceDisplay: String,
    onSelect: () -> Unit
) {
    val isCurrent = currentTier == tier

    WoodCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_tier_${tier.id.lowercase()}"),
        goldAccentBorder = isCurrent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = tier.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 13.5.sp
                        )
                    )
                    Text(
                        text = "• $priceDisplay",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) GreenSealInk else ParchmentCream,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = tier.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = MutedCream,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isCurrent) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GreenSealInk.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, GreenSealInk)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(GreenSealInk)
                        )
                        Text(
                            text = "CURRENT TIER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = GreenSealInk,
                                fontSize = 8.5.sp
                            )
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, SovereignGold, RoundedCornerShape(6.dp))
                        .clickable { onSelect() }
                        .testTag("settings_select_tier_${tier.id.lowercase()}"),
                    color = if (tier == MembershipTier.PREMIUM) BurgundyDeep else DeepOakBrown
                ) {
                    Text(
                        text = if (tier == MembershipTier.FREE) "SELECT" else "UPGRADE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 9.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
