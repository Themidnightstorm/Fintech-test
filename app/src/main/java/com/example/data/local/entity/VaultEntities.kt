package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SlipType {
    EXPENSES,
    SAVINGS,
    WANTS
}

@Entity(tableName = "vault_transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val slipType: String, // "EXPENSES", "SAVINGS", "WANTS"
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDateDescription: String = "Long-Term Reserve",
    val isPrimary: Boolean = true
)

@Entity(tableName = "finance_lessons")
data class LessonEntity(
    @PrimaryKey
    val id: Long,
    val lessonNumber: Int,
    val title: String,
    val subtitle: String,
    val principle: String,
    val content: String,
    val actionItem: String,
    val isCompleted: Boolean = false,
    val isPremium: Boolean = false
)

@Entity(tableName = "vault_profile")
data class VaultProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val isPremiumUnlocked: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    val baseNetWorth: Double = 125000.00,
    val monthlyIncome: Double = 9500.00,
    val hasFixedIncome: Boolean = true,
    val checkingBalance: Double = 0.00,
    val totalDebt: Double = 0.00,
    val savingsBalance: Double = 0.00,
    val investmentsBalance: Double = 0.00,
    val fixedExpensesTotal: Double = 0.00,
    val dailySafeSpend: Double = 0.00,
    val sovereignTitle: String = "Vault Master",
    val currencyCode: String = "USD",
    val membershipTier: String = "FREE", // "FREE", "PRO", "PREMIUM"
    val subscribedEmail: String = "",
    val occupation: String = "",
    val employerOrWorkplace: String = "",
    val incomeType: String = "FIXED_SALARY" // "FIXED_SALARY", "HOURLY_WAGE", "FREELANCE_GIG", "BUSINESS_OWNER", "STUDENT_ALLOWANCE", "INVESTMENTS_PASSIVE"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "SOVEREIGN"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "accountability_partner")
data class AccountabilityPartnerEntity(
    @PrimaryKey
    val id: Int = 1,
    val isLinked: Boolean = false,
    val partnerEmail: String = "",
    val partnerName: String = "",
    val partnerTitle: String = "Financial Confidant",
    val sharedStreak: Int = 0,
    val userCheckedInToday: Boolean = false,
    val partnerCheckedInToday: Boolean = false,
    val userDailySafeSpend: Double = 120.00,
    val partnerDailySafeSpend: Double = 115.00,
    val userSpentToday: Double = 42.50,
    val partnerSpentToday: Double = 35.00,
    val lastCheckInDate: String = "",
    val lastNudgeSentMessage: String = "",
    val lastNudgeSentTimestamp: Long = 0L,
    val lastNudgeReceivedMessage: String = "",
    val lastNudgeReceivedTimestamp: Long = 0L,
    val totalNudgesSent: Int = 0,
    val pactStartDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "partner_nudges")
data class PartnerNudgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val isFromUser: Boolean = true,
    val senderName: String = "You",
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

