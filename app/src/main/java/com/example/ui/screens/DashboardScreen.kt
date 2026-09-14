package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.SlipType
import com.example.data.model.MembershipTier
import com.example.ui.components.AccountabilityPartnerCard
import com.example.ui.components.AristocraticHeader
import com.example.ui.components.BankSlipCard
import com.example.ui.components.CoinPileProgressSection
import com.example.ui.components.DailyLessonCard
import com.example.ui.components.GoldButton
import com.example.ui.components.NetWorthCard
import com.example.ui.components.WoodCard
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DarkOakSurface
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.GreenSealInk
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PolishedOak
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun DashboardScreen(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeSlip by viewModel.activeSlipType.collectAsStateWithLifecycle()
    val activeLesson by viewModel.activeLesson.collectAsStateWithLifecycle()
    val showTutor by viewModel.showTutorModal.collectAsStateWithLifecycle()
    val showUpgrade by viewModel.showUpgradeModal.collectAsStateWithLifecycle()
    val showSimulator by viewModel.showSimulatorModal.collectAsStateWithLifecycle()
    val showAddTx by viewModel.showAddTxModal.collectAsStateWithLifecycle()
    val showPartner by viewModel.showPartnerModal.collectAsStateWithLifecycle()
    val showCsvImport by viewModel.showCsvImportModal.collectAsStateWithLifecycle()
    val parsedCsvTransactions by viewModel.parsedCsvTransactions.collectAsStateWithLifecycle()
    val csvImportError by viewModel.csvImportError.collectAsStateWithLifecycle()
    val csvImportSuccessMessage by viewModel.csvImportSuccessMessage.collectAsStateWithLifecycle()
    val investingSim by viewModel.investingSim.collectAsStateWithLifecycle()
    val currency = uiState.currency
    val showSettings by viewModel.showSettingsModal.collectAsStateWithLifecycle()
    val showRoadmap by viewModel.showRoadmapModal.collectAsStateWithLifecycle()
    val showMonthlyReview by viewModel.showMonthlyReviewModal.collectAsStateWithLifecycle()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    viewModel.loadCsvFromStream(inputStream)
                }
            } catch (e: Exception) {
                viewModel.openCsvImport()
            }
        }
    }

    val fabInteraction = remember { MutableInteractionSource() }
    val fabPressed by fabInteraction.collectIsPressedAsState()
    val fabGlow by animateColorAsState(
        targetValue = if (fabPressed) BurgundyGlow else SovereignGold,
        animationSpec = tween(150),
        label = "fab_glow"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddTransaction(SlipType.SAVINGS) },
                containerColor = SovereignGold,
                contentColor = ObsidianBlack,
                shape = CircleShape,
                interactionSource = fabInteraction,
                modifier = Modifier
                    .testTag("floating_add_tx_button")
                    .shadow(
                        elevation = if (fabPressed) 10.dp else 4.dp,
                        shape = CircleShape,
                        ambientColor = if (fabPressed) BurgundyGlow else ObsidianBlack,
                        spotColor = if (fabPressed) BurgundyGlow else ObsidianBlack
                    )
                    .border(
                        width = if (fabPressed) 2.5.dp else 1.5.dp,
                        color = fabGlow,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top Aristocratic Header: Title + Tier Badge + Date + Settings access
            item {
                AristocraticHeader(
                    membershipTier = uiState.membershipTier,
                    onOpenSettings = { viewModel.openSettings() },
                    onUpgradeClick = { viewModel.openUpgradeModal() }
                )
            }

            // Total Net Worth Card with record entry + single Curator AI consultation button
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    NetWorthCard(
                        netWorth = uiState.netWorth,
                        monthlyIncome = uiState.monthlyIncome,
                        currency = currency,
                        onAddDeposit = { viewModel.openAddTransaction() },
                        onOpenTutor = { viewModel.openTutor() }
                    )
                }
            }

            // Daily Safe Spend Reality Card (Recalibrate button kept in place)
            if (uiState.dailySafeSpend > 0) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        WoodCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_daily_safe_spend_card"),
                            goldAccentBorder = true
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "DAILY SAFE SPEND",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 11.sp,
                                            letterSpacing = 1.2.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${currency.format(uiState.dailySafeSpend)} / day",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp,
                                            color = SovereignGold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Fixed Bills: ${currency.format(uiState.fixedExpensesTotal, false)}/mo • Debt: ${currency.format(uiState.totalDebt, false)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = ParchmentCream,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                val recalibrateInteraction = remember { MutableInteractionSource() }
                                val isRecalibratePressed by recalibrateInteraction.collectIsPressedAsState()
                                val recalibrateBorder by animateColorAsState(
                                    targetValue = if (isRecalibratePressed) BurgundyGlow else SovereignGold.copy(alpha = 0.8f),
                                    animationSpec = tween(150),
                                    label = "recalibrate_glow"
                                )

                                // Recalibrate Button (Preserved)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DeepOakBrown)
                                        .border(
                                            width = if (isRecalibratePressed) 2.dp else 1.dp,
                                            color = recalibrateBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable(
                                            interactionSource = recalibrateInteraction,
                                            indication = null,
                                            onClick = { viewModel.resetOnboarding() }
                                        )
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("recalibrate_reality_button")
                                ) {
                                    Text(
                                        text = "Recalibrate",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = SovereignGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Accountability Partner Card
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    AccountabilityPartnerCard(
                        partner = uiState.partner,
                        onOpenPartnerModal = { viewModel.openPartnerModal() },
                        onQuickCheckIn = { viewModel.completeDailyCheckIn() },
                        onQuickNudge = {
                            viewModel.sendNudge("🔔 Don't forget to log your daily safe spend before midnight!")
                        }
                    )
                }
            }

            // CSV Import Section with Gold Button
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    WoodCard(
                        modifier = Modifier.fillMaxWidth(),
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
                                    Icon(
                                        imageVector = Icons.Default.FileUpload,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "BANK LEDGER IMPORT",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                }

                                Text(
                                    text = "CSV Statement Sync",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Text(
                                text = "Export transactions from your bank (Chase, BofA, Vanguard, etc.) as CSV. Automatically categorize entries into Needs, Savings, & Wants.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = ParchmentCream,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )

                            GoldButton(
                                text = "Import Transactions",
                                onClick = {
                                    filePickerLauncher.launch("*/*")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("import_transactions_button")
                            )
                        }
                    }
                }
            }

            // Section: Three Money Slips (Expenses, Savings with Gold Coin Counter, Wants)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MONTHLY MONEY SLIPS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                                fontSize = 12.sp,
                                color = SovereignGold
                            )
                        )
                        Text(
                            text = "Tap slip to view ledger",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Slip 1: Expenses Slip
                    val baselineCapital = if (uiState.hasFixedIncome && uiState.monthlyIncome > 0) uiState.monthlyIncome else uiState.checkingBalance
                    val expensesMonthlyCap = (baselineCapital * 0.50).coerceAtLeast(uiState.fixedExpensesTotal)
                    val expCount = uiState.allTransactions.count { it.slipType == SlipType.EXPENSES.name }
                    BankSlipCard(
                        slipType = SlipType.EXPENSES,
                        amount = uiState.expensesTotal,
                        monthlyTarget = expensesMonthlyCap,
                        entryCount = expCount,
                        currency = currency,
                        onClick = { viewModel.openSlip(SlipType.EXPENSES) }
                    )

                    // Slip 2: Savings Slip (with Gold Coin Counter)
                    val savingsMonthlyTarget = (baselineCapital * 0.30).coerceAtLeast(0.0)
                    val savCount = uiState.allTransactions.count { it.slipType == SlipType.SAVINGS.name }
                    BankSlipCard(
                        slipType = SlipType.SAVINGS,
                        amount = uiState.savingsTotal,
                        monthlyTarget = savingsMonthlyTarget,
                        entryCount = savCount,
                        currency = currency,
                        onClick = { viewModel.openSlip(SlipType.SAVINGS) }
                    )

                    // Slip 3: Wants Slip
                    val wantsMonthlyCap = if (uiState.dailySafeSpend > 0) (uiState.dailySafeSpend * 30.0) else (baselineCapital * 0.20)
                    val wntCount = uiState.allTransactions.count { it.slipType == SlipType.WANTS.name }
                    BankSlipCard(
                        slipType = SlipType.WANTS,
                        amount = uiState.wantsTotal,
                        monthlyTarget = wantsMonthlyCap,
                        entryCount = wntCount,
                        currency = currency,
                        onClick = { viewModel.openSlip(SlipType.WANTS) }
                    )
                }
            }

            // Flagship Strategic Features (Roadmap & Monthly Review)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Feature 1: Personalized Financial Roadmap Card
                    val roadmapData = uiState.roadmapData
                    WoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openRoadmap() }
                            .testTag("personalized_roadmap_dashboard_card"),
                        goldAccentBorder = uiState.isPremium
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
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Personalized Financial Roadmap",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SovereignGold,
                                                    fontSize = 14.sp
                                                )
                                            )
                                            if (!uiState.isPremium) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Vault+ Exclusive",
                                                    tint = SovereignGold,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = if (roadmapData != null) "${String.format(java.util.Locale.US, "%.1f", roadmapData.fiProgressPercent)}% to Financial Independence • Milestones Timeline"
                                            else "Custom trajectory based on income & expenses",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = DeepOakBrown,
                                    border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.7f))
                                ) {
                                    Text(
                                        text = if (uiState.isPremium) "VIEW ROADMAP" else "VAULT+ ONLY",
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

                            if (roadmapData != null) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    LinearProgressIndicator(
                                        progress = { (roadmapData.fiProgressPercent / 100f).coerceIn(0f, 1f) },
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
                                            text = "Target: ${currency.format(roadmapData.fiTargetNumber, false)} (25x)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = ParchmentCream,
                                                fontSize = 9.sp
                                            )
                                        )
                                        Text(
                                            text = "Est. ~${String.format(java.util.Locale.US, "%.1f", roadmapData.estimatedYearsToFi)} Yrs",
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

                    // Feature 2: Monthly Progress Review Card
                    val reviewData = uiState.monthlyReviewData
                    WoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openMonthlyReview() }
                            .testTag("monthly_review_dashboard_card"),
                        goldAccentBorder = uiState.isPremium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Monthly Progress Review",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold,
                                                fontSize = 14.sp
                                            )
                                        )
                                        if (!uiState.isPremium) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Vault+ Exclusive",
                                                tint = SovereignGold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (reviewData != null) "${reviewData.monthTitle} Ready • Grade: ${reviewData.overallGrade.take(2)}"
                                        else "Audit saved vs goal, debt reduction & net worth",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = ParchmentCream,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DeepOakBrown,
                                border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.7f))
                            ) {
                                Text(
                                    text = if (uiState.isPremium) "REPORT CARD" else "VAULT+ ONLY",
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
                }
            }

            // Dedicated Membership Tiers & Privileges Overview Card (All 3 Tiers Clearly Displayed)
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    WoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("membership_tiers_dashboard_section"),
                        goldAccentBorder = true
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Section Header
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
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "MEMBERSHIP TIERS",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 12.5.sp,
                                            letterSpacing = 1.2.sp
                                        )
                                    )
                                }

                                Text(
                                    text = "Active: ${uiState.membershipTier.title}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSealInk,
                                        fontSize = 9.5.sp
                                    )
                                )
                            }

                            // 1. Free Tier (The Vault)
                            DashboardTierItem(
                                tier = MembershipTier.FREE,
                                currentTier = uiState.membershipTier,
                                priceText = "Free Forever",
                                featuresSummary = "50/30/20 ledger, daily safe spend, lessons 1-7, gold coin tracker",
                                onAction = { viewModel.setMembershipTier(MembershipTier.FREE) }
                            )

                            // 2. Pro Tier (Vault Pro)
                            DashboardTierItem(
                                tier = MembershipTier.PRO,
                                currentTier = uiState.membershipTier,
                                priceText = "$7.99/mo",
                                featuresSummary = "All 12 lessons, unlimited Compounding Simulator, CSV bank sync",
                                onAction = {
                                    if (uiState.membershipTier == MembershipTier.PRO) {
                                        viewModel.openUpgradeModal()
                                    } else {
                                        viewModel.setMembershipTier(MembershipTier.PRO)
                                    }
                                }
                            )

                            // 3. Premium Tier (Vault+)
                            DashboardTierItem(
                                tier = MembershipTier.PREMIUM,
                                currentTier = uiState.membershipTier,
                                priceText = "$19.99/mo or $149/yr",
                                featuresSummary = "Financial Roadmap, Monthly Review report cards, Priority Support, Community Hub",
                                onAction = {
                                    if (uiState.membershipTier == MembershipTier.PREMIUM) {
                                        viewModel.openUpgradeModal()
                                    } else {
                                        viewModel.setMembershipTier(MembershipTier.PREMIUM)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Section: Savings Goal Progress with Gold Coins
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    CoinPileProgressSection(
                        primaryGoal = uiState.primaryGoal,
                        currency = currency,
                        onDepositGold = { depositAmount ->
                            uiState.primaryGoal?.let { goal ->
                                viewModel.depositGoldToGoal(goal.id, depositAmount)
                            }
                        }
                    )
                }
            }

            // Daily Finance Lesson Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "DAILY FINANCE LESSON",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp,
                            fontSize = 12.sp,
                            color = SovereignGold
                        )
                    )

                    DailyLessonCard(
                        lesson = uiState.todayLesson,
                        isPremiumUser = uiState.isPro, // Pro or Premium unlocks daily lessons
                        onReadLesson = {
                            val lesson = uiState.todayLesson
                            if (lesson != null) {
                                if (lesson.isPremium && uiState.isFree) {
                                    viewModel.openUpgradeModal()
                                } else {
                                    viewModel.openLesson(lesson)
                                }
                            }
                        },
                        onToggleCompleted = { isCompleted ->
                            uiState.todayLesson?.let {
                                viewModel.toggleLessonCompletion(it.id, isCompleted)
                            }
                        }
                    )
                }
            }

            // Sovereign Curriculum Library + Investing Simulator Action
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FINANCIAL LESSONS & TOOLS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                                fontSize = 12.sp,
                                color = SovereignGold
                            )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Roadmap Quick Launch
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepOakBrown)
                                    .border(
                                        width = 1.dp,
                                        color = SovereignGold.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable(onClick = { viewModel.openRoadmap() })
                                    .padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timeline,
                                    contentDescription = "Roadmap",
                                    tint = SovereignGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Roadmap",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = SovereignGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            // Monthly Review Quick Launch
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepOakBrown)
                                    .border(
                                        width = 1.dp,
                                        color = SovereignGold.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable(onClick = { viewModel.openMonthlyReview() })
                                    .padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = "Monthly Review",
                                    tint = SovereignGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Review",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = SovereignGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            // Simulator Quick Launch (Locked for Free users)
                            val simInteraction = remember { MutableInteractionSource() }
                            val isSimPressed by simInteraction.collectIsPressedAsState()
                            val simBorder by animateColorAsState(
                                targetValue = if (isSimPressed) BurgundyGlow else SovereignGold.copy(alpha = 0.5f),
                                animationSpec = tween(150),
                                label = "sim_glow"
                            )

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepOakBrown)
                                    .border(
                                        width = if (isSimPressed) 2.dp else 1.dp,
                                        color = simBorder,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable(
                                        interactionSource = simInteraction,
                                        indication = null,
                                        onClick = {
                                            if (uiState.isFree) {
                                                viewModel.openUpgradeModal()
                                            } else {
                                                viewModel.openSimulator()
                                            }
                                        }
                                    )
                                    .padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.isFree) Icons.Default.Lock else Icons.Default.Calculate,
                                    contentDescription = "Investing Simulator",
                                    tint = SovereignGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Simulator",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = SovereignGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.lessons, key = { it.id }) { lesson ->
                            val isLocked = lesson.isPremium && uiState.isFree
                            WoodCard(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(130.dp)
                                    .clickable {
                                        if (isLocked) {
                                            viewModel.openUpgradeModal()
                                        } else {
                                            viewModel.openLesson(lesson)
                                        }
                                    },
                                goldAccentBorder = lesson.isCompleted
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "LESSON ${lesson.lessonNumber}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = SovereignGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        if (isLocked) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked",
                                                tint = SovereignGold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = lesson.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLocked) ParchmentCream.copy(alpha = 0.6f) else SovereignGold,
                                            fontSize = 13.sp
                                        ),
                                        maxLines = 2,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = if (isLocked) "Upgrade to read" else if (lesson.isCompleted) "✓ Completed" else "Tap to read",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = if (lesson.isCompleted) SovereignGold else ParchmentCream,
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

    // Modal Overlays
    activeSlip?.let { slipType ->
        val baselineCap = if (uiState.hasFixedIncome && uiState.monthlyIncome > 0) uiState.monthlyIncome else uiState.checkingBalance
        val monthlyBudget = when (slipType) {
            SlipType.EXPENSES -> (baselineCap * 0.50).coerceAtLeast(uiState.fixedExpensesTotal)
            SlipType.SAVINGS -> (baselineCap * 0.30).coerceAtLeast(0.0)
            SlipType.WANTS -> if (uiState.dailySafeSpend > 0) (uiState.dailySafeSpend * 30.0) else (baselineCap * 0.20)
        }
        SlipDetailModal(
            slipType = slipType,
            transactions = uiState.allTransactions,
            monthlyBudget = monthlyBudget,
            currency = currency,
            onDismiss = { viewModel.closeSlip() },
            onAddEntry = { viewModel.openAddTransaction(slipType) },
            onDeleteEntry = { id -> viewModel.deleteTransaction(id) }
        )
    }

    activeLesson?.let { lesson ->
        LessonDetailModal(
            lesson = lesson,
            onDismiss = { viewModel.closeLesson() },
            onToggleCompleted = { completed ->
                viewModel.toggleLessonCompletion(lesson.id, completed)
            }
        )
    }

    if (showTutor) {
        TutorChatModal(
            chatMessages = uiState.chatMessages,
            isAiThinking = uiState.isAiThinking,
            onSendMessage = { prompt -> viewModel.sendTutorMessage(prompt) },
            onClearChat = { viewModel.clearChatHistory() },
            onDismiss = { viewModel.closeTutor() }
        )
    }

    if (showSimulator) {
        InvestingSimulatorModal(
            simState = investingSim,
            currency = currency,
            onUpdateSim = { initCap, monDep, yrs, stkW, reW, gldW, bndW ->
                viewModel.updateSimulator(initCap, monDep, yrs, stkW, reW, gldW, bndW)
            },
            onDismiss = { viewModel.closeSimulator() }
        )
    }

    if (showUpgrade) {
        UpgradeMembershipModal(
            currentTier = uiState.membershipTier,
            onSelectTier = { tier ->
                viewModel.setMembershipTier(tier)
                viewModel.closeUpgradeModal()
            },
            onDismiss = { viewModel.closeUpgradeModal() }
        )
    }

    if (showAddTx) {
        AddTransactionDialog(
            initialSlipType = activeSlip ?: SlipType.EXPENSES,
            currency = currency,
            onDismiss = { viewModel.closeAddTransaction() },
            onSave = { title, amount, slipType, category, note ->
                viewModel.addTransaction(title, amount, slipType, category, note)
            }
        )
    }

    if (showPartner) {
        AccountabilityPartnerModal(
            partner = uiState.partner,
            recentNudges = uiState.recentNudges,
            userDailySafeSpend = uiState.dailySafeSpend,
            onInvitePartner = { email, name -> viewModel.invitePartner(email, name) },
            onCompleteCheckIn = { viewModel.completeDailyCheckIn() },
            onTogglePartnerCheckIn = { viewModel.togglePartnerCheckIn() },
            onSendNudge = { message -> viewModel.sendNudge(message) },
            onUnlinkPartner = { viewModel.unlinkPartner() },
            onResetStreak = { viewModel.resetStreak() },
            onDismiss = { viewModel.closePartnerModal() }
        )
    }

    if (showCsvImport) {
        CsvImportReviewModal(
            transactions = parsedCsvTransactions,
            errorMessage = csvImportError,
            successMessage = csvImportSuccessMessage,
            currency = currency,
            onSelectFile = { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        viewModel.loadCsvFromStream(stream)
                    }
                } catch (e: Exception) {
                    viewModel.openCsvImport()
                }
            },
            onLoadSampleCsv = { viewModel.loadSampleCsv() },
            onLoadCsvText = { text -> viewModel.loadCsvFromString(text) },
            onUpdateSlip = { id, newSlip -> viewModel.updateParsedTransactionSlip(id, newSlip) },
            onToggleInclusion = { id -> viewModel.toggleParsedTransactionInclusion(id) },
            onToggleAll = { include -> viewModel.toggleAllParsedTransactions(include) },
            onConfirmImport = { viewModel.commitCsvImport() },
            onDismiss = { viewModel.closeCsvImport() }
        )
    }

    if (showSettings) {
        SettingsModal(
            currentCurrency = currency,
            membershipTier = uiState.membershipTier,
            subscribedEmail = uiState.subscribedEmail,
            onSelectCurrency = { newCurrency -> viewModel.setCurrency(newCurrency) },
            onUpgradeMembership = {
                viewModel.closeSettings()
                viewModel.openUpgradeModal()
            },
            onSelectTier = { newTier -> viewModel.setMembershipTier(newTier) },
            onSubscribeEmail = { email -> viewModel.subscribeEmail(email) },
            onRecalibrateOnboarding = {
                viewModel.closeSettings()
                viewModel.resetOnboarding()
            },
            onDismiss = { viewModel.closeSettings() }
        )
    }

    if (showRoadmap && uiState.roadmapData != null) {
        PersonalizedRoadmapModal(
            roadmapData = uiState.roadmapData!!,
            isPremium = uiState.isPremium,
            onUpgradeClick = {
                viewModel.closeRoadmap()
                viewModel.openUpgradeModal()
            },
            onDismiss = { viewModel.closeRoadmap() }
        )
    }

    if (showMonthlyReview && uiState.monthlyReviewData != null) {
        MonthlyProgressReviewModal(
            reviewData = uiState.monthlyReviewData!!,
            isPremium = uiState.isPremium,
            onUpgradeClick = {
                viewModel.closeMonthlyReview()
                viewModel.openUpgradeModal()
            },
            onDismiss = { viewModel.closeMonthlyReview() }
        )
    }
}

