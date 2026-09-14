package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SlipType
import com.example.data.model.VaultCurrency
import com.example.ui.components.ThinGoldDivider
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PolishedOak
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    initialSlipType: SlipType,
    currency: VaultCurrency = VaultCurrency.USD,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, slipType: SlipType, category: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedSlip by remember { mutableStateOf(initialSlipType) }
    var titleText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var categoryText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    val categories = when (selectedSlip) {
        SlipType.EXPENSES -> listOf("Fixed Overhead", "Estate", "Professional", "Utilities", "Taxes")
        SlipType.SAVINGS -> listOf("Capital Asset", "Gold Bullion", "Private Equities", "Treasuries", "Real Estate")
        SlipType.WANTS -> listOf("Discretionary Luxury", "Horology & Art", "Travel", "Private Club", "Dining")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianBlack,
        dragHandle = null,
        modifier = Modifier.testTag("add_transaction_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ADD TRANSACTION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = SovereignGold,
                        letterSpacing = 1.sp
                    )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_add_tx_button")
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

            // Slip Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(SlipType.EXPENSES, SlipType.SAVINGS, SlipType.WANTS).forEach { slip ->
                    val isSelected = selectedSlip == slip
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PolishedOak else DarkOakBrown)
                            .border(
                                1.dp,
                                if (isSelected) SovereignGold else RichOakBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedSlip = slip
                                if (categoryText.isBlank() || !categories.contains(categoryText)) {
                                    categoryText = categories.firstOrNull() ?: ""
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slip.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) LustrousGold else MutedCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input Field (Monospace Gold)
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                        amountText = input
                    }
                },
                label = { Text("Amount (${currency.symbol})", color = SovereignGold) },
                placeholder = { Text("e.g. 150.00", color = MutedCream.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_amount_input"),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkOakBrown,
                    unfocusedContainerColor = DarkOakBrown,
                    focusedTextColor = LustrousGold,
                    unfocusedTextColor = LustrousGold,
                    focusedIndicatorColor = SovereignGold,
                    unfocusedIndicatorColor = RichOakBorder
                ),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = LustrousGold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Title Field
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Title / Description", color = SovereignGold) },
                placeholder = { Text("e.g. Monthly groceries, Emergency fund", color = MutedCream.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_title_input"),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkOakBrown,
                    unfocusedContainerColor = DarkOakBrown,
                    focusedTextColor = ParchmentCream,
                    unfocusedTextColor = ParchmentCream,
                    focusedIndicatorColor = SovereignGold,
                    unfocusedIndicatorColor = RichOakBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Category Selector Chips
            Text(
                text = "Category",
                style = MaterialTheme.typography.bodySmall.copy(color = SovereignGold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(3).forEach { cat ->
                    val isSelected = categoryText == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) DeepOakBrown else DarkOakBrown)
                            .border(1.dp, if (isSelected) SovereignGold else RichOakBorder, RoundedCornerShape(6.dp))
                            .clickable { categoryText = cat }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSelected) LustrousGold else MutedCream,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Note Field
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Note (Optional)", color = SovereignGold) },
                placeholder = { Text("Add any extra details...", color = MutedCream.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_note_input"),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkOakBrown,
                    unfocusedContainerColor = DarkOakBrown,
                    focusedTextColor = ParchmentCream,
                    unfocusedTextColor = ParchmentCream,
                    focusedIndicatorColor = SovereignGold,
                    unfocusedIndicatorColor = RichOakBorder
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            val isValid = amountText.toDoubleOrNull() != null && (amountText.toDoubleOrNull() ?: 0.0) > 0
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isValid) Brush.horizontalGradient(listOf(SovereignGold, LustrousGold))
                        else Brush.horizontalGradient(listOf(DarkOakBrown, DeepOakBrown))
                    )
                    .clickable(enabled = isValid) {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        val finalCategory = if (categoryText.isNotBlank()) categoryText else categories.first()
                        onSave(titleText, amount, selectedSlip, finalCategory, noteText)
                    }
                    .padding(vertical = 14.dp)
                    .testTag("save_transaction_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SAVE ENTRY",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isValid) ObsidianBlack else MutedCream.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
