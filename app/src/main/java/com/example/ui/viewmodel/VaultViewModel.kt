package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VaultDatabase
import com.example.data.local.entity.AccountabilityPartnerEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.LessonEntity
import com.example.data.local.entity.PartnerNudgeEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.SlipType
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.VaultProfileEntity
import com.example.data.model.FinancialRoadmapData
import com.example.data.model.MembershipTier
import com.example.data.model.MonthlyReviewData
import com.example.data.model.VaultCurrency
import com.example.data.repository.VaultRepository
import com.example.util.CsvTransactionParser
import com.example.util.ParsedCsvTransaction
import java.io.InputStream
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.pow

data class VaultUiState(
    val netWorth: Double = 148250.00,
    val monthlyIncome: Double = 8500.00,
    val hasFixedIncome: Boolean = true,
    val checkingBalance: Double = 0.00,
    val isPremium: Boolean = false,
    val membershipTier: MembershipTier = MembershipTier.FREE,
    val isOnboardingCompleted: Boolean = false,
    val totalDebt: Double = 0.00,
    val savingsBalance: Double = 25000.00,
    val fixedExpensesTotal: Double = 3200.00,
    val dailySafeSpend: Double = 120.00,
    val expensesTotal: Double = 2370.00,
    val savingsTotal: Double = 5700.00,
    val wantsTotal: Double = 1300.00,
    val primaryGoal: SavingsGoalEntity? = null,
    val allGoals: List<SavingsGoalEntity> = emptyList(),
    val allTransactions: List<TransactionEntity> = emptyList(),
    val lessons: List<LessonEntity> = emptyList(),
    val todayLesson: LessonEntity? = null,
    val chatMessages: List<ChatMessageEntity> = emptyList(),
    val isAiThinking: Boolean = false,
    val partner: AccountabilityPartnerEntity? = null,
    val recentNudges: List<PartnerNudgeEntity> = emptyList(),
    val currency: VaultCurrency = VaultCurrency.USD,
    val roadmapData: FinancialRoadmapData? = null,
    val monthlyReviewData: MonthlyReviewData? = null,
    val subscribedEmail: String = "",
    val investmentsBalance: Double = 0.00,
    val occupation: String = "",
    val employerOrWorkplace: String = "",
    val incomeType: String = "FIXED_SALARY"
) {
    val isPro: Boolean
        get() = membershipTier == MembershipTier.PRO || membershipTier == MembershipTier.PREMIUM

    val isFree: Boolean
        get() = membershipTier == MembershipTier.FREE
}

data class InvestingSimState(
    val initialCapital: Double = 25000.00,
    val monthlyDeposit: Double = 1500.00,
    val years: Int = 20,
    val stocksWeight: Float = 0.60f, // Expected ~10% annual
    val realEstateWeight: Float = 0.20f, // Expected ~8% annual
    val goldWeight: Float = 0.10f, // Expected ~6% annual
    val bondsWeight: Float = 0.10f // Expected ~4% annual
) {
    val weightedAnnualReturn: Double
        get() {
            val totalWeight = (stocksWeight + realEstateWeight + goldWeight + bondsWeight).toDouble()
            return if (totalWeight > 0.0) {
                ((stocksWeight * 0.10) + (realEstateWeight * 0.08) + (goldWeight * 0.06) + (bondsWeight * 0.04)) / totalWeight
            } else {
                0.07
            }
        }

    val totalDeposited: Double
        get() = initialCapital + (monthlyDeposit * 12 * years)

    val futureNetWorth: Double
        get() {
            val r = weightedAnnualReturn
            val monthlyRate = r / 12.0
            val months = years * 12

            val fvInitial = initialCapital * (1 + monthlyRate).pow(months.toDouble())
            val fvAnnuity = if (monthlyRate > 0) {
                monthlyDeposit * (((1 + monthlyRate).pow(months.toDouble()) - 1) / monthlyRate)
            } else {
                monthlyDeposit * months
            }
            return fvInitial + fvAnnuity
        }

    val compoundGain: Double
        get() = (futureNetWorth - totalDeposited).coerceAtLeast(0.0)
}

private data class VaultCoreData(
    val profile: VaultProfileEntity?,
    val txs: List<TransactionEntity>,
    val goals: List<SavingsGoalEntity>,
    val lessons: List<LessonEntity>,
    val partner: AccountabilityPartnerEntity?,
    val nudges: List<PartnerNudgeEntity>
)

class VaultViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VaultRepository

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _investingSim = MutableStateFlow(InvestingSimState())
    val investingSim: StateFlow<InvestingSimState> = _investingSim.asStateFlow()

    // Modals and Active Selections
    private val _activeSlipType = MutableStateFlow<SlipType?>(null)
    val activeSlipType: StateFlow<SlipType?> = _activeSlipType.asStateFlow()

    private val _activeLesson = MutableStateFlow<LessonEntity?>(null)
    val activeLesson: StateFlow<LessonEntity?> = _activeLesson.asStateFlow()

    private val _showTutorModal = MutableStateFlow(false)
    val showTutorModal: StateFlow<Boolean> = _showTutorModal.asStateFlow()

    private val _showUpgradeModal = MutableStateFlow(false)
    val showUpgradeModal: StateFlow<Boolean> = _showUpgradeModal.asStateFlow()

    private val _showSimulatorModal = MutableStateFlow(false)
    val showSimulatorModal: StateFlow<Boolean> = _showSimulatorModal.asStateFlow()

    private val _showAddTxModal = MutableStateFlow(false)
    val showAddTxModal: StateFlow<Boolean> = _showAddTxModal.asStateFlow()

    private val _showPartnerModal = MutableStateFlow(false)
    val showPartnerModal: StateFlow<Boolean> = _showPartnerModal.asStateFlow()

    private val _showSettingsModal = MutableStateFlow(false)
    val showSettingsModal: StateFlow<Boolean> = _showSettingsModal.asStateFlow()

    private val _showCsvImportModal = MutableStateFlow(false)
    val showCsvImportModal: StateFlow<Boolean> = _showCsvImportModal.asStateFlow()

    private val _showRoadmapModal = MutableStateFlow(false)
    val showRoadmapModal: StateFlow<Boolean> = _showRoadmapModal.asStateFlow()

    private val _showMonthlyReviewModal = MutableStateFlow(false)
    val showMonthlyReviewModal: StateFlow<Boolean> = _showMonthlyReviewModal.asStateFlow()

    private val _parsedCsvTransactions = MutableStateFlow<List<ParsedCsvTransaction>>(emptyList())
    val parsedCsvTransactions: StateFlow<List<ParsedCsvTransaction>> = _parsedCsvTransactions.asStateFlow()

    private val _csvImportError = MutableStateFlow<String?>(null)
    val csvImportError: StateFlow<String?> = _csvImportError.asStateFlow()

    private val _csvImportSuccessMessage = MutableStateFlow<String?>(null)
    val csvImportSuccessMessage: StateFlow<String?> = _csvImportSuccessMessage.asStateFlow()

    val uiState: StateFlow<VaultUiState>

    init {
        val database = VaultDatabase.getDatabase(application)
        repository = VaultRepository(database.vaultDao())

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }

        val partnerCoreFlow = combine(
            repository.vaultProfile,
            repository.allTransactions,
            repository.allSavingsGoals,
            repository.allLessons
        ) { profile, txs, goals, lessons ->
            Quadruple(profile, txs, goals, lessons)
        }

        val coreFlow = combine(
            partnerCoreFlow,
            repository.accountabilityPartner,
            repository.recentNudges
        ) { quad, partner, nudges ->
            VaultCoreData(quad.first, quad.second, quad.third, quad.fourth, partner, nudges)
        }

        uiState = combine(
            coreFlow,
            repository.chatMessages,
            _isAiThinking
        ) { core, chat, thinking ->
            val profile = core.profile
            val txs = core.txs
            val goals = core.goals
            val lessons = core.lessons
            val partner = core.partner
            val nudges = core.nudges

            val membershipTier = MembershipTier.fromId(
                profile?.membershipTier ?: if (profile?.isPremiumUnlocked == true) "PREMIUM" else "FREE"
            )
            val isPrem = membershipTier == MembershipTier.PREMIUM
            val isOnboarded = profile?.isOnboardingCompleted ?: false
            val baseNw = profile?.baseNetWorth ?: 125000.00
            val income = profile?.monthlyIncome ?: 8500.00
            val debt = profile?.totalDebt ?: 0.00
            val savBal = profile?.savingsBalance ?: 25000.00
            val fixedExp = profile?.fixedExpensesTotal ?: 3200.00
            val dailySafe = profile?.dailySafeSpend ?: 120.00

            val expTotal = txs.filter { it.slipType == SlipType.EXPENSES.name }.sumOf { it.amount }
            val savTotal = txs.filter { it.slipType == SlipType.SAVINGS.name }.sumOf { it.amount }
            val wantTotal = txs.filter { it.slipType == SlipType.WANTS.name }.sumOf { it.amount }

            val primaryGoal = goals.firstOrNull { it.isPrimary } ?: goals.firstOrNull()
            val totalGoalSavings = goals.sumOf { it.currentAmount }

            val totalAssets = if (goals.isNotEmpty()) {
                totalGoalSavings + (profile?.checkingBalance ?: 0.0)
            } else {
                savBal + (profile?.investmentsBalance ?: 0.0) + (profile?.checkingBalance ?: 0.0)
            }

            val calculatedNetWorth = if (isOnboarded) {
                totalAssets - debt
            } else {
                baseNw + totalGoalSavings - debt
            }

            val currentDayLesson = lessons.firstOrNull { !it.isCompleted } ?: lessons.firstOrNull()
            val userCurrency = VaultCurrency.fromCode(profile?.currencyCode)

            val roadmap = FinancialRoadmapData.calculate(
                netWorth = calculatedNetWorth,
                monthlyIncome = income,
                fixedExpensesTotal = fixedExp,
                totalDebt = debt,
                savingsBalance = savBal,
                primaryGoal = primaryGoal,
                allGoals = goals,
                currency = userCurrency
            )

            val monthlyReview = MonthlyReviewData.generate(
                netWorth = calculatedNetWorth,
                monthlyIncome = income,
                fixedExpensesTotal = fixedExp,
                totalDebt = debt,
                savingsBalance = savBal,
                allTransactions = txs,
                allGoals = goals,
                lessons = lessons,
                streakDays = partner?.sharedStreak ?: 4,
                dailySafeSpend = dailySafe,
                currency = userCurrency
            )

            VaultUiState(
                netWorth = calculatedNetWorth,
                monthlyIncome = income,
                hasFixedIncome = profile?.hasFixedIncome ?: true,
                checkingBalance = profile?.checkingBalance ?: 0.00,
                isPremium = isPrem,
                membershipTier = membershipTier,
                isOnboardingCompleted = isOnboarded,
                totalDebt = debt,
                savingsBalance = savBal,
                fixedExpensesTotal = fixedExp,
                dailySafeSpend = dailySafe,
                expensesTotal = expTotal,
                savingsTotal = savTotal,
                wantsTotal = wantTotal,
                primaryGoal = primaryGoal,
                allGoals = goals,
                allTransactions = txs,
                lessons = lessons,
                todayLesson = currentDayLesson,
                chatMessages = chat,
                isAiThinking = thinking,
                partner = partner,
                recentNudges = nudges,
                currency = userCurrency,
                roadmapData = roadmap,
                monthlyReviewData = monthlyReview,
                subscribedEmail = profile?.subscribedEmail.orEmpty(),
                investmentsBalance = profile?.investmentsBalance ?: 0.00,
                occupation = profile?.occupation.orEmpty(),
                employerOrWorkplace = profile?.employerOrWorkplace.orEmpty(),
                incomeType = profile?.incomeType.orEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VaultUiState()
        )
    }

    // Helper data holder for combining flows
    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    fun openSlip(slipType: SlipType) {
        _activeSlipType.value = slipType
    }

    fun closeSlip() {
        _activeSlipType.value = null
    }

    fun openLesson(lesson: LessonEntity) {
        if (lesson.isPremium && !(uiState.value.isPro)) {
            _showUpgradeModal.value = true
        } else {
            _activeLesson.value = lesson
        }
    }

    fun closeLesson() {
        _activeLesson.value = null
    }

    fun toggleLessonCompletion(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleLessonCompletion(id, completed)
        }
    }

    fun openTutor() {
        _showTutorModal.value = true
    }

    fun closeTutor() {
        _showTutorModal.value = false
    }

    fun openUpgradeModal() {
        _showUpgradeModal.value = true
    }

    fun closeUpgradeModal() {
        _showUpgradeModal.value = false
    }

    fun openRoadmap() {
        _showRoadmapModal.value = true
    }

    fun closeRoadmap() {
        _showRoadmapModal.value = false
    }

    fun openMonthlyReview() {
        _showMonthlyReviewModal.value = true
    }

    fun closeMonthlyReview() {
        _showMonthlyReviewModal.value = false
    }

    fun unlockPremierTier() {
        setMembershipTier(MembershipTier.PREMIUM)
    }

    fun unlockSovereignTier() {
        setMembershipTier(MembershipTier.PREMIUM)
    }

    fun setMembershipTier(tier: MembershipTier) {
        viewModelScope.launch {
            repository.setMembershipTier(tier.id)
            _showUpgradeModal.value = false
        }
    }

    fun subscribeEmail(email: String) {
        viewModelScope.launch {
            repository.subscribeEmail(email)
        }
    }

    fun openSimulator() {
        if (!uiState.value.isPro) {
            _showUpgradeModal.value = true
        } else {
            _showSimulatorModal.value = true
        }
    }

    fun closeSimulator() {
        _showSimulatorModal.value = false
    }

    fun openAddTransaction(defaultSlip: SlipType? = null) {
        if (defaultSlip != null) {
            _activeSlipType.value = defaultSlip
        }
        _showAddTxModal.value = true
    }

    fun closeAddTransaction() {
        _showAddTxModal.value = false
    }

    fun addTransaction(
        title: String,
        amount: Double,
        slipType: SlipType,
        category: String,
        note: String
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                title = title.ifBlank { "Vault Ledger Entry" },
                amount = amount,
                slipType = slipType,
                category = category.ifBlank { "General" },
                note = note
            )
            _showAddTxModal.value = false
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun depositGoldToGoal(goalId: Long, amount: Double) {
        viewModelScope.launch {
            repository.addFundsToGoal(goalId, amount)
        }
    }

    fun sendTutorMessage(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isAiThinking.value = true
            try {
                repository.sendChatMessage(prompt)
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun updateSimulator(
        initialCapital: Double? = null,
        monthlyDeposit: Double? = null,
        years: Int? = null,
        stocksWeight: Float? = null,
        realEstateWeight: Float? = null,
        goldWeight: Float? = null,
        bondsWeight: Float? = null
    ) {
        _investingSim.value = _investingSim.value.copy(
            initialCapital = initialCapital ?: _investingSim.value.initialCapital,
            monthlyDeposit = monthlyDeposit ?: _investingSim.value.monthlyDeposit,
            years = years ?: _investingSim.value.years,
            stocksWeight = stocksWeight ?: _investingSim.value.stocksWeight,
            realEstateWeight = realEstateWeight ?: _investingSim.value.realEstateWeight,
            goldWeight = goldWeight ?: _investingSim.value.goldWeight,
            bondsWeight = bondsWeight ?: _investingSim.value.bondsWeight
        )
    }

    fun completeOnboarding(
        monthlyIncome: Double,
        fixedExpenses: List<Pair<String, Double>>,
        totalDebt: Double,
        savingsBalance: Double,
        hasFixedIncome: Boolean = true,
        checkingBalance: Double = 0.0,
        investmentsBalance: Double = 0.0,
        occupation: String = "",
        employerOrWorkplace: String = "",
        incomeType: String = "FIXED_SALARY"
    ) {
        viewModelScope.launch {
            repository.completeOnboarding(
                monthlyIncome = monthlyIncome,
                fixedExpenses = fixedExpenses,
                totalDebt = totalDebt,
                savingsBalance = savingsBalance,
                hasFixedIncome = hasFixedIncome,
                checkingBalance = checkingBalance,
                investmentsBalance = investmentsBalance,
                occupation = occupation,
                employerOrWorkplace = employerOrWorkplace,
                incomeType = incomeType
            )
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            repository.resetOnboarding()
        }
    }

    fun openSettings() {
        _showSettingsModal.value = true
    }

    fun closeSettings() {
        _showSettingsModal.value = false
    }

    fun setCurrency(currency: VaultCurrency) {
        viewModelScope.launch {
            repository.setCurrency(currency)
        }
    }

    // Accountability Partner Functions
    fun openPartnerModal() {
        _showPartnerModal.value = true
    }

    fun closePartnerModal() {
        _showPartnerModal.value = false
    }

    fun invitePartner(email: String, name: String = "") {
        viewModelScope.launch {
            repository.inviteAndLinkPartner(email, name)
        }
    }

    fun completeDailyCheckIn() {
        viewModelScope.launch {
            repository.completeUserCheckIn(uiState.value.dailySafeSpend)
        }
    }

    fun togglePartnerCheckIn() {
        viewModelScope.launch {
            repository.togglePartnerCheckIn()
        }
    }

    fun sendNudge(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            repository.sendNudge(message)
            delay(1200)
            val partnerName = uiState.value.partner?.partnerName?.ifBlank { "Alexandra" } ?: "Alexandra"
            val responses = listOf(
                "Got your nudge! Just reviewed my daily ledger slips. Streak safe!",
                "Thanks for the accountability! Kept to my daily safe spend today.",
                "Appreciate the reminder! Stayed right within my 20% Wants allowance today.",
                "Nudge received! Let's keep our consecutive streak going strong!"
            )
            repository.simulatePartnerResponse(responses.random())
        }
    }

    fun unlinkPartner() {
        viewModelScope.launch {
            repository.unlinkPartner()
        }
    }

    fun resetStreak() {
        viewModelScope.launch {
            repository.resetStreak()
        }
    }

    // CSV Import Functions
    fun openCsvImport() {
        _csvImportError.value = null
        _csvImportSuccessMessage.value = null
        _showCsvImportModal.value = true
    }

    fun closeCsvImport() {
        _showCsvImportModal.value = false
    }

    fun loadCsvFromStream(inputStream: InputStream) {
        viewModelScope.launch {
            try {
                val parsed = CsvTransactionParser.parseCsvStream(inputStream)
                if (parsed.isEmpty()) {
                    _csvImportError.value = "No valid transactions found in the selected CSV file."
                } else {
                    _csvImportError.value = null
                    _parsedCsvTransactions.value = parsed
                    _showCsvImportModal.value = true
                }
            } catch (e: Exception) {
                _csvImportError.value = "Failed to parse CSV: ${e.localizedMessage ?: "Invalid format"}"
            }
        }
    }

    fun loadCsvFromString(csvContent: String) {
        viewModelScope.launch {
            try {
                val parsed = CsvTransactionParser.parseCsvContent(csvContent)
                if (parsed.isEmpty()) {
                    _csvImportError.value = "No valid transactions found in the CSV content."
                } else {
                    _csvImportError.value = null
                    _parsedCsvTransactions.value = parsed
                    _showCsvImportModal.value = true
                }
            } catch (e: Exception) {
                _csvImportError.value = "Failed to parse CSV: ${e.localizedMessage ?: "Invalid format"}"
            }
        }
    }

    fun loadSampleCsv() {
        loadCsvFromString(CsvTransactionParser.getSampleCsvContent())
    }

    fun updateParsedTransactionSlip(id: String, newSlip: SlipType) {
        _parsedCsvTransactions.value = _parsedCsvTransactions.value.map { item ->
            if (item.id == id) {
                item.copy(slipType = newSlip)
            } else {
                item
            }
        }
    }

    fun toggleParsedTransactionInclusion(id: String) {
        _parsedCsvTransactions.value = _parsedCsvTransactions.value.map { item ->
            if (item.id == id) {
                item.copy(isIncluded = !item.isIncluded)
            } else {
                item
            }
        }
    }

    fun toggleAllParsedTransactions(include: Boolean) {
        _parsedCsvTransactions.value = _parsedCsvTransactions.value.map { item ->
            item.copy(isIncluded = include)
        }
    }

    fun commitCsvImport() {
        val selected = _parsedCsvTransactions.value.filter { it.isIncluded }
        if (selected.isEmpty()) return

        viewModelScope.launch {
            val entities = selected.map { parsed ->
                TransactionEntity(
                    title = parsed.description,
                    amount = parsed.amount,
                    slipType = parsed.slipType.name,
                    category = parsed.category,
                    timestamp = parsed.timestamp,
                    note = "Imported from CSV (${parsed.rawDate})"
                )
            }

            repository.importTransactions(entities)
            _csvImportSuccessMessage.value = "Successfully imported ${selected.size} transactions to Vault Ledger!"
            _parsedCsvTransactions.value = emptyList()
            delay(500)
            _showCsvImportModal.value = false
        }
    }

    fun clearCsvImportMessages() {
        _csvImportError.value = null
        _csvImportSuccessMessage.value = null
    }
}