@Composable
private fun DashboardTierItem(
    tier: MembershipTier,
    currentTier: MembershipTier,
    priceText: String,
    featuresSummary: String,
    onAction: () -> Unit
) {
    val isCurrent = currentTier == tier

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isCurrent) DeepOakBrown else DarkOakSurface,
        border = BorderStroke(
            1.dp,
            if (isCurrent) SovereignGold else RichOakBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = when (tier) {
                            MembershipTier.PREMIUM -> Icons.Default.WorkspacePremium
                            MembershipTier.PRO -> Icons.Default.Star
                            MembershipTier.FREE -> Icons.Default.Shield
                        },
                        contentDescription = null,
                        tint = SovereignGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = tier.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignGold,
                            fontSize = 12.5.sp
                        )
                    )
                    Text(
                        text = "• $priceText",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) GreenSealInk else ParchmentCream,
                            fontSize = 10.5.sp
                        )
                    )
                }

                Text(
                    text = featuresSummary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = MutedCream,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isCurrent) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
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
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(GreenSealInk)
                        )
                        Text(
                            text = "CURRENT TIER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = GreenSealInk,
                                fontSize = 8.sp
                            )
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, SovereignGold, RoundedCornerShape(6.dp))
                        .clickable { onAction() }
                        .testTag("dashboard_tier_action_${tier.id.lowercase()}"),
                    color = if (tier == MembershipTier.PREMIUM) BurgundyDeep else DeepOakBrown
                ) {
                    Text(
                        text = if (tier == MembershipTier.FREE) "SELECT" else "UPGRADE",
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
    }
}
