package com.example.data.repository

import com.example.data.local.dao.VaultDao
import com.example.data.local.entity.AccountabilityPartnerEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.LessonEntity
import com.example.data.local.entity.PartnerNudgeEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.SlipType
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.VaultProfileEntity
import com.example.data.model.VaultCurrency
import com.example.data.remote.CuratorFinancialContext
import com.example.data.remote.GeminiTutorService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class VaultRepository(
    private val vaultDao: VaultDao,
    private val geminiService: GeminiTutorService = GeminiTutorService()
) {
    val allTransactions: Flow<List<TransactionEntity>> = vaultDao.getAllTransactions()
    val allSavingsGoals: Flow<List<SavingsGoalEntity>> = vaultDao.getAllSavingsGoals()
    val allLessons: Flow<List<LessonEntity>> = vaultDao.getAllLessons()
    val vaultProfile: Flow<VaultProfileEntity?> = vaultDao.getProfile()
    val chatMessages: Flow<List<ChatMessageEntity>> = vaultDao.getAllChatMessages()
    val accountabilityPartner: Flow<AccountabilityPartnerEntity?> = vaultDao.getPartner()
    val recentNudges: Flow<List<PartnerNudgeEntity>> = vaultDao.getRecentNudges()

    suspend fun initializeDefaultDataIfEmpty() {
        val currentProfile = vaultDao.getProfile().firstOrNull()
        if (currentProfile == null) {
            vaultDao.insertOrUpdateProfile(
                VaultProfileEntity(
                    id = 1,
                    isPremiumUnlocked = false,
                    isOnboardingCompleted = false,
                    baseNetWorth = 148250.00,
                    monthlyIncome = 8500.00,
                    totalDebt = 0.00,
                    savingsBalance = 25000.00,
                    fixedExpensesTotal = 3200.00,
                    dailySafeSpend = 120.00,
                    sovereignTitle = "Master of the Vault"
                )
            )
        }

        val currentPartner = vaultDao.getPartner().firstOrNull()
        if (currentPartner == null) {
            vaultDao.insertOrUpdatePartner(
                AccountabilityPartnerEntity(
                    id = 1,
                    isLinked = true,
                    partnerEmail = "alexandra.sterling@vaultcapital.org",
                    partnerName = "Alexandra Sterling",
                    partnerTitle = "Accountability Partner",
                    sharedStreak = 7,
                    userCheckedInToday = false,
                    partnerCheckedInToday = true,
                    userDailySafeSpend = 120.00,
                    partnerDailySafeSpend = 115.00,
                    userSpentToday = 42.50,
                    partnerSpentToday = 38.00,
                    lastCheckInDate = "Today",
                    lastNudgeReceivedMessage = "Don't forget to log your daily safe spend before midnight!",
                    lastNudgeReceivedTimestamp = System.currentTimeMillis() - 3600000 * 2,
                    totalNudgesSent = 3
                )
            )

            // Seed initial nudges
            val initialNudges = listOf(
                PartnerNudgeEntity(
                    isFromUser = false,
                    senderName = "Alexandra",
                    message = "Don't forget to log your daily safe spend before midnight!",
                    timestamp = System.currentTimeMillis() - 3600000 * 2
                ),
                PartnerNudgeEntity(
                    isFromUser = true,
                    senderName = "You",
                    message = "Logged my 50/30/20 slips! Staying disciplined today.",
                    timestamp = System.currentTimeMillis() - 3600000 * 22
                ),
                PartnerNudgeEntity(
                    isFromUser = false,
                    senderName = "Alexandra",
                    message = "Our 7-day streak is looking solid! Kept my spending under $40 today.",
                    timestamp = System.currentTimeMillis() - 3600000 * 26
                )
            )
            for (nudge in initialNudges) {
                vaultDao.insertNudge(nudge)
            }
        }

        val currentLessons = vaultDao.getAllLessons().firstOrNull()
        val initialLessons = generateCurriculum()
        if (currentLessons.isNullOrEmpty()) {
            vaultDao.insertLessons(initialLessons)
        } else {
            val updatedLessons = initialLessons.map { newLesson ->
                val existing = currentLessons.firstOrNull { it.id == newLesson.id }
                if (existing != null) newLesson.copy(isCompleted = existing.isCompleted) else newLesson
            }
            vaultDao.insertLessons(updatedLessons)
        }

        val currentGoals = vaultDao.getAllSavingsGoals().firstOrNull()
        if (currentGoals.isNullOrEmpty()) {
            vaultDao.insertSavingsGoal(
                SavingsGoalEntity(
                    id = 1,
                    title = "Total Savings & Reserves",
                    targetAmount = 50000.00,
                    currentAmount = 32800.00,
                    targetDateDescription = "Emergency & Opportunity Reserve",
                    isPrimary = true
                )
            )
            vaultDao.insertSavingsGoal(
                SavingsGoalEntity(
                    id = 2,
                    title = "Prime Real Estate Acquisition",
                    targetAmount = 150000.00,
                    currentAmount = 45000.00,
                    targetDateDescription = "Commercial Land Vault",
                    isPrimary = false
                )
            )
        }

        val currentTransactions = vaultDao.getAllTransactions().firstOrNull()
        if (currentTransactions.isNullOrEmpty()) {
            val defaultTxs = listOf(
                TransactionEntity(
                    title = "Private Equities Allocation",
                    amount = 3200.00,
                    slipType = SlipType.SAVINGS.name,
                    category = "Capital Asset",
                    timestamp = System.currentTimeMillis() - 86400000L * 1,
                    note = "Treasury dividend reinvestment"
                ),
                TransactionEntity(
                    title = "Estate Operations & Utilities",
                    amount = 1450.00,
                    slipType = SlipType.EXPENSES.name,
                    category = "Fixed Overhead",
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    note = "Monthly primary residency upkeep"
                ),
                TransactionEntity(
                    title = "Vintage Horology Acquisition",
                    amount = 850.00,
                    slipType = SlipType.WANTS.name,
                    category = "Discretionary Luxury",
                    timestamp = System.currentTimeMillis() - 86400000L * 3,
                    note = "Reward for quarterly discipline"
                ),
                TransactionEntity(
                    title = "Gold Bullion Deposit",
                    amount = 2500.00,
                    slipType = SlipType.SAVINGS.name,
                    category = "Hard Assets",
                    timestamp = System.currentTimeMillis() - 86400000L * 4,
                    note = "Allocated to physical vault"
                ),
                TransactionEntity(
                    title = "Executive Advisory Retainer",
                    amount = 920.00,
                    slipType = SlipType.EXPENSES.name,
                    category = "Professional Services",
                    timestamp = System.currentTimeMillis() - 86400000L * 5,
                    note = "Tax & trust counsel"
                ),
                TransactionEntity(
                    title = "Private Club Dues",
                    amount = 450.00,
                    slipType = SlipType.WANTS.name,
                    category = "Social Network",
                    timestamp = System.currentTimeMillis() - 86400000L * 6,
                    note = "High-class networking"
                )
            )
            for (tx in defaultTxs) {
                vaultDao.insertTransaction(tx)
            }
        }
    }

    suspend fun addTransaction(
        title: String,
        amount: Double,
        slipType: SlipType,
        category: String,
        note: String
    ) {
        val tx = TransactionEntity(
            title = title,
            amount = amount,
            slipType = slipType.name,
            category = category,
            timestamp = System.currentTimeMillis(),
            note = note
        )
        vaultDao.insertTransaction(tx)

        // If it's a saving, automatically allocate into primary goal
        if (slipType == SlipType.SAVINGS) {
            val primaryGoal = vaultDao.getAllSavingsGoals().firstOrNull()?.firstOrNull { it.isPrimary }
            if (primaryGoal != null) {
                vaultDao.addFundsToGoal(primaryGoal.id, amount)
            }
        }
    }

    suspend fun importTransactions(transactions: List<TransactionEntity>) {
        if (transactions.isEmpty()) return
        vaultDao.insertTransactions(transactions)

        // If any are savings, allocate into primary goal
        val savingsSum = transactions.filter { it.slipType == SlipType.SAVINGS.name }.sumOf { it.amount }
        if (savingsSum > 0) {
            val primaryGoal = vaultDao.getAllSavingsGoals().firstOrNull()?.firstOrNull { it.isPrimary }
            if (primaryGoal != null) {
                vaultDao.addFundsToGoal(primaryGoal.id, savingsSum)
            }
        }
    }

    suspend fun deleteTransaction(id: Long) = vaultDao.deleteTransaction(id)

    suspend fun addFundsToGoal(goalId: Long, amount: Double) {
        vaultDao.addFundsToGoal(goalId, amount)
        vaultDao.insertTransaction(
            TransactionEntity(
                title = "Direct Vault Deposit",
                amount = amount,
                slipType = SlipType.SAVINGS.name,
                category = "Fortress Allocation",
                timestamp = System.currentTimeMillis(),
                note = "Manual gold deposit"
            )
        )
    }

    suspend fun toggleLessonCompletion(id: Long, isCompleted: Boolean) {
        vaultDao.updateLessonCompletion(id, isCompleted)
    }

    suspend fun setPremiumUnlocked(unlocked: Boolean) {
        vaultDao.setPremiumStatus(unlocked)
        if (unlocked) {
            vaultDao.updateMembershipTier("PREMIUM")
        } else {
            vaultDao.updateMembershipTier("FREE")
        }
    }

    suspend fun setMembershipTier(tier: String) {
        vaultDao.updateMembershipTier(tier)
    }

    suspend fun subscribeEmail(email: String) {
        vaultDao.updateSubscribedEmail(email.trim())
    }

    suspend fun setCurrency(newCurrency: VaultCurrency, convertExistingData: Boolean = true) {
        val currentProfile = vaultDao.getProfile().firstOrNull()
        val oldCurrency = VaultCurrency.fromCode(currentProfile?.currencyCode)
        if (oldCurrency == newCurrency) return

        if (convertExistingData && currentProfile != null) {
            val updatedProfile = currentProfile.copy(
                currencyCode = newCurrency.code,
                monthlyIncome = oldCurrency.convertTo(currentProfile.monthlyIncome, newCurrency),
                totalDebt = oldCurrency.convertTo(currentProfile.totalDebt, newCurrency),
                savingsBalance = oldCurrency.convertTo(currentProfile.savingsBalance, newCurrency),
                fixedExpensesTotal = oldCurrency.convertTo(currentProfile.fixedExpensesTotal, newCurrency),
                dailySafeSpend = oldCurrency.convertTo(currentProfile.dailySafeSpend, newCurrency),
                baseNetWorth = oldCurrency.convertTo(currentProfile.baseNetWorth, newCurrency)
            )
            vaultDao.insertOrUpdateProfile(updatedProfile)

            // Convert goals
            val goals = vaultDao.getSavingsGoalList()
            for (goal in goals) {
                vaultDao.updateSavingsGoal(
                    goal.copy(
                        targetAmount = oldCurrency.convertTo(goal.targetAmount, newCurrency),
                        currentAmount = oldCurrency.convertTo(goal.currentAmount, newCurrency)
                    )
                )
            }

            // Convert transactions
            val txs = vaultDao.getTransactionList()
            for (tx in txs) {
                vaultDao.insertTransaction(
                    tx.copy(
                        amount = oldCurrency.convertTo(tx.amount, newCurrency)
                    )
                )
            }
        } else {
            vaultDao.updateCurrency(newCurrency.code)
        }
    }

    suspend fun completeOnboarding(
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
        val fixedTotal = fixedExpenses.sumOf { it.second }
        val savingsTargetMonthly = if (hasFixedIncome && monthlyIncome > 0) {
            (monthlyIncome * 0.30).coerceAtLeast(0.0)
        } else {
            (checkingBalance * 0.30).coerceAtLeast(0.0)
        }
        val discretionaryPool = if (hasFixedIncome && monthlyIncome > 0) {
            (monthlyIncome - fixedTotal - savingsTargetMonthly).coerceAtLeast(0.0)
        } else {
            (checkingBalance - fixedTotal - savingsTargetMonthly).coerceAtLeast(0.0)
        }
        val dailySafe = if (hasFixedIncome && monthlyIncome > 0) {
            discretionaryPool / 30.0
        } else {
            if (discretionaryPool > 0) discretionaryPool / 30.0 else (checkingBalance / 30.0)
        }
        val baseNet = (savingsBalance + investmentsBalance + checkingBalance) - totalDebt

        val currentProfile = vaultDao.getProfile().firstOrNull()
        val updatedProfile = (currentProfile ?: VaultProfileEntity()).copy(
            isOnboardingCompleted = true,
            monthlyIncome = if (hasFixedIncome) monthlyIncome else 0.0,
            hasFixedIncome = hasFixedIncome,
            checkingBalance = checkingBalance,
            totalDebt = totalDebt,
            savingsBalance = savingsBalance,
            investmentsBalance = investmentsBalance,
            fixedExpensesTotal = fixedTotal,
            dailySafeSpend = dailySafe,
            baseNetWorth = baseNet,
            occupation = occupation,
            employerOrWorkplace = employerOrWorkplace,
            incomeType = incomeType
        )
        vaultDao.insertOrUpdateProfile(updatedProfile)

        // Clear mock sample transactions so user starts with their calibrated expenses
        vaultDao.clearAllTransactions()

        // Insert fixed expenses into Expenses slip
        for ((name, amount) in fixedExpenses) {
            vaultDao.insertTransaction(
                TransactionEntity(
                    title = name,
                    amount = amount,
                    slipType = SlipType.EXPENSES.name,
                    category = "Fixed Overhead",
                    timestamp = System.currentTimeMillis(),
                    note = "Calibrated Fixed Expense"
                )
            )
        }

        // Proportional emergency fund target calculation (not arbitrarily fixed at 10,000)
        val calculatedEmergencyTarget = when {
            fixedTotal > 0 -> fixedTotal * 6.0
            monthlyIncome > 0 -> monthlyIncome * 3.0
            savingsBalance > 0 -> savingsBalance * 1.5
            else -> 1000.00
        }
        val emergencyFundTarget = when {
            savingsBalance > calculatedEmergencyTarget -> (savingsBalance * 1.25).coerceAtLeast(1000.00)
            else -> calculatedEmergencyTarget.coerceAtLeast(1000.00)
        }

        vaultDao.insertSavingsGoal(
            SavingsGoalEntity(
                id = 1,
                title = "Total Savings & Reserves",
                targetAmount = emergencyFundTarget,
                currentAmount = savingsBalance,
                targetDateDescription = "6-Month Emergency Savings",
                isPrimary = true
            )
        )

        // Investments goal (if user has investments or wants to build portfolio)
        val investTarget = if (investmentsBalance > 0) (investmentsBalance * 1.5).coerceAtLeast(5000.00) else 10000.00
        vaultDao.insertSavingsGoal(
            SavingsGoalEntity(
                id = 2,
                title = "Wealth Investment Portfolio",
                targetAmount = investTarget,
                currentAmount = investmentsBalance,
                targetDateDescription = "Index Equities & Long-Term Growth",
                isPrimary = false
            )
        )
    }

    suspend fun resetOnboarding() {
        val current = vaultDao.getProfile().firstOrNull()
        if (current != null) {
            vaultDao.insertOrUpdateProfile(current.copy(isOnboardingCompleted = false))
        }
    }

    suspend fun sendChatMessage(userMessage: String): String {
        vaultDao.insertChatMessage(
            ChatMessageEntity(sender = "USER", text = userMessage)
        )

        val historyList = vaultDao.getAllChatMessages().firstOrNull()?.map {
            it.sender to it.text
        } ?: emptyList()

        val profile = vaultDao.getProfile().firstOrNull() ?: VaultProfileEntity()
        val goals = vaultDao.getAllSavingsGoals().firstOrNull()?.map {
            "${it.title}: target ${profile.currencyCode} ${it.targetAmount}, accumulated ${profile.currencyCode} ${it.currentAmount}"
        } ?: emptyList()
        val recentTransactions = vaultDao.getAllTransactions().firstOrNull()?.take(15)?.map {
            "${it.title}: ${profile.currencyCode} ${it.amount} [${it.slipType} - ${it.category}]"
        } ?: emptyList()
        val netWorth = (profile.savingsBalance + profile.checkingBalance) - profile.totalDebt

        val financialContext = CuratorFinancialContext(
            currencyCode = profile.currencyCode,
            monthlyIncome = profile.monthlyIncome,
            hasFixedIncome = profile.hasFixedIncome,
            checkingBalance = profile.checkingBalance,
            savingsBalance = profile.savingsBalance,
            totalDebt = profile.totalDebt,
            fixedExpensesTotal = profile.fixedExpensesTotal,
            dailySafeSpend = profile.dailySafeSpend,
            netWorth = netWorth,
            savingsGoals = goals,
            recentTransactions = recentTransactions
        )

        val response = geminiService.askTutor(userMessage, historyList, financialContext)
        vaultDao.insertChatMessage(
            ChatMessageEntity(sender = "CURATOR", text = response)
        )
        return response
    }

    suspend fun clearChat() = vaultDao.clearChatHistory()

    // Accountability Partner Methods
    suspend fun inviteAndLinkPartner(email: String, name: String) {
        val resolvedName = if (name.isNotBlank()) name.trim() else email.substringBefore("@").replaceFirstChar { it.uppercase() }
        val current = vaultDao.getPartner().firstOrNull() ?: AccountabilityPartnerEntity()
        val updated = current.copy(
            isLinked = true,
            partnerEmail = email.trim(),
            partnerName = resolvedName,
            sharedStreak = if (current.sharedStreak > 0) current.sharedStreak else 1,
            userCheckedInToday = false,
            partnerCheckedInToday = true,
            pactStartDate = System.currentTimeMillis()
        )
        vaultDao.insertOrUpdatePartner(updated)

        // Add initial welcome nudge
        vaultDao.insertNudge(
            PartnerNudgeEntity(
                isFromUser = false,
                senderName = resolvedName,
                message = "Vault Accountability Pact linked! Let's hit our daily safe spend goals and build our streak together.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun completeUserCheckIn(dailySpend: Double = 0.0) {
        val partner = vaultDao.getPartner().firstOrNull() ?: return
        val wasBothCheckedIn = partner.userCheckedInToday && partner.partnerCheckedInToday
        val updatedUserCheckedIn = true
        val isNowBothCheckedIn = updatedUserCheckedIn && partner.partnerCheckedInToday
        val newStreak = if (!wasBothCheckedIn && isNowBothCheckedIn) partner.sharedStreak + 1 else partner.sharedStreak.coerceAtLeast(1)

        val updated = partner.copy(
            userCheckedInToday = true,
            sharedStreak = newStreak,
            userSpentToday = if (dailySpend > 0) dailySpend else partner.userSpentToday,
            lastCheckInDate = "Today"
        )
        vaultDao.insertOrUpdatePartner(updated)
    }

    suspend fun togglePartnerCheckIn() {
        val partner = vaultDao.getPartner().firstOrNull() ?: return
        val newPartnerStatus = !partner.partnerCheckedInToday
        val wasBothCheckedIn = partner.userCheckedInToday && partner.partnerCheckedInToday
        val isNowBothCheckedIn = partner.userCheckedInToday && newPartnerStatus
        val newStreak = if (!wasBothCheckedIn && isNowBothCheckedIn) partner.sharedStreak + 1 else partner.sharedStreak

        val updated = partner.copy(
            partnerCheckedInToday = newPartnerStatus,
            sharedStreak = newStreak
        )
        vaultDao.insertOrUpdatePartner(updated)
    }

    suspend fun sendNudge(message: String) {
        val partner = vaultDao.getPartner().firstOrNull() ?: return
        val userNudge = PartnerNudgeEntity(
            isFromUser = true,
            senderName = "You",
            message = message,
            timestamp = System.currentTimeMillis()
        )
        vaultDao.insertNudge(userNudge)

        val updated = partner.copy(
            lastNudgeSentMessage = message,
            lastNudgeSentTimestamp = System.currentTimeMillis(),
            totalNudgesSent = partner.totalNudgesSent + 1
        )
        vaultDao.insertOrUpdatePartner(updated)
    }

    suspend fun simulatePartnerResponse(reply: String) {
        val partner = vaultDao.getPartner().firstOrNull() ?: return
        val partnerName = if (partner.partnerName.isNotBlank()) partner.partnerName else "Partner"
        val partnerNudge = PartnerNudgeEntity(
            isFromUser = false,
            senderName = partnerName,
            message = reply,
            timestamp = System.currentTimeMillis()
        )
        vaultDao.insertNudge(partnerNudge)

        val updated = partner.copy(
            lastNudgeReceivedMessage = reply,
            lastNudgeReceivedTimestamp = System.currentTimeMillis()
        )
        vaultDao.insertOrUpdatePartner(updated)
    }

    suspend fun unlinkPartner() {
        vaultDao.unlinkPartner()
        vaultDao.clearNudges()
    }

    suspend fun resetStreak() {
        val partner = vaultDao.getPartner().firstOrNull() ?: return
        vaultDao.insertOrUpdatePartner(partner.copy(sharedStreak = 0, userCheckedInToday = false, partnerCheckedInToday = false))
    }

    private fun generateCurriculum(): List<LessonEntity> {
        return listOf(
            LessonEntity(
                id = 1,
                lessonNumber = 1,
                title = "Protect Your Money First",
                subtitle = "Rule #1: Don't lose money you worked hard to earn",
                principle = "Before you try to grow your money, you must protect what you already have. Avoid risky bets that can wipe you out.",
                content = "Building real wealth isn't about secret tricks or risky shortcuts. It's about keeping the money you make.\n\nWhen you lose $1,000 on a bad gamble or impulse purchase, you don't just lose $1,000 today. You also lose all the money that $1,000 could have earned you over the next 10, 20, or 30 years.\n\nEvery time you are about to spend or invest, pause and ask yourself: 'Is this money safe, and do I understand what I am doing?'",
                actionItem = "Check your bank statement from the last 30 days. Cancel any subscription or service you don't actually use.",
                isCompleted = true,
                isPremium = false
            ),
            LessonEntity(
                id = 2,
                lessonNumber = 2,
                title = "The 50/30/20 Budgeting Rule",
                subtitle = "How to easily split your monthly paycheck",
                principle = "Give every dollar a clear job so you always have enough for bills, savings, and guilt-free fun.",
                content = "Managing your money doesn't have to be complicated. The 50/30/20 rule gives you an easy roadmap:\n\n• 50% for Needs (Expenses): Rent or mortgage, groceries, utilities, and gas.\n• 30% for Savings (Future You): Emergency cash, retirement funds, and paying off debt.\n• 20% for Wants (Fun Money): Dining out, hobbies, and shopping—spent without guilt because bills are already handled.\n\nFollowing this simple formula ensures you get ahead and build real savings every month.",
                actionItem = "Look at your monthly take-home pay and check if you are saving at least 20% to 30% of it.",
                isCompleted = true,
                isPremium = false
            ),
            LessonEntity(
                id = 3,
                lessonNumber = 3,
                title = "Your Emergency Rainy Day Fund",
                subtitle = "Why having cash in the bank gives you total peace of mind",
                principle = "Cash in the bank protects you when life happens, so an unexpected bill doesn't turn into high-interest debt.",
                content = "Unexpected things happen to everyone: cars need repairs, appliances break down, or jobs change. Without cash saved up, an emergency forces you to swipe a credit card and pay high interest.\n\nYour goal is to save 3 to 6 months of basic living expenses in a safe savings account. This isn't money for shopping or investing—it is your safety net so you can sleep peacefully at night no matter what happens.",
                actionItem = "Set a goal to save your first $1,000 emergency buffer, then build it up to 3 months of living costs.",
                isCompleted = false,
                isPremium = false
            ),
            LessonEntity(
                id = 4,
                lessonNumber = 4,
                title = "The Magic of Compound Interest",
                subtitle = "How small regular savings grow into a fortune over time",
                principle = "Money makes money, and the money that money makes, makes more money.",
                content = "Compound interest is like a snowball rolling down a hill. At first it looks small, but as it rolls, it picks up speed and gets massive.\n\nFor example: If you invest $250 every month into a standard S&P 500 index fund starting at age 25, by age 65 you will have put in $120,000 of your own money—but your account could grow to over $800,000 thanks to compound interest.\n\nYou don't need to be rich to start. You just need to start early and be consistent.",
                actionItem = "Open the Investing Simulator in the app to see what your monthly savings will be worth in 10 and 20 years.",
                isCompleted = false,
                isPremium = false
            ),
            LessonEntity(
                id = 5,
                lessonNumber = 5,
                title = "Crushing High-Interest Debt",
                subtitle = "How to wipe out credit cards and payday loans fast",
                principle = "Paying 20%+ interest on credit cards drains your wallet faster than almost any investment can grow.",
                content = "Credit card companies make billions because people carry balances at 20% to 28% interest rates. If you owe $5,000 on a card, you might be throwing away over $1,000 a year just on interest fees!\n\nTo become debt-free, use the Avalanche Method:\n1. Pay the minimum payment on all your cards.\n2. Put every extra dollar toward the card with the highest interest rate.\n3. Once that card reaches $0, attack the next one until you are 100% free.",
                actionItem = "Write down all your debts, their balances, and their interest rates. Pick the highest interest rate to tackle first.",
                isCompleted = false,
                isPremium = false
            ),
            LessonEntity(
                id = 6,
                lessonNumber = 6,
                title = "Buying Pieces of Great Companies",
                subtitle = "Investing in businesses you know and use every day",
                principle = "Buying a stock means you own a real piece of a profitable business.",
                content = "When you buy shares of a great company (like Apple, Costco, or Nike), you become a part-owner. When the company makes a profit, your share becomes more valuable.\n\nLook for businesses people love, with products they can't live without.\n\nEven better: You don't have to guess or pick single stocks! You can buy a low-cost index fund (like the S&P 500) that lets you own a piece of America's 500 largest companies all at once.",
                actionItem = "Think of 3 products or stores you use every single week. Research if they are publicly traded companies.",
                isCompleted = false,
                isPremium = false
            ),
            LessonEntity(
                id = 7,
                lessonNumber = 7,
                title = "The 72-Hour Spending Rule",
                subtitle = "How to stop impulse buys and save hundreds of dollars",
                principle = "Waiting before you buy stops emotional spending and puts you in complete control.",
                content = "Stores and online ads are built to make you feel like you must buy things right now. This is how impulse spending drains your wallet.\n\nTry this simple trick called the 72-Hour Rule: Whenever you feel the urge to buy a non-essential item over $50, wait 3 days (72 hours). Add it to a wishlist instead of checking out immediately.\n\nMost of the time, the emotional urge will fade, and you'll be glad you kept that money in your savings.",
                actionItem = "The next time you want to buy something online on impulse, wait 72 hours before completing the purchase.",
                isCompleted = false,
                isPremium = false
            ),
            // Premium Lessons (8 - 12)
            LessonEntity(
                id = 8,
                lessonNumber = 8,
                title = "Spreading Out Your Investments",
                subtitle = "Why you shouldn't put all your eggs in one basket",
                principle = "Diversification means owning different types of assets so you are protected if one drops in value.",
                content = "If you put all your money into one company's stock and that company runs into trouble, you could lose a lot. But when your money is spread across different investments, a drop in one area won't derail your life.\n\nA simple, strong portfolio holds:\n• Index funds (stocks for long-term growth)\n• Real estate or REITs (property and rental income)\n• Cash savings (safe emergency reserves)\n\nThis keeps your money growing steadily with much less worry.",
                actionItem = "Look at where your savings and investments are right now. Make sure you aren't risking too much in just one single place.",
                isCompleted = false,
                isPremium = true
            ),
            LessonEntity(
                id = 9,
                lessonNumber = 9,
                title = "Keeping More of What You Earn (Taxes)",
                subtitle = "Using simple tax-saving accounts like a 401(k) and Roth IRA",
                principle = "Taxes are one of your biggest lifetime expenses. Using legal retirement accounts helps your money grow faster.",
                content = "The government offers everyday people special accounts to legally save on taxes:\n\n• 401(k) or 403(b): Many employers match your contributions (which is literally 100% free bonus money!). Always contribute enough to get the full match.\n• Roth IRA: You put in money today, and all your future growth and retirement withdrawals are 100% tax-free!\n• HSA (Health Savings Account): Special tax savings for healthcare and future retirement.\n\nUsing these accounts can save you tens of thousands of dollars over your career.",
                actionItem = "Check if your workplace offers a 401(k) match. If they do, make sure you are contributing enough to get the full free match.",
                isCompleted = false,
                isPremium = true
            ),
            LessonEntity(
                id = 10,
                lessonNumber = 10,
                title = "What to Do When the Stock Market Drops",
                subtitle = "Staying calm and treating market dips like a big sale",
                principle = "Market drops are completely normal. When prices go down, you get to buy great investments on sale.",
                content = "Every few years, news headlines scream that the stock market is falling. Many people panic and sell at the worst time, locking in their losses.\n\nSmart investors do the opposite. When your favorite grocery store has a 30% off sale, you don't run away in panic—you buy more! The stock market is the only place where people get scared when things go on sale.\n\nStay calm, keep investing your regular monthly amount automatically, and remember that historically the market has always recovered and gone on to new highs.",
                actionItem = "Remind yourself: next time the market has a bad week or month, do not panic sell. Keep your regular monthly savings going.",
                isCompleted = false,
                isPremium = true
            ),
            LessonEntity(
                id = 11,
                lessonNumber = 11,
                title = "Real Estate Basics for Beginners",
                subtitle = "How rental properties create monthly passive income",
                principle = "Real estate lets you earn monthly rent while the property increases in value over time.",
                content = "Owning real estate is one of the most proven ways regular people build lasting wealth. Here is how it works simply:\n\nYou buy a property (like a duplex or house) with a mortgage. Tenants pay you rent every month. That rent covers your mortgage, insurance, property taxes, and repairs. Whatever cash is left over is profit in your pocket.\n\nOver 15 to 30 years, your tenants pay off the mortgage for you, leaving you with a valuable property and reliable monthly income for life.",
                actionItem = "Look up rental prices in your neighborhood to see what typical monthly rents look like compared to home prices.",
                isCompleted = false,
                isPremium = true
            ),
            LessonEntity(
                id = 12,
                lessonNumber = 12,
                title = "Building Wealth for Your Family's Future",
                subtitle = "Setting up your loved ones for lifelong financial peace",
                principle = "True wealth is passing down both financial security and great money habits to your children.",
                content = "Building wealth isn't just about what you can buy today—it is about creating freedom, stability, and peace of mind for your family.\n\nTo protect your loved ones:\n1. Have a simple will so your wishes are crystal clear.\n2. Have affordable term life insurance if family members rely on your income.\n3. Most importantly: teach your children how to budget, save, and invest so they grow up smart and confident with money.",
                actionItem = "Make sure your bank accounts and insurance policies have your chosen beneficiaries clearly listed.",
                isCompleted = false,
                isPremium = true
            )
        )
    }
}
