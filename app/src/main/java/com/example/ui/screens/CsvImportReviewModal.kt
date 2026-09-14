package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SlipType
import com.example.data.model.VaultCurrency
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
import com.example.ui.theme.SovereignGold
import com.example.util.ParsedCsvTransaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvImportReviewModal(
    transactions: List<ParsedCsvTransaction>,
    errorMessage: String?,
    successMessage: String?,
    currency: VaultCurrency = VaultCurrency.USD,
    onSelectFile: (Uri) -> Unit,
    onLoadSampleCsv: () -> Unit,
    onLoadCsvText: (String) -> Unit,
    onUpdateSlip: (id: String, newSlip: SlipType) -> Unit,
    onToggleInclusion: (id: String) -> Unit,
    onToggleAll: (Boolean) -> Unit,
    onConfirmImport: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onSelectFile(uri)
        }
    }

    var showPasteSection by remember { mutableStateOf(false) }
    var pastedContent by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<SlipType?>(null) }

    val includedList = transactions.filter { it.isIncluded }
    val totalSelectedCount = includedList.size
    val totalSelectedAmount = includedList.sumOf { it.amount }
    val expensesSum = includedList.filter { it.slipType == SlipType.EXPENSES }.sumOf { it.amount }
    val savingsSum = includedList.filter { it.slipType == SlipType.SAVINGS }.sumOf { it.amount }
    val wantsSum = includedList.filter { it.slipType == SlipType.WANTS }.sumOf { it.amount }

    val filteredList = remember(transactions, selectedFilter) {
        if (selectedFilter == null) transactions else transactions.filter { it.slipType == selectedFilter }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepOakBrown,
        dragHandle = null,
        modifier = Modifier
            .fillMaxHeight(0.94f)
            .testTag("csv_import_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(DeepOakBrown, DarkOakSurface, BurgundyDeep.copy(alpha = 0.65f))
                    )
                )
        ) {
            // Header Bar
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
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "CSV Import",
                            tint = SovereignGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "IMPORT TRANSACTIONS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 16.sp,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = if (transactions.isEmpty()) "Select Bank Statement CSV" else "Review & Confirm Ledger Slips",
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
                    modifier = Modifier.testTag("close_csv_import_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SovereignGold
                    )
                }
            }

            ThinGoldDivider(alpha = 0.4f, showEmblem = true)

            // Content Area
            if (transactions.isEmpty()) {
                // ==========================================
                // NO TRANSACTIONS LOADED: FILE PICKER FLOW
                // ==========================================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (errorMessage != null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RedStampInk.copy(alpha = 0.2f))
                                    .border(1.dp, RedStampInk, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = errorMessage,
                                    color = ParchmentCream,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }

                    item {
                        WoodCard(
                            modifier = Modifier.fillMaxWidth(),
                            goldAccentBorder = true
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(DeepOakBrown)
                                        .border(2.dp, SovereignGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                        contentDescription = "Bank Ledger",
                                        tint = SovereignGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Text(
                                    text = "SELECT BANK CSV EXPORT",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 15.sp,
                                        letterSpacing = 1.sp
                                    )
                                )

                                Text(
                                    text = "Import transactions from Chase, Bank of America, Fidelity, Vanguard, or any standard bank CSV export. The Vault will automatically categorize your entries into Expenses (50%), Savings (30%), and Wants (20%).",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Device File Picker Button
                                GoldButton(
                                    text = "CHOOSE CSV FILE FROM DEVICE",
                                    onClick = {
                                        filePickerLauncher.launch("*/*")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("choose_csv_device_button")
                                )

                                // Sample Bank Demo Button
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DeepOakBrown)
                                        .border(1.dp, SovereignGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                        .clickable { onLoadSampleCsv() }
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "⚡ Load Sample Bank Statement (Demo)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                // Toggle Paste Raw CSV
                                Text(
                                    text = if (showPasteSection) "Hide Manual Paste Area ▲" else "Or Paste CSV Text Directly ▼",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = SovereignGold.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier
                                        .clickable { showPasteSection = !showPasteSection }
                                        .padding(4.dp)
                                )

                                AnimatedVisibility(visible = showPasteSection) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = pastedContent,
                                            onValueChange = { pastedContent = it },
                                            label = { Text("Paste CSV Lines (Date, Description, Amount, Balance)", color = SovereignGold, fontSize = 10.sp) },
                                            placeholder = { Text("2026-08-20, Whole Foods Market, 142.80, 48000.00", color = MutedCream.copy(alpha = 0.5f), fontSize = 10.sp) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .testTag("pasted_csv_input"),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = DeepOakBrown,
                                                unfocusedContainerColor = DeepOakBrown,
                                                focusedTextColor = ParchmentCream,
                                                unfocusedTextColor = ParchmentCream,
                                                focusedIndicatorColor = SovereignGold,
                                                unfocusedIndicatorColor = GoldBorder
                                            )
                                        )

                                        GoldButton(
                                            text = "PARSE PASTED CSV",
                                            onClick = {
                                                if (pastedContent.isNotBlank()) {
                                                    onLoadCsvText(pastedContent)
                                                }
                                            },
                                            enabled = pastedContent.isNotBlank(),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Format specification guide
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepOakBrown.copy(alpha = 0.8f))
                                .border(1.dp, GoldBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = SovereignGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "SUPPORTED CSV FORMATS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Text(
                                text = "• Columns: Date, Description (or Payee/Merchant), Amount, Balance\n• Automatic keyword categorization for Needs, Savings, & Wants\n• You will be able to review and manually change any category before confirming.",
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
            } else {
                // ==========================================
                // TRANSACTIONS LOADED: REVIEW & EDIT SCREEN
                // ==========================================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary KPI Card
                    item {
                        WoodCard(
                            modifier = Modifier.fillMaxWidth(),
                            goldAccentBorder = true
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "LEDGER IMPORT SUMMARY",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp
                                        )
                                    )

                                    Text(
                                        text = "$totalSelectedCount / ${transactions.size} Selected",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                // 3 Slips breakdown tiles
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Expenses tile
                                    SlipSummaryPill(
                                        title = "EXPENSES (50%)",
                                        amount = expensesSum,
                                        currency = currency,
                                        color = BurgundyGlow,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Savings tile
                                    SlipSummaryPill(
                                        title = "SAVINGS (30%)",
                                        amount = savingsSum,
                                        currency = currency,
                                        color = SovereignGold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Wants tile
                                    SlipSummaryPill(
                                        title = "WANTS (20%)",
                                        amount = wantsSum,
                                        currency = currency,
                                        color = Color(0xFFC48B71),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Quick Bulk Actions: Select / Deselect All
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text(
                                            text = "Select All",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = SovereignGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier
                                                .clickable { onToggleAll(true) }
                                                .padding(vertical = 4.dp)
                                        )
                                        Text(
                                            text = "Deselect All",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = MutedCream,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier
                                                .clickable { onToggleAll(false) }
                                                .padding(vertical = 4.dp)
                                        )
                                    }

                                    // Re-pick file option
                                    Text(
                                        text = "Pick Another File",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = SovereignGold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier
                                            .clickable { filePickerLauncher.launch("*/*") }
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Filter Filter Tabs
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterTabPill(
                                label = "All (${transactions.size})",
                                isSelected = selectedFilter == null,
                                onClick = { selectedFilter = null },
                                modifier = Modifier.weight(1f)
                            )
                            FilterTabPill(
                                label = "Needs (${transactions.count { it.slipType == SlipType.EXPENSES }})",
                                isSelected = selectedFilter == SlipType.EXPENSES,
                                onClick = { selectedFilter = SlipType.EXPENSES },
                                modifier = Modifier.weight(1f)
                            )
                            FilterTabPill(
                                label = "Savings (${transactions.count { it.slipType == SlipType.SAVINGS }})",
                                isSelected = selectedFilter == SlipType.SAVINGS,
                                onClick = { selectedFilter = SlipType.SAVINGS },
                                modifier = Modifier.weight(1f)
                            )
                            FilterTabPill(
                                label = "Wants (${transactions.count { it.slipType == SlipType.WANTS }})",
                                isSelected = selectedFilter == SlipType.WANTS,
                                onClick = { selectedFilter = SlipType.WANTS },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Transaction Items
                    items(filteredList, key = { it.id }) { item ->
                        TransactionReviewRow(
                            item = item,
                            currency = currency,
                            onToggleInclusion = { onToggleInclusion(item.id) },
                            onSelectSlip = { newSlip -> onUpdateSlip(item.id, newSlip) }
                        )
                    }
                }

                // Bottom Sticky Confirmation Bar
                Surface(
                    color = DarkOakBrown,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SovereignGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL TO IMPORT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = MutedCream,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = currency.format(totalSelectedAmount),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold,
                                    fontSize = 16.sp
                                )
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Cancel
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepOakBrown)
                                    .border(1.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { onDismiss() }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            // Confirm
                            GoldButton(
                                text = "CONFIRM & IMPORT ($totalSelectedCount)",
                                onClick = onConfirmImport,
                                enabled = totalSelectedCount > 0,
                                modifier = Modifier.testTag("confirm_csv_import_button")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionReviewRow(
    item: ParsedCsvTransaction,
    currency: VaultCurrency = VaultCurrency.USD,
    onToggleInclusion: () -> Unit,
    onSelectSlip: (SlipType) -> Unit
) {
    val rowBg = if (item.isIncluded) DeepOakBrown else DeepOakBrown.copy(alpha = 0.5f)
    val borderCol = if (item.isIncluded) SovereignGold.copy(alpha = 0.4f) else GoldBorder.copy(alpha = 0.15f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBg)
            .border(1.dp, borderCol, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("csv_tx_row_${item.id}"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top row: Checkbox, Date & Description, and Amount
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Custom checkbox
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (item.isIncluded) SovereignGold else DarkOakBrown)
                        .border(1.dp, SovereignGold, RoundedCornerShape(4.dp))
                        .clickable { onToggleInclusion() },
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isIncluded) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Included",
                            tint = ObsidianBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isIncluded) ParchmentCream else MutedCream,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.rawDate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = SovereignGold.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        )
                        if (item.balance != null) {
                            Text(
                                text = "• Bal: ${currency.format(item.balance)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = MutedCream,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount display
            Text(
                text = currency.format(item.amount),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isIncluded) SovereignGold else MutedCream,
                    fontSize = 14.sp
                )
            )
        }

        // 3-Way Category Selection Segment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CategorySegmentButton(
                label = "50% Needs",
                isSelected = item.slipType == SlipType.EXPENSES,
                activeColor = BurgundyGlow,
                onClick = { onSelectSlip(SlipType.EXPENSES) },
                modifier = Modifier.weight(1f)
            )

            CategorySegmentButton(
                label = "30% Savings",
                isSelected = item.slipType == SlipType.SAVINGS,
                activeColor = SovereignGold,
                onClick = { onSelectSlip(SlipType.SAVINGS) },
                modifier = Modifier.weight(1f)
            )

            CategorySegmentButton(
                label = "20% Wants",
                isSelected = item.slipType == SlipType.WANTS,
                activeColor = Color(0xFFC48B71),
                onClick = { onSelectSlip(SlipType.WANTS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategorySegmentButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) activeColor.copy(alpha = 0.25f) else DarkOakBrown.copy(alpha = 0.6f)
    val border = if (isSelected) activeColor else GoldBorder.copy(alpha = 0.25f)
    val textColor = if (isSelected) ParchmentCream else MutedCream

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun SlipSummaryPill(
    title: String,
    amount: Double,
    currency: VaultCurrency = VaultCurrency.USD,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkOakBrown)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = color,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Text(
                text = currency.format(amount, false),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = ParchmentCream,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun FilterTabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) DeepOakBrown else DarkOakBrown.copy(alpha = 0.5f)
    val border = if (isSelected) SovereignGold else GoldBorder.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) SovereignGold else MutedCream,
                fontSize = 10.sp
            ),
            maxLines = 1
        )
    }
}
