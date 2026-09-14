package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.local.entity.SlipType
import com.example.data.model.VaultCurrency
import com.example.ui.components.BankSlipCard
import com.example.ui.components.GoldButton
import com.example.ui.components.ThinGoldDivider
import com.example.ui.components.WoodCard
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PolishedOak
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold
import java.util.Locale

data class FixedExpenseItem(
    val id: Long = System.currentTimeMillis() + (0..1000).random(),
    val name: String,
    val amount: Double
)

enum class OnboardingStep(val progressIndex: Int) {
    WELCOME(0),
    MONTHLY_INCOME(1),
    CHECKING_BASELINE(1),
    FIXED_EXPENSES(2),
    TOTAL_DEBT(3),
    SAVINGS_BALANCE(4),
    REALITY_REVEAL(5)
}

@Composable
fun TierOneOnboardingScreen(
    currentCurrency: VaultCurrency = VaultCurrency.USD,
    initialIncome: Double? = null,
    initialChecking: Double? = null,
    initialDebt: Double? = null,
    initialSavings: Double? = null,
    initialInvestments: Double? = null,
    initialOccupation: String? = null,
    initialEmployer: String? = null,
    initialIncomeType: String? = null,
    initialHasFixedIncome: Boolean? = null,
    onSelectCurrency: (VaultCurrency) -> Unit = {},
    onComplete: (
        monthlyIncome: Double,
        fixedExpenses: List<Pair<String, Double>>,
        totalDebt: Double,
        savingsBalance: Double,
        hasFixedIncome: Boolean,
        checkingBalance: Double,
        investmentsBalance: Double,
        occupation: String,
        employerOrWorkplace: String,
        incomeType: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Steps state machine
    var currentStep by remember { mutableStateOf(OnboardingStep.WELCOME) }
    var selectedCurrency by remember(currentCurrency) { mutableStateOf(currentCurrency) }
    var showCurrencyDialog by remember { mutableStateOf(false) }

    var hasFixedIncome by remember { mutableStateOf(initialHasFixedIncome ?: true) }
    var incomeText by remember { mutableStateOf(if (initialIncome != null && initialIncome > 0) String.format(Locale.US, "%.0f", initialIncome) else "6500") }
    var checkingText by remember { mutableStateOf(if (initialChecking != null && initialChecking > 0) String.format(Locale.US, "%.0f", initialChecking) else "3000") }
    var debtText by remember { mutableStateOf(if (initialDebt != null && initialDebt > 0) String.format(Locale.US, "%.0f", initialDebt) else "4200") }
    var savingsText by remember { mutableStateOf(if (initialSavings != null && initialSavings > 0) String.format(Locale.US, "%.0f", initialSavings) else "18500") }
    var investmentsText by remember { mutableStateOf(if (initialInvestments != null && initialInvestments > 0) String.format(Locale.US, "%.0f", initialInvestments) else "0") }
    var occupationText by remember { mutableStateOf(initialOccupation.orEmpty()) }
    var employerText by remember { mutableStateOf(initialEmployer.orEmpty()) }
    var selectedIncomeType by remember { mutableStateOf(if (!initialIncomeType.isNullOrBlank()) initialIncomeType else "FIXED_SALARY") }

    val fixedExpensesList = remember {
        mutableStateListOf(
            FixedExpenseItem(name = "Rent / Housing", amount = 2100.0),
            FixedExpenseItem(name = "Car & Insurance", amount = 550.0),
            FixedExpenseItem(name = "Utilities & Internet", amount = 280.0)
        )
    }

    var newExpenseName by remember { mutableStateOf("") }
    var newExpenseAmount by remember { mutableStateOf("") }

    // Calculations
    val incomeVal = if (hasFixedIncome) (incomeText.toDoubleOrNull() ?: 0.0) else 0.0
    val checkingVal = checkingText.toDoubleOrNull() ?: 0.0
    val totalFixedVal = fixedExpensesList.sumOf { it.amount }
    val debtVal = debtText.toDoubleOrNull() ?: 0.0
    val savingsVal = savingsText.toDoubleOrNull() ?: 0.0
    val investmentsVal = investmentsText.toDoubleOrNull() ?: 0.0

    val savingsAllocationMonthly = if (hasFixedIncome && incomeVal > 0) (incomeVal * 0.30).coerceAtLeast(0.0) else (checkingVal * 0.30).coerceAtLeast(0.0)
    val discretionaryPool = if (hasFixedIncome && incomeVal > 0) {
        (incomeVal - totalFixedVal - savingsAllocationMonthly).coerceAtLeast(0.0)
    } else {
        (checkingVal - totalFixedVal - savingsAllocationMonthly).coerceAtLeast(0.0)
    }

    // App logic:
    // - If monthly income > 0 → Use income for Daily Safe Spend
    // - If monthly income = 0 or skipped → Use checking balance ÷ 30 for Daily Safe Spend
    val dailySafeSpend = if (hasFixedIncome && incomeVal > 0) {
        discretionaryPool / 30.0
    } else {
        if (discretionaryPool > 0) discretionaryPool / 30.0 else (checkingVal / 30.0)
    }

    val fixedExpenseSuggestions = listOf(
        "Rent / Mortgage",
        "Car Payment",
        "Insurance",
        "Utilities",
        "Groceries",
        "Phone / Internet",
        "Subscriptions"
    )

    val handleCurrencyChange: (VaultCurrency) -> Unit = { newCurr ->
        val oldCurr = selectedCurrency
        if (oldCurr != newCurr) {
            incomeText.toDoubleOrNull()?.let { oldVal ->
                val newVal = oldCurr.convertTo(oldVal, newCurr)
                incomeText = if (newCurr.hasDecimals) String.format(Locale.US, "%.0f", newVal) else newVal.toLong().toString()
            }
            checkingText.toDoubleOrNull()?.let { oldVal ->
                val newVal = oldCurr.convertTo(oldVal, newCurr)
                checkingText = if (newCurr.hasDecimals) String.format(Locale.US, "%.0f", newVal) else newVal.toLong().toString()
            }
            debtText.toDoubleOrNull()?.let { oldVal ->
                val newVal = oldCurr.convertTo(oldVal, newCurr)
                debtText = if (newCurr.hasDecimals) String.format(Locale.US, "%.0f", newVal) else newVal.toLong().toString()
            }
            savingsText.toDoubleOrNull()?.let { oldVal ->
                val newVal = oldCurr.convertTo(oldVal, newCurr)
                savingsText = if (newCurr.hasDecimals) String.format(Locale.US, "%.0f", newVal) else newVal.toLong().toString()
            }
            val updatedExpenses = fixedExpensesList.map { item ->
                val converted = oldCurr.convertTo(item.amount, newCurr)
                FixedExpenseItem(
                    id = item.id,
                    name = item.name,
                    amount = Math.round(converted * 100.0) / 100.0
                )
            }
            fixedExpensesList.clear()
            fixedExpensesList.addAll(updatedExpenses)
            selectedCurrency = newCurr
            onSelectCurrency(newCurr)
        }
    }

    val handleBack: () -> Unit = {
        focusManager.clearFocus()
        keyboardController?.hide()
        when (currentStep) {
            OnboardingStep.WELCOME -> {}
            OnboardingStep.MONTHLY_INCOME -> currentStep = OnboardingStep.WELCOME
            OnboardingStep.CHECKING_BASELINE -> currentStep = OnboardingStep.MONTHLY_INCOME
            OnboardingStep.FIXED_EXPENSES -> {
                currentStep = if (hasFixedIncome) OnboardingStep.MONTHLY_INCOME else OnboardingStep.CHECKING_BASELINE
            }
            OnboardingStep.TOTAL_DEBT -> currentStep = OnboardingStep.FIXED_EXPENSES
            OnboardingStep.SAVINGS_BALANCE -> currentStep = OnboardingStep.TOTAL_DEBT
            OnboardingStep.REALITY_REVEAL -> currentStep = OnboardingStep.SAVINGS_BALANCE
        }
    }

    // Handle system back navigation across steps
    BackHandler(enabled = currentStep != OnboardingStep.WELCOME) {
        handleBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Navigation Bar & Step Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep != OnboardingStep.WELCOME) {
                    IconButton(
                        onClick = handleBack,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SovereignGold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(36.dp))
                }

                // Brand Title in Center (only shown during calibration steps; welcome step uses clean WELCOME label to let hero card shine)
                if (currentStep != OnboardingStep.WELCOME) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(DeepOakBrown)
                                .border(1.2.dp, SovereignGold, CircleShape),
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
                        Text(
                            text = "THE VAULT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 1.5.sp,
                                color = SovereignGold
                            )
                        )
                    }
                } else {
                    Text(
                        text = "WELCOME",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 2.sp,
                            color = SovereignGold.copy(alpha = 0.85f)
                        )
                    )
                }

                // Top-right indicator
                if (currentStep == OnboardingStep.WELCOME) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepOakBrown)
                            .border(1.dp, SovereignGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = selectedCurrency.code,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 10.sp
                            )
                        )
                    }
                } else if (currentStep.progressIndex in 1..4) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Interactive quick currency switcher pill
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showCurrencyDialog = true }
                                .border(1.dp, SovereignGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp)),
                            color = DeepOakBrown
                        ) {
                            Text(
                                text = "${selectedCurrency.symbol} ${selectedCurrency.code}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "Step ${currentStep.progressIndex}/4",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 10.sp
                            )
                        )
                    }
                } else {
                    // Reveal screen
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showCurrencyDialog = true }
                            .border(1.dp, SovereignGold, RoundedCornerShape(6.dp)),
                        color = DeepOakBrown
                    ) {
                        Text(
                            text = "${selectedCurrency.symbol} ${selectedCurrency.code}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step Progress Indicator Bars (for calibration steps 1-4)
            if (currentStep.progressIndex in 1..4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (step in 1..4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (step <= currentStep.progressIndex) SovereignGold else RichOakBorder
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Animated Question / Screen Content
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "onboarding_step_transition",
                modifier = Modifier.fillMaxSize()
            ) { targetStep ->
                when (targetStep) {
                    OnboardingStep.WELCOME -> ScreenZeroCurrencyAndWelcome(
                        selectedCurrency = selectedCurrency,
                        onSelectCurrency = handleCurrencyChange,
                        onStartCalibration = {
                            currentStep = OnboardingStep.MONTHLY_INCOME
                        }
                    )

                    OnboardingStep.MONTHLY_INCOME -> ScreenOneMonthlyIncome(
                        currency = selectedCurrency,
                        incomeText = incomeText,
                        onIncomeChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                                incomeText = input
                            }
                        },
                        occupationText = occupationText,
                        onOccupationChange = { occupationText = it },
                        employerText = employerText,
                        onEmployerChange = { employerText = it },
                        incomeType = selectedIncomeType,
                        onIncomeTypeChange = { selectedIncomeType = it },
                        onSkipNoFixedIncome = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            hasFixedIncome = false
                            currentStep = OnboardingStep.CHECKING_BASELINE
                        },
                        onNext = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            hasFixedIncome = true
                            currentStep = OnboardingStep.FIXED_EXPENSES
                        }
                    )

                    OnboardingStep.CHECKING_BASELINE -> ScreenOneNoFixedIncomeChecking(
                        currency = selectedCurrency,
                        checkingText = checkingText,
                        onCheckingChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                                checkingText = input
                            }
                        },
                        onBackToFixedIncome = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            hasFixedIncome = true
                            currentStep = OnboardingStep.MONTHLY_INCOME
                        },
                        onNext = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            hasFixedIncome = false
                            currentStep = OnboardingStep.FIXED_EXPENSES
                        }
                    )

                    OnboardingStep.FIXED_EXPENSES -> ScreenTwoFixedExpenses(
                        currency = selectedCurrency,
                        expensesList = fixedExpensesList,
                        newExpenseName = newExpenseName,
                        newExpenseAmount = newExpenseAmount,
                        onNameChange = { newExpenseName = it },
                        onAmountChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                                newExpenseAmount = input
                            }
                        },
                        onAddExpense = {
                            val amt = newExpenseAmount.toDoubleOrNull() ?: 0.0
                            if (newExpenseName.isNotBlank() && amt > 0) {
                                fixedExpensesList.add(FixedExpenseItem(name = newExpenseName.trim(), amount = amt))
                                newExpenseName = ""
                                newExpenseAmount = ""
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        },
                        onRemoveExpense = { item ->
                            fixedExpensesList.remove(item)
                        },
                        suggestions = fixedExpenseSuggestions,
                        onSelectSuggestion = { suggestion ->
                            newExpenseName = suggestion
                        },
                        totalFixedVal = totalFixedVal,
                        onNext = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            currentStep = OnboardingStep.TOTAL_DEBT
                        }
                    )

                    OnboardingStep.TOTAL_DEBT -> ScreenThreeTotalDebt(
                        currency = selectedCurrency,
                        debtText = debtText,
                        onDebtChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                                debtText = input
                            }
                        },
                        onNext = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            currentStep = OnboardingStep.SAVINGS_BALANCE
                        }
                    )

                    OnboardingStep.SAVINGS_BALANCE -> ScreenFourSavingsBalance(
                        currency = selectedCurrency,
                        savingsText = savingsText,
                        onSavingsChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                                savingsText = input
                            }
                        },
                        investmentsText = investmentsText,
                        onInvestmentsChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                                investmentsText = input
                            }
                        },
                        onShowReality = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            currentStep = OnboardingStep.REALITY_REVEAL
                        }
                    )

                    OnboardingStep.REALITY_REVEAL -> ScreenFiveRealityReveal(
                        currency = selectedCurrency,
                        dailySafeSpend = dailySafeSpend,
                        totalFixedVal = totalFixedVal,
                        incomeVal = incomeVal,
                        hasFixedIncome = hasFixedIncome,
                        checkingVal = checkingVal,
                        savingsVal = savingsVal,
                        investmentsVal = investmentsVal,
                        debtVal = debtVal,
                        discretionaryPool = discretionaryPool,
                        savingsAllocationMonthly = savingsAllocationMonthly,
                        fixedExpensesCount = fixedExpensesList.size,
                        fixedExpensesList = fixedExpensesList,
                        onComplete = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            val fixedPairs = fixedExpensesList.map { it.name to it.amount }
                            onComplete(
                                if (hasFixedIncome) incomeVal else 0.0,
                                fixedPairs,
                                debtVal,
                                savingsVal,
                                hasFixedIncome,
                                checkingVal,
                                investmentsVal,
                                occupationText,
                                employerText,
                                selectedIncomeType
                            )
                        }
                    )
                }
            }
        }
    }

    // Quick Currency Switcher Dialog if tapped during onboarding
    if (showCurrencyDialog) {
        Dialog(onDismissRequest = { showCurrencyDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                color = DeepOakBrown,
                border = BorderStroke(1.dp, SovereignGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SWITCH CURRENCY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 14.sp
                            )
                        )
                        IconButton(
                            onClick = { showCurrencyDialog = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = ParchmentCream
                            )
                        }
                    }

                    ThinGoldDivider(alpha = 0.3f)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VaultCurrency.entries.forEach { curr ->
                            val isSelected = curr == selectedCurrency
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        if (isSelected) SovereignGold else RichOakBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        handleCurrencyChange(curr)
                                        showCurrencyDialog = false
                                    },
                                color = if (isSelected) PolishedOak else DarkOakBrown
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = curr.symbol,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold
                                            )
                                        )
                                        Column {
                                            Text(
                                                text = "${curr.code} - ${curr.displayName}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ParchmentCream
                                                )
                                            )
                                            Text(
                                                text = curr.format(6500.0, decimals = false),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    color = MutedCream,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = SovereignGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// CUSTOMER-CENTRIC MATH & HELP TOOLTIP DIALOG
// -------------------------------------------------------------------------------------------------
@Composable
private fun InfoExplanationDialog(
    title: String,
    explanation: String,
    formula: String? = null,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            color = DeepOakBrown,
            border = BorderStroke(1.5.dp, SovereignGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PolishedOak)
                                .border(1.dp, SovereignGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SovereignGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 14.5.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SovereignGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                ThinGoldDivider(alpha = 0.3f)

                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = ParchmentCream,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                )

                if (formula != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = PolishedOak,
                        border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "CALCULATION FORMULA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = formula,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = LustrousGold,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                GoldButton(
                    text = "GOT IT",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun InfoTooltipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "How this is calculated"
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(26.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = contentDescription,
            tint = SovereignGold.copy(alpha = 0.85f),
            modifier = Modifier.size(16.dp)
        )
    }
}

// -------------------------------------------------------------------------------------------------
// SCREEN 0: CURRENCY SELECTION & WELCOME WITH FINANCIAL DISCLAIMER
// -------------------------------------------------------------------------------------------------
@Composable
private fun ScreenZeroCurrencyAndWelcome(
    selectedCurrency: VaultCurrency,
    onSelectCurrency: (VaultCurrency) -> Unit,
    onStartCalibration: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Hero Brand Logo Card
            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_hero_brand_logo_card"),
                    goldAccentBorder = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, SovereignGold, RoundedCornerShape(16.dp)),
                            color = DeepOakBrown,
                            shadowElevation = 8.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.the_vault_logo_emblem_1788026193523),
                                contentDescription = "The Vault Official Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Executive Wealth Architecture",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 19.sp,
                                letterSpacing = 1.2.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Private Cash Flow Allocation & 3-Slip Wealth Discipline",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 12.5.sp,
                                lineHeight = 17.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Welcome & Currency Switcher Header Card
            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_welcome_currency_card"),
                    goldAccentBorder = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(DeepOakBrown)
                                .border(1.5.dp, SovereignGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = "Currency Standard",
                                tint = SovereignGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "SELECT YOUR BASE CURRENCY",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 17.sp,
                                letterSpacing = 1.1.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Select your denomination standard. All 50/30/20 slips, Daily Safe Spend calculations, and wealth simulations will be denominated in this currency.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        ThinGoldDivider(alpha = 0.35f)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Currency Selector List
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VaultCurrency.entries.forEach { curr ->
                                val isSelected = curr == selectedCurrency
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            1.5.dp,
                                            if (isSelected) SovereignGold else RichOakBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onSelectCurrency(curr) }
                                        .testTag("currency_option_${curr.code}"),
                                    color = if (isSelected) PolishedOak else DeepOakBrown
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) SovereignGold.copy(alpha = 0.25f) else DarkOakBrown)
                                                    .border(1.dp, if (isSelected) SovereignGold else RichOakBorder, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = curr.symbol,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) SovereignGold else LustrousGold,
                                                        fontSize = 14.sp
                                                    )
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = "${curr.code} • ${curr.displayName}",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.SansSerif,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) SovereignGold else ParchmentCream,
                                                        fontSize = 13.sp
                                                    )
                                                )
                                                val rateInfo = if (curr == VaultCurrency.USD) "1.00 USD (Base Standard)" else "1 USD = ${curr.format(curr.rateToUsd, decimals = curr.hasDecimals)}"
                                                Text(
                                                    text = "${curr.countryOrRegion} • $rateInfo",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        color = MutedCream,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clip(CircleShape)
                                                    .background(SovereignGold),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = ObsidianBlack,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Terms of Service Card
            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_terms_of_service_card")
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

            // Privacy Policy Card
            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_privacy_policy_card")
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

            // Financial Advisory Disclaimer Card
            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_disclaimer_card")
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
                            text = "The Vault is a self-directed budgeting ledger, cash-flow discipline system, and financial literacy simulator. It is designed for educational, illustrative, and organizational purposes only and does NOT constitute certified financial, investment, tax, or legal advice. Calculations and safe spend metrics are estimates based on user inputs. Please consult a licensed Financial Advisor, CFP®, CPA, or fiduciary expert for personalized financial planning.",
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

        // Bottom CTA: Confirm Currency & Start Calibration
        GoldButton(
            text = "CONFIRM ${selectedCurrency.code} & START",
            onClick = onStartCalibration,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("start_calibration_button")
        )
    }
}

