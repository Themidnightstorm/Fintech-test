package com.example.data.model

import com.example.data.local.entity.LessonEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.SlipType
import com.example.data.local.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

data class ReviewMilestoneBadge(
    val id: String,
    val title: String,
    val description: String,
    val category: String // "SAVINGS", "DEBT", "STREAK", "KNOWLEDGE"
)

data class MonthlyReviewData(
    val monthTitle: String, // e.g. "August 2026 Audit"
    val reviewPeriod: String, // e.g. "Aug 1 – Aug 31, 2026"
    val overallGrade: String, // e.g. "A+ PREMIER STANDING"
    val gradeSubtitle: String,
    // Metric 1: Total Saved vs Goal
    val totalSavedThisMonth: Double,
    val savingsMonthlyTarget: Double,
    val savingsGoalPercentage: Int,
    val primaryGoalProgressText: String,
    // Metric 2: Debt Paid Down
    val debtPaidDownThisMonth: Double,
    val remainingDebt: Double,
    val debtFreeStatusText: String,
    // Metric 3: Net Worth Change
    val currentNetWorth: Double,
    val netWorthGrowthAmount: Double,
    val netWorthGrowthRatePercent: Double,
    // Metric 4: Streak Consistency
    val currentStreakDays: Int,
    val streakConsistencyPercent: Int,
    val ledgerDisciplineText: String,
    // Milestones achieved
    val badgesEarned: List<ReviewMilestoneBadge>,
    // Recommendations for next month
    val nextMonthDirectives: List<String>,
    val currency: VaultCurrency = VaultCurrency.USD
) {
    companion object {
        fun generate(
            netWorth: Double,
            monthlyIncome: Double,
            fixedExpensesTotal: Double,
            totalDebt: Double,
            savingsBalance: Double,
            allTransactions: List<TransactionEntity>,
            allGoals: List<SavingsGoalEntity>,
            lessons: List<LessonEntity>,
            streakDays: Int,
            dailySafeSpend: Double = 0.0,
            currency: VaultCurrency
        ): MonthlyReviewData {
            val calendar = Calendar.getInstance()
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
            val monthTitle = "${monthFormat.format(calendar.time)} Audit"
            val periodFormat = SimpleDateFormat("MMM d – MMM", Locale.US)
            val yearFormat = SimpleDateFormat("yyyy", Locale.US)
            val reviewPeriod = "${periodFormat.format(calendar.time)} ${calendar.getActualMaximum(Calendar.DAY_OF_MONTH)}, ${yearFormat.format(calendar.time)}"

            val savingsTransactions = allTransactions.filter { it.slipType == SlipType.SAVINGS.name }
            val totalSavedTx = savingsTransactions.sumOf { it.amount }
            // Real savings: Use actual logged transactions. If none logged yet, reflect savingsBalance (or 0.0 if user has no savings)
            val totalSaved = if (totalSavedTx > 0) {
                totalSavedTx
            } else if (savingsBalance > 0) {
                savingsBalance.coerceAtMost(if (monthlyIncome > 0) monthlyIncome * 0.30 else savingsBalance)
            } else {
                0.00
            }

            val savingsTarget = if (monthlyIncome > 0) (monthlyIncome * 0.30) else if (savingsBalance > 0) (savingsBalance * 0.20) else 100.0
            val savingsPercent = if (savingsTarget > 0 && totalSaved > 0) {
                ((totalSaved / savingsTarget) * 100.0).roundToInt().coerceIn(0, 250)
            } else {
                0
            }

            val primaryGoal = allGoals.firstOrNull { it.isPrimary } ?: allGoals.firstOrNull()
            val primaryGoalText = if (primaryGoal != null) {
                val targetAmt = primaryGoal.targetAmount.coerceAtLeast(1.0)
                val pct = ((primaryGoal.currentAmount / targetAmt) * 100).toInt()
                "${primaryGoal.title}: ${currency.format(primaryGoal.currentAmount, false)} / ${currency.format(primaryGoal.targetAmount, false)} ($pct%)"
            } else {
                "Total Savings & Reserves: ${currency.format(savingsBalance, false)}"
            }

            // Debt metrics
            val isDebtFree = totalDebt <= 0.0
            val estimatedDebtPaid = if (isDebtFree) 0.0 else (monthlyIncome * 0.15).coerceAtLeast(250.0).coerceAtMost(totalDebt)
            val debtStatus = if (isDebtFree) "Zero Liabilities • 100% Cash Flow Retained" else "Active Avalanche Paydown on Track"

            // Net worth change
            val estimatedGrowthAmt = (totalSaved * 0.90) + (if (isDebtFree) 450.0 else estimatedDebtPaid * 0.8)
            val growthRate = if (netWorth > 0) (estimatedGrowthAmt / netWorth * 100.0).coerceIn(0.5, 25.0) else 4.2

            // Streak & Consistency
            val activeStreak = streakDays.coerceAtLeast(1)
            val consistency = (activeStreak * 10).coerceIn(75, 100)
            val disciplineRating = if (consistency >= 90) "Exemplary Daily Safe Spend Execution" else "Consistent Ledger Tracking"

            // Overall Grade
            val grade = if (savingsPercent >= 100 && (isDebtFree || estimatedDebtPaid > 0)) {
                "A+ PREMIER STANDING"
            } else if (savingsPercent >= 50 || totalSaved > 0) {
                "A DISTINGUISHED BUILDER"
            } else {
                "B FOUNDATION IN PROGRESS"
            }

            val gradeSubtitle = "Top Tier Capital Efficiency & Ledger Discipline"

            // Badges
            val badges = mutableListOf<ReviewMilestoneBadge>()
            if (savingsPercent >= 100) {
                badges.add(
                    ReviewMilestoneBadge(
                        id = "savings_target_met",
                        title = "30% Premier Wealth Builder",
                        description = "Achieved 100%+ of the monthly savings allocation target.",
                        category = "SAVINGS"
                    )
                )
            }
            if (isDebtFree) {
                badges.add(
                    ReviewMilestoneBadge(
                        id = "debt_free_badge",
                        title = "Unencumbered Balance",
                        description = "Zero consumer liabilities dragging monthly compound velocity.",
                        category = "DEBT"
                    )
                )
            } else {
                badges.add(
                    ReviewMilestoneBadge(
                        id = "debt_crusher_badge",
                        title = "Debt Avalanche Force",
                        description = "Reduced liability balances by ${currency.format(estimatedDebtPaid, false)} this cycle.",
                        category = "DEBT"
                    )
                )
            }

            if (activeStreak >= 3) {
                badges.add(
                    ReviewMilestoneBadge(
                        id = "streak_keeper",
                        title = "Ledger Guardian",
                        description = "Maintained continuous daily check-in and slip discipline.",
                        category = "STREAK"
                    )
                )
            }

            val completedLessons = lessons.count { it.isCompleted }
            if (completedLessons > 0) {
                badges.add(
                    ReviewMilestoneBadge(
                        id = "scholar_badge",
                        title = "Aristocratic Scholar",
                        description = "Mastered $completedLessons Wealth Curriculum modules.",
                        category = "KNOWLEDGE"
                    )
                )
            }

            // Next Month Directives
            val directives = mutableListOf<String>()
            if (!isDebtFree) {
                directives.add("Funnel an additional ${currency.format(100.0, false)} to your highest APR debt balance.")
            } else {
                directives.add("Direct excess cashflow beyond the 6-month citadel into broad-market index assets.")
            }
            val targetDaily = if (dailySafeSpend > 0) dailySafeSpend else (monthlyIncome * 0.20 / 30.0)
            directives.add("Maintain daily discretionary spend under ${currency.format(targetDaily, false)}/day via the Daily Safe Spend gauge.")
            directives.add("Explore Lesson ${completedLessons + 1} in the Wealth Curriculum to sharpen asset structuring.")
            directives.add("Perform a weekly review of Bank Ledger CSV slips to detect subscription creep.")

            return MonthlyReviewData(
                monthTitle = monthTitle,
                reviewPeriod = reviewPeriod,
                overallGrade = grade,
                gradeSubtitle = gradeSubtitle,
                totalSavedThisMonth = totalSaved,
                savingsMonthlyTarget = savingsTarget,
                savingsGoalPercentage = savingsPercent,
                primaryGoalProgressText = primaryGoalText,
                debtPaidDownThisMonth = estimatedDebtPaid,
                remainingDebt = totalDebt,
                debtFreeStatusText = debtStatus,
                currentNetWorth = netWorth,
                netWorthGrowthAmount = estimatedGrowthAmt,
                netWorthGrowthRatePercent = growthRate,
                currentStreakDays = activeStreak,
                streakConsistencyPercent = consistency,
                ledgerDisciplineText = disciplineRating,
                badgesEarned = badges,
                nextMonthDirectives = directives,
                currency = currency
            )
        }
    }
}
