package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SlipType
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.VaultCurrency
import com.example.ui.components.ThinGoldDivider
import com.example.ui.theme.AntiquePaper
import com.example.ui.theme.DarkLedgerInk
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.GreenSealInk
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RedStampInk
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SlateInk
import com.example.ui.theme.SovereignGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlipDetailModal(
    slipType: SlipType,
    transactions: List<TransactionEntity>,
    monthlyBudget: Double,
    currency: VaultCurrency = VaultCurrency.USD,
    onDismiss: () -> Unit,
    onAddEntry: () -> Unit,
    onDeleteEntry: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateFormatter = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.US)

    val slipTransactions = transactions.filter { it.slipType == slipType.name }
    val totalAmount = slipTransactions.sumOf { it.amount }

    val (title, codeNumber, guidelineQuote) = when (slipType) {
        SlipType.EXPENSES -> Triple(
            "EXPENSES LEDGER",
            "SLIP #01-EXP • 50% SPENDING BASELINE",
            "\"Beware of little expenses; a small leak will sink a great ship.\""
        )
        SlipType.SAVINGS -> Triple(
            "SAVINGS SLIP",
            "SLIP #02-SAV • 30% SAVINGS TARGET",
            "\"Compound interest is the royal scepter of the disciplined mind.\""
        )
        SlipType.WANTS -> Triple(
            "WANTS SLIP",
            "SLIP #03-WNT • 20% WANTS ALLOWANCE",
            "\"Enjoy your money through deliberate choices, never through debt.\""
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianBlack,
        dragHandle = null,
        modifier = Modifier.testTag("slip_detail_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = codeNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SovereignGold,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            color = SovereignGold,
                            fontSize = 20.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_slip_detail_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SovereignGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cream Paper Slip Ledger Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = ParchmentCream,
                border = BorderStroke(1.dp, AntiquePaper)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val w = size.width
                        val h = size.height
                        val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        drawLine(
                            color = Color(0x331A1410),
                            start = Offset(20f, 0f),
                            end = Offset(20f, h),
                            strokeWidth = 1f,
                            pathEffect = dashedEffect
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 28.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CURRENT LEDGER TOTAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SlateInk,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Target: ${currency.format(monthlyBudget, false)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateInk,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = currency.format(totalAmount),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = DarkLedgerInk,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = guidelineQuote,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateInk,
                                fontFamily = FontFamily.Serif,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Record Entry Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(listOf(SovereignGold, LustrousGold))
                    )
                    .clickable { onAddEntry() }
                    .padding(vertical = 12.dp)
                    .testTag("add_entry_to_slip_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "ADD TRANSACTION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ObsidianBlack,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ThinGoldDivider(alpha = 0.3f)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "TRANSACTION HISTORY (${slipTransactions.size} ENTRIES)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SovereignGold,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (slipTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions recorded for this slip yet.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedCream)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(slipTransactions, key = { it.id }) { tx ->
                        TransactionRowItem(
                            transaction = tx,
                            dateStr = dateFormatter.format(Date(tx.timestamp)),
                            currency = currency,
                            onDelete = { onDeleteEntry(tx.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRowItem(
    transaction: TransactionEntity,
    dateStr: String,
    currency: VaultCurrency,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        color = DarkOakBrown,
        border = BorderStroke(1.dp, RichOakBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        color = SovereignGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$dateStr • ${transaction.category}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MutedCream,
                        fontSize = 11.sp
                    )
                )
                if (transaction.note.isNotBlank()) {
                    Text(
                        text = "Note: ${transaction.note}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LustrousGold.copy(alpha = 0.75f),
                            fontFamily = FontFamily.SansSerif,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = currency.format(transaction.amount),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = LustrousGold,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = MutedCream.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
