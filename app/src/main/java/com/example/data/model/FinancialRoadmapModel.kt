package com.example.data.model

import com.example.data.local.entity.SavingsGoalEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

enum class MilestoneStatus {
    COMPLETED,
    IN_PROGRESS,
    UPCOMING
}

data class RoadmapMilestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val status: MilestoneStatus,
    val estimatedDateText: String,
    val guidance: String,
    val iconType: String // "emergency", "debt", "citadel", "investing", "goal", "fire"
) {
    val progressFraction: Float
        get() = if (targetAmount <= 0.0) 1f else (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f)

    val progressPercent: Int
        get() = (progressFraction * 100f).roundToInt()
}

data class RecommendedAction(
    val id: String,
    val title: String,
    val category: String, // "RESERVES", "DEBT", "INVESTING", "LEDGER"
    val description: String,
    val monthlyTargetAmount: Double? = null,
    val isCompleted: Boolean = false,
    val priorityTag: String = "HIGH PRIORITY"
)

data class FinancialRoadmapData(
    val currentNetWorth: Double,
    val monthlyIncome: Double,
    val fixedExpensesTotal: Double,
    val totalDebt: Double,
    val savingsBalance: Double,
    val monthlySavingsCapacity: Double,
    val fiTargetNumber: Double,
    val fiProgressPercent: Float,
    val estimatedYearsToFi: Double,
    val milestones: List<RoadmapMilestone>,
    val recommendedActions: List<RecommendedAction>,
    val currency: VaultCurrency = VaultCurrency.USD
) {
    companion object {
        fun calculate(
            netWorth: Double,
            monthlyIncome: Double,
            fixedExpensesTotal: Double,
            totalDebt: Double,
            savingsBalance: Double,
            primaryGoal: SavingsGoalEntity?,
            allGoals: List<SavingsGoalEntity>,
            currency: VaultCurrency
        ): FinancialRoadmapData {
            // Estimated annual living expenses = (fixed expenses + 20% estimated baseline lifestyle) * 12
            val monthlyLivingExpenses = (fixedExpensesTotal + (monthlyIncome * 0.20)).coerceAtLeast(1200.0)
            val annualExpenses = monthlyLivingExpenses * 12.0

            // 25x Annual Expenses Rule for Financial Independence (4% Safe Withdrawal Rate)
            val fiTarget = annualExpenses * 25.0
            val fiProgress = if (fiTarget <= 0.0) 0f else (netWorth / fiTarget * 100.0).toFloat().coerceIn(0f, 100f)

            // Monthly surplus available for wealth deployment
            val monthlySurplus = (monthlyIncome - fixedExpensesTotal - (monthlyIncome * 0.20)).coerceAtLeast(200.0)

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.US)

            val milestones = mutableListOf<RoadmapMilestone>()

            // 1. Starter Emergency Fund (1 Month Fixed Living Expenses or $1,000 min)
            val starterTarget = fixedExpensesTotal.coerceAtLeast(VaultCurrency.USD.convertTo(1000.0, currency))
            val starterCurrent = savingsBalance.coerceAtMost(starterTarget)
            val isStarterComplete = savingsBalance >= starterTarget
            val starterMonths = if (isStarterComplete) 0 else ceil((starterTarget - savingsBalance) / monthlySurplus).toInt().coerceAtLeast(1)
            
            val starterDate = if (isStarterComplete) "Achieved ✓" else {
                val cal = calendar.clone() as Calendar
                cal.add(Calendar.MONTH, starterMonths)
                dateFormat.format(cal.time)
            }

            milestones.add(
                RoadmapMilestone(
                    id = "starter_fund",
                    title = "Starter Emergency Fortress",
                    subtitle = "1-Month Living Reserve (${currency.format(starterTarget, false)})",
                    targetAmount = starterTarget,
                    currentAmount = starterCurrent,
                    status = if (isStarterComplete) MilestoneStatus.COMPLETED else MilestoneStatus.IN_PROGRESS,
                    estimatedDateText = starterDate,
                    guidance = if (isStarterComplete) "Liquid buffer active. Protects you against life emergencies." else "Funnel initial cashflow to build your first defense line.",
                    iconType = "emergency"
                )
            )

            // 2. High-Interest Debt Elimination ('Debt-Free Date')
            val isDebtFree = totalDebt <= 0.0
            val debtMonths = if (isDebtFree) 0 else ceil(totalDebt / monthlySurplus).toInt().coerceAtLeast(1)
            val debtFreeDate = if (isDebtFree) "Debt-Free Premier ✓" else {
                val cal = calendar.clone() as Calendar
                cal.add(Calendar.MONTH, (if (!isStarterComplete) starterMonths else 0) + debtMonths)
                dateFormat.format(cal.time)
            }

            milestones.add(
                RoadmapMilestone(
                    id = "debt_free",
                    title = "Debt-Free Milestone",
                    subtitle = if (isDebtFree) "Zero Liabilities Maintained" else "Eradicate ${currency.format(totalDebt, false)} Total Debt",
                    targetAmount = if (isDebtFree) 1.0 else totalDebt,
                    currentAmount = if (isDebtFree) 1.0 else 0.0,
                    status = if (isDebtFree) MilestoneStatus.COMPLETED else if (isStarterComplete) MilestoneStatus.IN_PROGRESS else MilestoneStatus.UPCOMING,
                    estimatedDateText = debtFreeDate,
                    guidance = if (isDebtFree) "No debt drag. 100% of your earnings stay in your wealth loop." else "Eliminate monthly interest penalties to maximize investment power.",
                    iconType = "debt"
                )
            )

            // 3. 6-Month Citadel Emergency Reserve
            val citadelTarget = fixedExpensesTotal * 6.0
            val isCitadelComplete = savingsBalance >= citadelTarget
            val citadelMonths = if (isCitadelComplete) 0 else ceil((citadelTarget - savingsBalance).coerceAtLeast(0.0) / monthlySurplus).toInt().coerceAtLeast(1)
            val citadelDate = if (isCitadelComplete) "Fully Funded Citadel ✓" else {
                val cal = calendar.clone() as Calendar
                val priorMonths = (if (!isStarterComplete) starterMonths else 0) + (if (!isDebtFree) debtMonths else 0)
                cal.add(Calendar.MONTH, priorMonths + citadelMonths)
                dateFormat.format(cal.time)
            }

            milestones.add(
                RoadmapMilestone(
                    id = "citadel_reserve",
                    title = "6-Month Citadel Reserve",
                    subtitle = "Complete Security Buffer (${currency.format(citadelTarget, false)})",
                    targetAmount = citadelTarget,
                    currentAmount = savingsBalance.coerceAtMost(citadelTarget),
                    status = if (isCitadelComplete) MilestoneStatus.COMPLETED else if (isDebtFree) MilestoneStatus.IN_PROGRESS else MilestoneStatus.UPCOMING,
                    estimatedDateText = citadelDate,
                    guidance = "Unbreakable 6-month safety net in high-yield local vault accounts.",
                    iconType = "citadel"
                )
            )

            // 4. First Investment Portfolio ($10,000 / $25,000 Portfolio)
            val investTarget = VaultCurrency.USD.convertTo(25000.0, currency)
            val investCurrent = (netWorth - savingsBalance).coerceAtLeast(0.0).coerceAtMost(investTarget)
            val isInvestComplete = investCurrent >= investTarget
            val investMonths = if (isInvestComplete) 0 else ceil((investTarget - investCurrent) / monthlySurplus).toInt().coerceAtLeast(1)
            val investDate = if (isInvestComplete) "Target Exceeded ✓" else {
                val cal = calendar.clone() as Calendar
                cal.add(Calendar.MONTH, (if (!isDebtFree) debtMonths else 0) + investMonths)
                dateFormat.format(cal.time)
            }

            milestones.add(
                RoadmapMilestone(
                    id = "first_investments",
                    title = "First Wealth Investment Portfolio",
                    subtitle = "Index Equities & Gold Bullion (${currency.format(investTarget, false)})",
                    targetAmount = investTarget,
                    currentAmount = investCurrent,
                    status = if (isInvestComplete) MilestoneStatus.COMPLETED else if (isDebtFree) MilestoneStatus.IN_PROGRESS else MilestoneStatus.UPCOMING,
                    estimatedDateText = investDate,
                    guidance = "Automate long-term index growth compounding across diversified assets.",
                    iconType = "investing"
                )
            )

            // 5. Primary Savings Goal (if exists)
            if (primaryGoal != null) {
                val goalTarget = primaryGoal.targetAmount
                val goalCurrent = primaryGoal.currentAmount
                val isGoalComplete = goalCurrent >= goalTarget
                val goalMonths = if (isGoalComplete) 0 else ceil((goalTarget - goalCurrent) / (monthlySurplus * 0.5)).toInt().coerceAtLeast(1)
                val goalDate = if (isGoalComplete) "Goal Achieved ✓" else {
                    val cal = calendar.clone() as Calendar
                    cal.add(Calendar.MONTH, goalMonths)
                    dateFormat.format(cal.time)
                }

                milestones.add(
                    RoadmapMilestone(
                        id = "primary_goal_${primaryGoal.id}",
                        title = "Vault Goal: ${primaryGoal.title}",
                        subtitle = "Target: ${currency.format(goalTarget, false)} (${(goalCurrent / goalTarget * 100).toInt()}% Funded)",
                        targetAmount = goalTarget,
                        currentAmount = goalCurrent,
                        status = if (isGoalComplete) MilestoneStatus.COMPLETED else MilestoneStatus.IN_PROGRESS,
                        estimatedDateText = goalDate,
                        guidance = "Dedicated savings allocation earmarked for your key aspiration.",
                        iconType = "goal"
                    )
                )
            }

            // 6. Halfway to Financial Independence (Coast FI - 12.5x Expenses)
            val coastTarget = fiTarget * 0.50
            val isCoastComplete = netWorth >= coastTarget
            val coastDate = if (isCoastComplete) "Coast FI Achieved ✓" else {
                val cal = calendar.clone() as Calendar
                val years = ((coastTarget - netWorth).coerceAtLeast(0.0) / (monthlySurplus * 12 * 1.07)).coerceIn(0.5, 35.0)
                cal.add(Calendar.MONTH, (years * 12).toInt())
                dateFormat.format(cal.time)
            }

            milestones.add(
                RoadmapMilestone(
                    id = "coast_fi",
                    title = "Halfway to Freedom (Coast FI)",
                    subtitle = "50% of Independence Target (${currency.format(coastTarget, false)})",
                    targetAmount = coastTarget,
                    currentAmount = netWorth.coerceAtMost(coastTarget),
                    status = if (isCoastComplete) MilestoneStatus.COMPLETED else MilestoneStatus.UPCOMING,
                    estimatedDateText = coastDate,
                    guidance = "Your compounding engine now generates significant passive velocity.",
                    iconType = "fire"
                )
            )

            // 7. Full Private Wealth Financial Independence (25x Expenses)
            val isFullFiComplete = netWorth >= fiTarget
            val fullFiDate = if (isFullFiComplete) "100% Financial Independence Complete ✓" else {
                val cal = calendar.clone() as Calendar
                val years = ((fiTarget - netWorth).coerceAtLeast(0.0) / (monthlySurplus * 12 * 1.08)).coerceIn(1.0, 45.0)
                cal.add(Calendar.MONTH, (years * 12).toInt())
                dateFormat.format(cal.time)
            }

            milestones.add(
                RoadmapMilestone(
                    id = "full_fi",
                    title = "Private Wealth Financial Independence",
                    subtitle = "25x Annual Expenses Matrix (${currency.format(fiTarget, false)})",
                    targetAmount = fiTarget,
                    currentAmount = netWorth.coerceAtMost(fiTarget),
                    status = if (isFullFiComplete) MilestoneStatus.COMPLETED else MilestoneStatus.UPCOMING,
                    estimatedDateText = fullFiDate,
                    guidance = "Perpetual 4% safe withdrawal rate covers all life expenses indefinitely.",
                    iconType = "fire"
                )
            )

            // Dynamic Recommended Monthly Actions based on progress
            val actions = mutableListOf<RecommendedAction>()

            if (!isStarterComplete) {
                val starterNeeded = starterTarget - savingsBalance
                val monthlyAlloc = (monthlySurplus * 0.8).coerceAtLeast(100.0).coerceAtMost(starterNeeded)
                actions.add(
                    RecommendedAction(
                        id = "act_starter",
                        title = "Direct ${currency.format(monthlyAlloc, false)} to Starter Emergency Vault",
                        category = "RESERVES",
                        description = "Prioritize building your 1-month liquid cash buffer before aggressive investments.",
                        monthlyTargetAmount = monthlyAlloc,
                        priorityTag = "URGENT PRIORITY"
                    )
                )
            }

            if (!isDebtFree) {
                val debtPaydown = (monthlySurplus * 0.70).coerceAtLeast(150.0).coerceAtMost(totalDebt)
                actions.add(
                    RecommendedAction(
                        id = "act_debt",
                        title = "Apply ${currency.format(debtPaydown, false)}/mo Avalanche Debt Paydown",
                        category = "DEBT",
                        description = "Target your highest APR balances first to destroy compound interest decay.",
                        monthlyTargetAmount = debtPaydown,
                        priorityTag = "HIGH IMPACT"
                    )
                )
            }

            if (isStarterComplete && isDebtFree) {
                val investMonthly = monthlySurplus * 0.75
                actions.add(
                    RecommendedAction(
                        id = "act_invest",
                        title = "Automate ${currency.format(investMonthly, false)} into Broad-Market Index Assets",
                        category = "INVESTING",
                        description = "Dollar-cost average into global total stock index funds + gold for multi-decade compounding.",
                        monthlyTargetAmount = investMonthly,
                        priorityTag = "COMPOUNDING"
                    )
                )
            }

            actions.add(
                RecommendedAction(
                    id = "act_daily_safe",
                    title = "Maintain Daily Safe Spend Ceiling",
                    category = "LEDGER",
                    description = "Keep unbudgeted daily discretionary spend strictly below ${currency.format(monthlyIncome * 0.20 / 30.0, false)}/day.",
                    monthlyTargetAmount = monthlyIncome * 0.20,
                    priorityTag = "DISCIPLINE"
                )
            )

            actions.add(
                RecommendedAction(
                    id = "act_ledger_sync",
                    title = "Review Weekly Money Slips",
                    category = "LEDGER",
                    description = "Verify all bank imports and daily slip logs to ensure zero invisible subscription leaks.",
                    monthlyTargetAmount = null,
                    priorityTag = "ROUTINE"
                )
            )

            val yearsToFi = ((fiTarget - netWorth).coerceAtLeast(0.0) / (monthlySurplus * 12 * 1.08)).coerceIn(0.1, 45.0)

            return FinancialRoadmapData(
                currentNetWorth = netWorth,
                monthlyIncome = monthlyIncome,
                fixedExpensesTotal = fixedExpensesTotal,
                totalDebt = totalDebt,
                savingsBalance = savingsBalance,
                monthlySavingsCapacity = monthlySurplus,
                fiTargetNumber = fiTarget,
                fiProgressPercent = fiProgress,
                estimatedYearsToFi = yearsToFi,
                milestones = milestones,
                recommendedActions = actions,
                currency = currency
            )
        }
    }
}
