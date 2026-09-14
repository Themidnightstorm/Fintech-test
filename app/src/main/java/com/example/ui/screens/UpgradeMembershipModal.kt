package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
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
import com.example.data.model.MembershipTier
import com.example.ui.components.GoldButton
import com.example.ui.components.ThinGoldDivider
import com.example.ui.components.WoodCard
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DarkOakSurface
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.GreenSealInk
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeMembershipModal(
    currentTier: MembershipTier,
    onSelectTier: (MembershipTier) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    var isAnnualBilling by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkOakBrown,
        dragHandle = null,
        modifier = Modifier.testTag("upgrade_membership_bottom_sheet")
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = SovereignGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "MEMBERSHIP TIERS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            letterSpacing = 1.4.sp,
                            fontSize = 15.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_upgrade_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SovereignGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            ThinGoldDivider(alpha = 0.35f)
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Crest Header
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(DeepOakBrown)
                        .border(2.dp, SovereignGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.the_vault_circular_badge_1788026210267),
                        contentDescription = "Premier Vault Crest",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Select Your Vault Tier",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = SovereignGold
                        )
                    )
                    Text(
                        text = "From essential discipline to full executive financial autonomy.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Billing Frequency Switch for Premium
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DeepOakBrown,
                    border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.5f)),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (!isAnnualBilling) BurgundyDeep else Color.Transparent,
                            border = if (!isAnnualBilling) BorderStroke(1.dp, SovereignGold) else null,
                            modifier = Modifier.clickable { isAnnualBilling = false }
                        ) {
                            Text(
                                text = "Monthly Billing",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isAnnualBilling) SovereignGold else ParchmentCream,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isAnnualBilling) BurgundyDeep else Color.Transparent,
                            border = if (isAnnualBilling) BorderStroke(1.dp, SovereignGold) else null,
                            modifier = Modifier.clickable { isAnnualBilling = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Annual Billing",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAnnualBilling) SovereignGold else ParchmentCream,
                                        fontSize = 11.sp
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GreenSealInk.copy(alpha = 0.3f),
                                    border = BorderStroke(0.5.dp, GreenSealInk)
                                ) {
                                    Text(
                                        text = "SAVE 38%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenSealInk,
                                            fontSize = 8.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Tier 1: Free Tier (The Vault)
                TierCard(
                    tier = MembershipTier.FREE,
                    currentTier = currentTier,
                    priceText = "$0",
                    billingPeriod = "Free Forever",
                    isRecommended = false,
                    onSelect = { onSelectTier(MembershipTier.FREE) }
                )

                // Tier 2: Pro Tier (Vault Pro)
                TierCard(
                    tier = MembershipTier.PRO,
                    currentTier = currentTier,
                    priceText = "$7.99",
                    billingPeriod = "/ month",
                    isRecommended = false,
                    onSelect = { onSelectTier(MembershipTier.PRO) }
                )

                // Tier 3: Premium Tier (Vault+)
                TierCard(
                    tier = MembershipTier.PREMIUM,
                    currentTier = currentTier,
                    priceText = if (isAnnualBilling) "$149" else "$19.99",
                    billingPeriod = if (isAnnualBilling) "/ year ($12.42/mo)" else "/ month",
                    subtitleNote = if (isAnnualBilling) "Billed annually • Save $90/year" else "Or $149/year (Save 38%)",
                    isRecommended = true,
                    isAnnual = isAnnualBilling,
                    onSelect = { onSelectTier(MembershipTier.PREMIUM) }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TierCard(
    tier: MembershipTier,
    currentTier: MembershipTier,
    priceText: String,
    billingPeriod: String,
    subtitleNote: String? = null,
    isRecommended: Boolean = false,
    isAnnual: Boolean = false,
    onSelect: () -> Unit
) {
    val isCurrent = currentTier == tier
    val isHigher = tier.level > currentTier.level
    val isLower = tier.level < currentTier.level

    WoodCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tier_card_${tier.id.lowercase()}"),
        goldAccentBorder = isCurrent || isRecommended
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = tier.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "• ${tier.tierLabel}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = MutedCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Text(
                        text = tier.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream.copy(alpha = 0.85f),
                            fontSize = 10.5.sp
                        )
                    )
                }

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
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                } else if (isRecommended) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BurgundyDeep,
                        border = BorderStroke(1.dp, SovereignGold)
                    ) {
                        Text(
                            text = "MOST POPULAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Price Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = DarkOakSurface,
                border = BorderStroke(0.7.dp, SovereignGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = priceText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = billingPeriod,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    if (subtitleNote != null) {
                        Text(
                            text = subtitleNote,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = MutedCream,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }
            }

            // Features Checklist
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tier.perks.forEach { perk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isCurrent || isRecommended) SovereignGold else MutedCream,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = perk,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // CTA Button
            val buttonText = when {
                isCurrent -> "CURRENT TIER ACTIVE"
                tier == MembershipTier.FREE -> "SELECT FREE TIER"
                tier == MembershipTier.PRO -> "UPGRADE TO PRO • $7.99/MO"
                isAnnual -> "UPGRADE TO VAULT+ • $149/YR"
                else -> "UPGRADE TO VAULT+ • $19.99/MO"
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = if (isCurrent) GreenSealInk.copy(alpha = 0.7f) else SovereignGold,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(enabled = !isCurrent) { onSelect() }
                    .testTag("select_tier_button_${tier.id.lowercase()}"),
                color = when {
                    isCurrent -> DeepOakBrown
                    isRecommended -> BurgundyDeep
                    else -> DeepOakBrown
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 11.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCurrent) Icons.Default.Check else if (isRecommended) Icons.Default.Star else Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (isCurrent) GreenSealInk else SovereignGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) GreenSealInk else SovereignGold,
                            fontSize = 11.5.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}
