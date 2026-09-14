package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MembershipTier
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GreenSealInk
import com.example.ui.theme.MutedCream
import com.example.ui.theme.PolishedOak
import com.example.ui.theme.SovereignGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Top Aristocratic Header: Dignified, showing Title, Tier Indicator, Date, and Settings access.
 */
@Composable
fun AristocraticHeader(
    membershipTier: MembershipTier = MembershipTier.FREE,
    onOpenSettings: () -> Unit = {},
    onUpgradeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("EEEE, MMMM d", Locale.US).format(Date()).uppercase()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Gold Vault Crest Image / Monogram
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(DeepOakBrown)
                        .border(1.5.dp, SovereignGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.the_vault_circular_badge_1788026210267),
                        contentDescription = "Vault Crest",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "THE VAULT",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                letterSpacing = 2.sp,
                                color = SovereignGold
                            ),
                            modifier = Modifier.testTag("app_title_text")
                        )

                        // Tier Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when (membershipTier) {
                                MembershipTier.PREMIUM -> BurgundyDeep
                                MembershipTier.PRO -> DeepOakBrown
                                MembershipTier.FREE -> DeepOakBrown.copy(alpha = 0.8f)
                            },
                            border = BorderStroke(
                                1.dp,
                                when (membershipTier) {
                                    MembershipTier.PREMIUM -> SovereignGold
                                    MembershipTier.PRO -> SovereignGold.copy(alpha = 0.7f)
                                    MembershipTier.FREE -> SovereignGold.copy(alpha = 0.4f)
                                }
                            ),
                            modifier = Modifier
                                .clickable { onUpgradeClick() }
                                .testTag("header_tier_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = when (membershipTier) {
                                        MembershipTier.PREMIUM -> Icons.Default.WorkspacePremium
                                        MembershipTier.PRO -> Icons.Default.Star
                                        MembershipTier.FREE -> Icons.Default.Shield
                                    },
                                    contentDescription = null,
                                    tint = SovereignGold,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = membershipTier.title.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 8.5.sp
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                            color = MutedCream
                        ),
                        modifier = Modifier.testTag("app_date_text")
                    )
                }
            }

            // Settings Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DeepOakBrown)
                    .border(1.dp, GoldBorder, CircleShape)
                    .clickable { onOpenSettings() }
                    .testTag("settings_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings Menu",
                    tint = SovereignGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        ThinGoldDivider(alpha = 0.5f, showEmblem = true)
    }
}
