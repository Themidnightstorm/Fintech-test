package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.DamaskBackground
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.TierOneOnboardingScreen
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.TheVaultTheme
import com.example.ui.viewmodel.VaultViewModel
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: VaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannels(this)

        handleIntent(intent)

        setContent {
            TheVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RichBurgundy
                ) {
                    DamaskBackground {
                        val uiState by viewModel.uiState.collectAsState()

                        Crossfade(
                            targetState = uiState.isOnboardingCompleted,
                            label = "onboarding_crossfade"
                        ) { isOnboarded ->
                            if (isOnboarded) {
                                DashboardScreen(viewModel = viewModel)
                            } else {
                                TierOneOnboardingScreen(
                                    currentCurrency = uiState.currency,
                                    initialIncome = if (uiState.monthlyIncome > 0) uiState.monthlyIncome else null,
                                    initialChecking = if (uiState.checkingBalance > 0) uiState.checkingBalance else null,
                                    initialDebt = if (uiState.totalDebt > 0) uiState.totalDebt else null,
                                    initialSavings = if (uiState.savingsBalance > 0) uiState.savingsBalance else null,
                                    initialHasFixedIncome = uiState.hasFixedIncome,
                                    onSelectCurrency = { newCurrency ->
                                        viewModel.setCurrency(newCurrency)
                                    },
                                    onComplete = { income, fixedExp, debt, savings, hasFixed, checkingBal, investments, occupation, employer, incomeType ->
                                        viewModel.completeOnboarding(
                                            monthlyIncome = income,
                                            fixedExpenses = fixedExp,
                                            totalDebt = debt,
                                            savingsBalance = savings,
                                            hasFixedIncome = hasFixed,
                                            checkingBalance = checkingBal,
                                            investmentsBalance = investments,
                                            occupation = occupation,
                                            employerOrWorkplace = employer,
                                            incomeType = incomeType
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("OPEN_MONTHLY_REVIEW", false) == true) {
            viewModel.openMonthlyReview()
        }
    }
}