// -------------------------------------------------------------------------------------------------
// SCREEN 1: MONTHLY INCOME & CAREER
// -------------------------------------------------------------------------------------------------
@Composable
private fun ScreenOneMonthlyIncome(
    currency: VaultCurrency,
    incomeText: String,
    onIncomeChange: (String) -> Unit,
    occupationText: String,
    onOccupationChange: (String) -> Unit,
    employerText: String,
    onEmployerChange: (String) -> Unit,
    incomeType: String,
    onIncomeTypeChange: (String) -> Unit,
    onSkipNoFixedIncome: () -> Unit,
    onNext: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showInfoDialog by remember { mutableStateOf(false) }

    val incomeTypes = listOf(
        "FIXED_SALARY" to "Fixed Salary (W-2)",
        "HOURLY" to "Hourly / Shift",
        "FREELANCE" to "Freelance / Gig",
        "BUSINESS" to "Business / Founder",
        "STUDENT" to "Student / Allowance",
        "INVESTMENT" to "Investments / Passive"
    )

    if (showInfoDialog) {
        InfoExplanationDialog(
            title = "Monthly Income After Taxes",
            explanation = "We'll use this to calculate your Daily Safe Spend—what you can safely spend each day after bills and savings.",
            formula = "Monthly Income - Fixed Bills - 30% Savings = Spending Pool ÷ 30",
            onDismiss = { showInfoDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_q1_card"),
                    goldAccentBorder = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
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
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "What is your monthly income after taxes?",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 17.5.sp
                                    )
                                )
                            }

                            InfoTooltipButton(
                                onClick = { showInfoDialog = true },
                                contentDescription = "How income is used"
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "We'll use this to calculate your Daily Safe Spend—what you can safely spend each day after bills and savings.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "If you don't have a fixed income, tap \"I don't have a fixed income\" below.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = MutedCream,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = incomeText,
                            onValueChange = onIncomeChange,
                            prefix = {
                                Text("${currency.symbol} ", color = SovereignGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            },
                            placeholder = {
                                Text("6500", color = MutedCream.copy(alpha = 0.5f), fontSize = 18.sp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("income_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DeepOakBrown,
                                unfocusedContainerColor = DeepOakBrown,
                                focusedTextColor = ParchmentCream,
                                unfocusedTextColor = ParchmentCream,
                                focusedIndicatorColor = SovereignGold,
                                unfocusedIndicatorColor = RichOakBorder
                            ),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick preset buttons for convenience (covering teens, young adults, professionals)
                        Text(
                            text = "Quick Select:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("2200", "3800", "5500", "7500", "11000").forEach { preset ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onIncomeChange(preset) },
                                    color = if (incomeText == preset) SovereignGold.copy(alpha = 0.25f) else PolishedOak,
                                    border = BorderStroke(
                                        1.dp,
                                        if (incomeText == preset) SovereignGold else RichOakBorder
                                    )
                                ) {
                                    Text(
                                        text = "${currency.symbol}$preset",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = if (incomeText == preset) SovereignGold else ParchmentCream,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        ThinGoldDivider(alpha = 0.25f)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Income Stream Type Chips
                        Text(
                            text = "Income Stream Type",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            incomeTypes.chunked(2).forEach { rowPair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowPair.forEach { (typeKey, label) ->
                                        val isSelected = incomeType == typeKey
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onIncomeTypeChange(typeKey) },
                                            color = if (isSelected) SovereignGold.copy(alpha = 0.22f) else PolishedOak,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isSelected) SovereignGold else RichOakBorder
                                            )
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.SansSerif,
                                                    color = if (isSelected) SovereignGold else ParchmentCream,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 11.5.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        ThinGoldDivider(alpha = 0.25f)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Career & Workplace Details (Tailored to the user, strictly device-private)
                        Text(
                            text = "Work & Career Details (Optional)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tailor personalized insights to your field. You choose whether to share.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Occupation Input
                        OutlinedTextField(
                            value = occupationText,
                            onValueChange = onOccupationChange,
                            label = { Text("What type of work do you do?", color = MutedCream, fontSize = 12.sp) },
                            placeholder = { Text("e.g. Software, Trades, Retail, Student, Healthcare", color = MutedCream.copy(alpha = 0.5f), fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("occupation_input"),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DeepOakBrown,
                                unfocusedContainerColor = DeepOakBrown,
                                focusedTextColor = ParchmentCream,
                                unfocusedTextColor = ParchmentCream,
                                focusedIndicatorColor = SovereignGold,
                                unfocusedIndicatorColor = RichOakBorder
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = ParchmentCream, fontSize = 13.sp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Employer / Workplace Input
                        OutlinedTextField(
                            value = employerText,
                            onValueChange = onEmployerChange,
                            label = { Text("Where do you work / Workplace", color = MutedCream, fontSize = 12.sp) },
                            placeholder = { Text("e.g. Acme Corp, Self-Employed, City Hospital, University", color = MutedCream.copy(alpha = 0.5f), fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("employer_input"),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DeepOakBrown,
                                unfocusedContainerColor = DeepOakBrown,
                                focusedTextColor = ParchmentCream,
                                unfocusedTextColor = ParchmentCream,
                                focusedIndicatorColor = SovereignGold,
                                unfocusedIndicatorColor = RichOakBorder
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = ParchmentCream, fontSize = 13.sp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Privacy badge reassuring device-only storage
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = DeepOakBrown.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = SovereignGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "100% Private & Device-Only: Stored strictly on your phone. Never sent to any external server or third party.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream.copy(alpha = 0.85f),
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        ThinGoldDivider(alpha = 0.25f)
                        Spacer(modifier = Modifier.height(14.dp))

                        // 'I don't have a fixed income' Button (tap to skip)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSkipNoFixedIncome() }
                                .testTag("no_fixed_income_button"),
                            color = DeepOakBrown,
                            border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = null,
                                    tint = SovereignGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "I don't have a fixed income",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Next Button
        GoldButton(
            text = "NEXT",
            onClick = onNext,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("next_button_step_1")
        )
    }
}

// -------------------------------------------------------------------------------------------------
// SCREEN 1-ALT: CHECKING BALANCE (VARIABLE INCOME BASELINE)
// -------------------------------------------------------------------------------------------------
@Composable
private fun ScreenOneNoFixedIncomeChecking(
    currency: VaultCurrency,
    checkingText: String,
    onCheckingChange: (String) -> Unit,
    onBackToFixedIncome: () -> Unit,
    onNext: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        InfoExplanationDialog(
            title = "Checking Balance Baseline",
            explanation = "We'll use this as your baseline. You can update it anytime. When you have variable income, your available balance helps establish a safe pacing limit.",
            formula = "(Checking Balance - Fixed Bills - 30% Savings) ÷ 30 Days = Daily Safe Spend",
            onDismiss = { showInfoDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_checking_card"),
                    goldAccentBorder = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
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
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "What is your current checking balance?",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 17.5.sp
                                    )
                                )
                            }

                            InfoTooltipButton(
                                onClick = { showInfoDialog = true },
                                contentDescription = "How checking balance is used"
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "We'll use this as your baseline. You can update it anytime.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = checkingText,
                            onValueChange = onCheckingChange,
                            prefix = {
                                Text("${currency.symbol} ", color = SovereignGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            },
                            placeholder = {
                                Text("3000", color = MutedCream.copy(alpha = 0.5f), fontSize = 18.sp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checking_balance_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DeepOakBrown,
                                unfocusedContainerColor = DeepOakBrown,
                                focusedTextColor = ParchmentCream,
                                unfocusedTextColor = ParchmentCream,
                                focusedIndicatorColor = SovereignGold,
                                unfocusedIndicatorColor = RichOakBorder
                            ),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    onNext()
                                }
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick preset buttons for convenience
                        Text(
                            text = "Quick Select:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("1000", "2500", "5000", "8000", "15000").forEach { preset ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onCheckingChange(preset) },
                                    color = if (checkingText == preset) SovereignGold.copy(alpha = 0.25f) else PolishedOak,
                                    border = BorderStroke(
                                        1.dp,
                                        if (checkingText == preset) SovereignGold else RichOakBorder
                                    )
                                ) {
                                    Text(
                                        text = "${currency.symbol}$preset",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = if (checkingText == preset) SovereignGold else ParchmentCream,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Back to Fixed Income option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onBackToFixedIncome() },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "← I actually have a fixed monthly income",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = SovereignGold.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // Bottom Next Button
        GoldButton(
            text = "NEXT",
            onClick = onNext,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("next_button_step_checking")
        )
    }
}

// -------------------------------------------------------------------------------------------------
// SCREEN 2: FIXED EXPENSES
// -------------------------------------------------------------------------------------------------
@Composable
private fun ScreenTwoFixedExpenses(
    currency: VaultCurrency,
    expensesList: List<FixedExpenseItem>,
    newExpenseName: String,
    newExpenseAmount: String,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onAddExpense: () -> Unit,
    onRemoveExpense: (FixedExpenseItem) -> Unit,
    suggestions: List<String>,
    onSelectSuggestion: (String) -> Unit,
    totalFixedVal: Double,
    onNext: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        InfoExplanationDialog(
            title = "Fixed Expenses",
            explanation = "This tells us your fixed costs so we can see what's left for you. These are mandatory bills that recur every month.",
            formula = "Rent + Vehicle + Utilities + Insurance + Subscriptions = Total Fixed Costs",
            onDismiss = { showInfoDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_q2_card"),
                    goldAccentBorder = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
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
                                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "List every fixed expense you have.",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 17.5.sp
                                    )
                                )
                            }

                            InfoTooltipButton(
                                onClick = { showInfoDialog = true },
                                contentDescription = "How fixed expenses are used"
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "This tells us your fixed costs so we can see what's left for you.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick suggestion pills
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(suggestions) { tag ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onSelectSuggestion(tag) },
                                    color = PolishedOak,
                                    border = BorderStroke(1.dp, RichOakBorder)
                                ) {
                                    Text(
                                        text = "+ $tag",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = ParchmentCream,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Input row to add new item
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newExpenseName,
                                onValueChange = onNameChange,
                                placeholder = { Text("Expense name", color = MutedCream.copy(alpha = 0.5f), fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("expense_name_input"),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = DeepOakBrown,
                                    unfocusedContainerColor = DeepOakBrown,
                                    focusedTextColor = ParchmentCream,
                                    unfocusedTextColor = ParchmentCream,
                                    focusedIndicatorColor = SovereignGold,
                                    unfocusedIndicatorColor = RichOakBorder
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Right) }
                                ),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newExpenseAmount,
                                onValueChange = onAmountChange,
                                prefix = { Text(currency.symbol, color = SovereignGold, fontWeight = FontWeight.Bold) },
                                placeholder = { Text("0", color = MutedCream.copy(alpha = 0.5f), fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("expense_amount_input"),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = DeepOakBrown,
                                    unfocusedContainerColor = DeepOakBrown,
                                    focusedTextColor = ParchmentCream,
                                    unfocusedTextColor = ParchmentCream,
                                    focusedIndicatorColor = SovereignGold,
                                    unfocusedIndicatorColor = RichOakBorder
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = { onAddExpense() }
                                ),
                                singleLine = true
                            )

                            // Plus Add Button
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepOakBrown)
                                    .border(1.dp, SovereignGold, RoundedCornerShape(8.dp))
                                    .clickable { onAddExpense() }
                                    .testTag("add_fixed_expense_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Expense",
                                    tint = SovereignGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Current Fixed Expenses List
                        Text(
                            text = "YOUR FIXED BILLS (${expensesList.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (expensesList.isEmpty()) {
                            Text(
                                text = "No fixed expenses added yet. Add at least one or click Next.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = ParchmentCream
                                )
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                expensesList.forEach { expense ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DeepOakBrown)
                                            .border(1.dp, RichOakBorder, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = expense.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = currency.format(expense.amount, decimals = false),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SovereignGold
                                                )
                                            )
                                            IconButton(
                                                onClick = { onRemoveExpense(expense) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = ParchmentCream.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Total Fixed Summary
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PolishedOak)
                                    .border(1.dp, RichOakBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Monthly Bills:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = currency.format(totalFixedVal, decimals = false) + " / mo",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Next Button
        GoldButton(
            text = "NEXT",
            onClick = onNext,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("next_button_step_2")
        )
    }
}

// -------------------------------------------------------------------------------------------------
// SCREEN 3: TOTAL DEBT
// -------------------------------------------------------------------------------------------------
@Composable
private fun ScreenThreeTotalDebt(
    currency: VaultCurrency,
    debtText: String,
    onDebtChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        InfoExplanationDialog(
            title = "Total Debt",
            explanation = "Understanding your debt helps us create a plan to pay it down faster. This includes consumer loans, credit cards, auto financing, and student debt.",
            formula = "Total Debt = Sum of all balances owed to lenders",
            onDismiss = { showInfoDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_q3_card"),
                    goldAccentBorder = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
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
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "What is your total debt?",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 17.5.sp
                                    )
                                )
                            }

                            InfoTooltipButton(
                                onClick = { showInfoDialog = true },
                                contentDescription = "How debt is used"
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Understanding your debt helps us create a plan to pay it down faster.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = debtText,
                            onValueChange = onDebtChange,
                            prefix = {
                                Text("${currency.symbol} ", color = SovereignGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            },
                            placeholder = {
                                Text("0", color = MutedCream.copy(alpha = 0.5f), fontSize = 18.sp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("debt_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DeepOakBrown,
                                unfocusedContainerColor = DeepOakBrown,
                                focusedTextColor = ParchmentCream,
                                unfocusedTextColor = ParchmentCream,
                                focusedIndicatorColor = SovereignGold,
                                unfocusedIndicatorColor = RichOakBorder
                            ),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    onNext()
                                }
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick debt presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("0 (Debt Free)", "1500", "4200", "12000", "25000").forEach { label ->
                                val cleanVal = if (label.contains("0")) "0" else label
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onDebtChange(cleanVal) },
                                    color = if (debtText == cleanVal) SovereignGold.copy(alpha = 0.25f) else PolishedOak,
                                    border = BorderStroke(
                                        1.dp,
                                        if (debtText == cleanVal) SovereignGold else RichOakBorder
                                    )
                                ) {
                                    Text(
                                        text = if (label.startsWith("0")) "Debt Free (${currency.symbol}0)" else "${currency.symbol}$label",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = if (debtText == cleanVal) SovereignGold else ParchmentCream,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Next Button
        GoldButton(
            text = "NEXT",
            onClick = onNext,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("next_button_step_3")
        )
    }
}

// -------------------------------------------------------------------------------------------------
// SCREEN 4: SAVINGS & INVESTMENTS BALANCE
// -------------------------------------------------------------------------------------------------
@Composable
private fun ScreenFourSavingsBalance(
    currency: VaultCurrency,
    savingsText: String,
    onSavingsChange: (String) -> Unit,
    investmentsText: String,
    onInvestmentsChange: (String) -> Unit,
    onShowReality: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showInfoDialog by remember { mutableStateOf(false) }

    val savVal = savingsText.toDoubleOrNull() ?: 0.0
    val invVal = investmentsText.toDoubleOrNull() ?: 0.0
    val totalCushion = savVal + invVal

    if (showInfoDialog) {
        InfoExplanationDialog(
            title = "Savings & Investment Assets",
            explanation = "Your savings provide liquid cash for unexpected emergencies and daily safety. Your investments (401k, Roth IRA, index funds, stocks, crypto) compound over decades to build true financial independence.",
            formula = "Total Capital Cushion = Liquid Cash Reserves + Investment Assets",
            onDismiss = { showInfoDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                WoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_q4_card"),
                    goldAccentBorder = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
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
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "What is your savings balance?",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 17.5.sp
                                    )
                                )
                            }

                            InfoTooltipButton(
                                onClick = { showInfoDialog = true },
                                contentDescription = "How savings balance is used"
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Liquid cash in checking, savings, or HYSA available right now for emergencies.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = ParchmentCream,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = savingsText,
                            onValueChange = onSavingsChange,
                            prefix = {
                                Text("${currency.symbol} ", color = SovereignGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            },
                            placeholder = {
                                Text("5000", color = MutedCream.copy(alpha = 0.5f), fontSize = 18.sp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("savings_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DeepOakBrown,
                                unfocusedContainerColor = DeepOakBrown,
                                focusedTextColor = ParchmentCream,
                                unfocusedTextColor = ParchmentCream,
                                focusedIndicatorColor = SovereignGold,
                                unfocusedIndicatorColor = RichOakBorder
                            ),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick savings presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("1000", "3000", "5000", "10000", "25000").forEach { preset ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onSavingsChange(preset) },
                                    color = if (savingsText == preset) SovereignGold.copy(alpha = 0.25f) else PolishedOak,
                                    border = BorderStroke(
                                        1.dp,
                                        if (savingsText == preset) SovereignGold else RichOakBorder
                                    )
                                ) {
                                    Text(
                                        text = "${currency.symbol}$preset",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = if (savingsText == preset) SovereignGold else ParchmentCream,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        ThinGoldDivider(alpha = 0.35f)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Investments Section
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
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
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = SovereignGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Investments & Long-Term Wealth",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "401(k), Roth IRA, stocks, index funds, crypto, or real estate equity",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        color = ParchmentCream.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = investmentsText,
                            onValueChange = onInvestmentsChange,
                            prefix = {
                                Text("${currency.symbol} ", color = SovereignGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            },
                            placeholder = {
                                Text("0", color = MutedCream.copy(alpha = 0.5f), fontSize = 18.sp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("investments_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DeepOakBrown,
                                unfocusedContainerColor = DeepOakBrown,
                                focusedTextColor = ParchmentCream,
                                unfocusedTextColor = ParchmentCream,
                                focusedIndicatorColor = SovereignGold,
                                unfocusedIndicatorColor = RichOakBorder
                            ),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    onShowReality()
                                }
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick investment presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("0", "1500", "5000", "15000", "50000").forEach { preset ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onInvestmentsChange(preset) },
                                    color = if (investmentsText == preset) SovereignGold.copy(alpha = 0.25f) else PolishedOak,
                                    border = BorderStroke(
                                        1.dp,
                                        if (investmentsText == preset) SovereignGold else RichOakBorder
                                    )
                                ) {
                                    Text(
                                        text = if (preset == "0") "None yet" else "${currency.symbol}$preset",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = if (investmentsText == preset) SovereignGold else ParchmentCream,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Live Total Cushion Callout
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = DeepOakBrown,
                            border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "TOTAL CAPITAL CUSHION",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            letterSpacing = 1.1.sp,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                    Text(
                                        text = "Liquid Reserves + Investments",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = ParchmentCream.copy(alpha = 0.7f),
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                                Text(
                                    text = currency.format(totalCushion, decimals = false),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        fontSize = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Primary Button: SHOW ME MY REALITY
        GoldButton(
            text = "SHOW ME MY REALITY",
            onClick = onShowReality,
            leadingIcon = Icons.Default.Paid,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("show_my_reality_button")
        )
    }
}

// -------------------------------------------------------------------------------------------------
// SCREEN 5: REALITY REVEAL & RESULTS (YOUR FINANCIAL REALITY)
// -------------------------------------------------------------------------------------------------
@Composable
private fun ScreenFiveRealityReveal(
    currency: VaultCurrency,
    dailySafeSpend: Double,
    totalFixedVal: Double,
    incomeVal: Double,
    hasFixedIncome: Boolean,
    checkingVal: Double,
    savingsVal: Double,
    investmentsVal: Double = 0.0,
    debtVal: Double,
    discretionaryPool: Double,
    savingsAllocationMonthly: Double,
    fixedExpensesCount: Int,
    fixedExpensesList: List<FixedExpenseItem>,
    onComplete: () -> Unit
) {
    var inspectedSlip by remember { mutableStateOf<SlipType?>(null) }
    var activeExplanation by remember { mutableStateOf<Pair<String, String>?>(null) }
    var activeFormula by remember { mutableStateOf<String?>(null) }

    if (activeExplanation != null) {
        InfoExplanationDialog(
            title = activeExplanation!!.first,
            explanation = activeExplanation!!.second,
            formula = activeFormula,
            onDismiss = {
                activeExplanation = null
                activeFormula = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WoodCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("my_reality_reveal_card"),
                goldAccentBorder = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your Financial Reality",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = SovereignGold,
                            letterSpacing = 1.2.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A customer-centric look at your daily cash, monthly bills, and savings",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = ParchmentCream
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Large Daily Safe Spend Hero Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = DeepOakBrown,
                        border = BorderStroke(1.5.dp, SovereignGold)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "DAILY SAFE SPEND",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold,
                                        letterSpacing = 1.5.sp
                                    )
                                )
                                InfoTooltipButton(
                                    onClick = {
                                        activeExplanation = Pair(
                                            "Daily Safe Spend",
                                            "Your Daily Safe Spend is what you can spend each day completely guilt-free after accounting for all your fixed monthly expenses and your 30% savings target."
                                        )
                                        activeFormula = if (hasFixedIncome && incomeVal > 0) {
                                            "(${currency.format(incomeVal, false)} Income - ${currency.format(totalFixedVal, false)} Bills - ${currency.format(savingsAllocationMonthly, false)} Savings) ÷ 30 Days = ${currency.format(dailySafeSpend, true)}/day"
                                        } else {
                                            "(${currency.format(checkingVal, false)} Checking - ${currency.format(totalFixedVal, false)} Bills - ${currency.format(savingsAllocationMonthly, false)} Savings) ÷ 30 Days = ${currency.format(dailySafeSpend, true)}/day"
                                        }
                                    },
                                    contentDescription = "Daily safe spend explanation"
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currency.format(dailySafeSpend, decimals = true) + " / DAY",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold,
                                    fontSize = 30.sp
                                ),
                                modifier = Modifier.testTag("daily_safe_spend_value")
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Basis Note Badge
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PolishedOak)
                                    .border(1.dp, SovereignGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (hasFixedIncome && incomeVal > 0) "Based on your monthly income" else "Based on your current balance",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SovereignGold,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (hasFixedIncome && incomeVal > 0) {
                                    "What you can spend each day after paying ${currency.format(totalFixedVal, decimals = false)} in bills and setting aside ${currency.format(savingsAllocationMonthly, decimals = false)} (30%) in savings."
                                } else {
                                    "What you can spend each day based on your baseline checking balance of ${currency.format(checkingVal, decimals = false)} after bills and 30% savings."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = ParchmentCream,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ---------------------------------------------------------------------------------
                    // RESULTS SCREEN: TRANSPARENT MATH BREAKDOWN
                    // ---------------------------------------------------------------------------------
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("math_breakdown_card"),
                        shape = RoundedCornerShape(10.dp),
                        color = DeepOakBrown,
                        border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.7f))
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
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = SovereignGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "THE MATH BEHIND YOUR NUMBER",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            letterSpacing = 1.1.sp,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                InfoTooltipButton(
                                    onClick = {
                                        activeExplanation = Pair(
                                            "How Calculations Work",
                                            "We believe in complete transparency. Your Daily Safe Spend is calculated by subtracting fixed bills and a 30% savings target from your baseline income, then dividing the remaining amount across 30 days."
                                        )
                                        activeFormula = "Daily Safe Spend = (Income - Bills - 30% Savings) ÷ 30"
                                    },
                                    contentDescription = "Calculation explanation"
                                )
                            }

                            ThinGoldDivider(alpha = 0.25f)

                            // Row 1: Monthly Income or Checking Balance
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (hasFixedIncome && incomeVal > 0) "Monthly Income:" else "Checking Baseline:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = ParchmentCream,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    InfoTooltipButton(
                                        onClick = {
                                            if (hasFixedIncome && incomeVal > 0) {
                                                activeExplanation = Pair(
                                                    "Monthly Income",
                                                    "Your take-home monthly earnings after taxes that fund your lifestyle, bills, and savings."
                                                )
                                                activeFormula = "Total Monthly Income = ${currency.format(incomeVal, false)}"
                                            } else {
                                                activeExplanation = Pair(
                                                    "Checking Baseline",
                                                    "Your available checking balance used as the baseline pool for budget calculations."
                                                )
                                                activeFormula = "Checking Balance = ${currency.format(checkingVal, false)}"
                                            }
                                        },
                                        contentDescription = "Income tooltip"
                                    )
                                }
                                Text(
                                    text = currency.format(if (hasFixedIncome && incomeVal > 0) incomeVal else checkingVal, decimals = false),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold
                                    )
                                )
                            }

                            // Row 2: Fixed Expenses
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Fixed Expenses:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = ParchmentCream,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    InfoTooltipButton(
                                        onClick = {
                                            activeExplanation = Pair(
                                                "Fixed Expenses",
                                                "Mandatory recurring commitments like rent, auto loans, insurance, and utilities that must be paid every single month."
                                            )
                                            activeFormula = "Fixed Expenses = -${currency.format(totalFixedVal, false)}"
                                        },
                                        contentDescription = "Fixed expenses tooltip"
                                    )
                                }
                                Text(
                                    text = "-${currency.format(totalFixedVal, decimals = false)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF9A9A)
                                    )
                                )
                            }

                            // Row 3: Savings Target (30%)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Savings Target (30%):",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = ParchmentCream,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    InfoTooltipButton(
                                        onClick = {
                                            activeExplanation = Pair(
                                                "Savings Target (30%)",
                                                "Setting aside 30% of your earnings builds your emergency cushion, accelerates debt payoff, and funds long-term wealth."
                                            )
                                            activeFormula = "30% Allocation = -${currency.format(savingsAllocationMonthly, false)}"
                                        },
                                        contentDescription = "Savings target tooltip"
                                    )
                                }
                                Text(
                                    text = "-${currency.format(savingsAllocationMonthly, decimals = false)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFCC80)
                                    )
                                )
                            }

                            ThinGoldDivider(alpha = 0.2f)

                            // Row 4: Remaining for Spending
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Remaining for Spending:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.SansSerif,
                                            color = ParchmentCream,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    InfoTooltipButton(
                                        onClick = {
                                            activeExplanation = Pair(
                                                "Remaining for Spending",
                                                "The total discretionary money left for flexible spending during the month, divided evenly over 30 days."
                                            )
                                            activeFormula = "${currency.format(discretionaryPool, false)} ÷ 30 Days"
                                        },
                                        contentDescription = "Remaining pool tooltip"
                                    )
                                }
                                Text(
                                    text = "${currency.format(discretionaryPool, decimals = false)} ÷ 30 days",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = LustrousGold
                                    )
                                )
                            }

                            // Row 5: Your Daily Safe Spend result
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                color = PolishedOak,
                                border = BorderStroke(1.dp, SovereignGold)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Your Daily Safe Spend:",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    Text(
                                        text = currency.format(dailySafeSpend, decimals = true),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignGold,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    ThinGoldDivider(alpha = 0.35f)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "YOUR 3 MONEY SLIPS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 12.sp,
                            color = SovereignGold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Slip 1: Expenses Slip
                    BankSlipCard(
                        slipType = SlipType.EXPENSES,
                        amount = totalFixedVal,
                        monthlyTarget = (incomeVal * 0.50).coerceAtLeast(totalFixedVal),
                        entryCount = fixedExpensesCount,
                        currency = currency,
                        onClick = { inspectedSlip = SlipType.EXPENSES }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Slip 2: Savings Slip
                    BankSlipCard(
                        slipType = SlipType.SAVINGS,
                        amount = savingsVal,
                        monthlyTarget = (totalFixedVal * 6.0).coerceAtLeast(20000.0),
                        entryCount = 1,
                        currency = currency,
                        onClick = { inspectedSlip = SlipType.SAVINGS }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Slip 3: Wants Slip
                    BankSlipCard(
                        slipType = SlipType.WANTS,
                        amount = discretionaryPool,
                        monthlyTarget = (incomeVal * 0.20).coerceAtLeast(discretionaryPool),
                        entryCount = 1,
                        currency = currency,
                        onClick = { inspectedSlip = SlipType.WANTS }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Total Net Worth & Emergency Fund Coverage Stats Box
                    val totalNetWorth = savingsVal + investmentsVal - debtVal
                    val runwayMonths = if (totalFixedVal > 0) savingsVal / totalFixedVal else 0.0

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = DeepOakBrown,
                        border = BorderStroke(1.dp, RichOakBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Row 1: Total Net Worth
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Total Net Worth",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            text = if (investmentsVal > 0) "Cash + investments minus total debt" else "Savings minus total debt",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream.copy(alpha = 0.7f),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    InfoTooltipButton(
                                        onClick = {
                                            activeExplanation = Pair(
                                                "Total Net Worth",
                                                "In plain English: What you own minus what you owe. Your liquid savings and investments combined, minus your total debt liabilities."
                                            )
                                            activeFormula = if (investmentsVal > 0) {
                                                "(${currency.format(savingsVal, false)} Savings + ${currency.format(investmentsVal, false)} Investments) - ${currency.format(debtVal, false)} Debt = ${currency.format(totalNetWorth, false)}"
                                            } else {
                                                "${currency.format(savingsVal, false)} Savings - ${currency.format(debtVal, false)} Debt = ${currency.format(totalNetWorth, false)}"
                                            }
                                        },
                                        contentDescription = "Net worth tooltip"
                                    )
                                }
                                Text(
                                    text = currency.format(totalNetWorth, decimals = false),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (totalNetWorth >= 0) SovereignGold else Color(0xFFE57373)
                                    )
                                )
                            }

                            ThinGoldDivider(alpha = 0.2f)

                            // Row 2: Emergency Fund Coverage
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Emergency Fund Coverage",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            text = "How long savings cover your bills",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream.copy(alpha = 0.7f),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    InfoTooltipButton(
                                        onClick = {
                                            activeExplanation = Pair(
                                                "Emergency Fund Coverage",
                                                "How many months your savings could pay all your basic living bills if your income completely stopped today."
                                            )
                                            activeFormula = "${currency.format(savingsVal, false)} Savings ÷ ${currency.format(totalFixedVal, false)} Monthly Bills = ${String.format(Locale.US, "%.1f", runwayMonths)} months"
                                        },
                                        contentDescription = "Emergency runway tooltip"
                                    )
                                }
                                Text(
                                    text = String.format(Locale.US, "%.1f months of bills", runwayMonths),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignGold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Disclaimer and terms reminder
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Advisory Note",
                            tint = SovereignGold.copy(alpha = 0.7f),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Educational self-directed budget tools. By entering The Vault, you accept the Terms of Service & Privacy Policy. Stored 100% locally on-device.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                color = MutedCream,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Final Button: ENTER THE VAULT
                    GoldButton(
                        text = "ENTER THE VAULT",
                        onClick = onComplete,
                        leadingIcon = Icons.Default.Shield,
                        trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enter_the_vault_button")
                    )
                }
            }
        }
    }

    // Slip Inspection Modal if tapped
    if (inspectedSlip != null) {
        val slip = inspectedSlip!!
        Dialog(
            onDismissRequest = { inspectedSlip = null }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = DeepOakBrown,
                border = BorderStroke(1.dp, SovereignGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (slip) {
                                SlipType.EXPENSES -> "Expenses Slip Details"
                                SlipType.SAVINGS -> "Savings Slip Details"
                                SlipType.WANTS -> "Wants Slip Details"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold
                            )
                        )
                        IconButton(
                            onClick = { inspectedSlip = null },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = ParchmentCream
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (slip) {
                        SlipType.EXPENSES -> {
                            Text(
                                text = "Your recorded fixed monthly bills:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = ParchmentCream
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                fixedExpensesList.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PolishedOak)
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.SansSerif,
                                                color = ParchmentCream
                                            )
                                        )
                                        Text(
                                            text = currency.format(item.amount, decimals = false),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SovereignGold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        SlipType.SAVINGS -> {
                            Text(
                                text = "Current savings: ${currency.format(savingsVal, decimals = false)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = ParchmentCream,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Recommended emergency cushion is 3-6 months of bills (${currency.format(totalFixedVal * 3, decimals = false)} - ${currency.format(totalFixedVal * 6, decimals = false)}).",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = ParchmentCream.copy(alpha = 0.8f)
                                )
                            )
                        }
                        SlipType.WANTS -> {
                            Text(
                                text = "Monthly fun & flexible money: ${currency.format(discretionaryPool, decimals = false)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = ParchmentCream,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Divided across 30 days gives your Daily Safe Spend of ${currency.format(dailySafeSpend, decimals = true)} per day.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    color = ParchmentCream
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GoldButton(
                        text = "CLOSE",
                        onClick = { inspectedSlip = null },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
