package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AccountabilityPartnerEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.LessonEntity
import com.example.data.local.entity.PartnerNudgeEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.VaultProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    // Transactions
    @Query("SELECT * FROM vault_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM vault_transactions")
    suspend fun getTransactionList(): List<TransactionEntity>

    @Query("SELECT * FROM vault_transactions WHERE slipType = :slipType ORDER BY timestamp DESC")
    fun getTransactionsBySlip(slipType: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>): List<Long>

    @Query("DELETE FROM vault_transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("DELETE FROM vault_transactions")
    suspend fun clearAllTransactions()

    // Savings Goals
    @Query("SELECT * FROM savings_goals ORDER BY isPrimary DESC, id ASC")
    fun getAllSavingsGoals(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals")
    suspend fun getSavingsGoalList(): List<SavingsGoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity): Long

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity)

    @Query("UPDATE savings_goals SET currentAmount = currentAmount + :amount WHERE id = :goalId")
    suspend fun addFundsToGoal(goalId: Long, amount: Double)

    // Lessons
    @Query("SELECT * FROM finance_lessons ORDER BY lessonNumber ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM finance_lessons WHERE id = :id")
    suspend fun getLessonById(id: Long): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE finance_lessons SET isCompleted = :completed WHERE id = :id")
    suspend fun updateLessonCompletion(id: Long, completed: Boolean)

    // Profile
    @Query("SELECT * FROM vault_profile WHERE id = 1")
    fun getProfile(): Flow<VaultProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: VaultProfileEntity)

    @Query("UPDATE vault_profile SET isPremiumUnlocked = :unlocked WHERE id = 1")
    suspend fun setPremiumStatus(unlocked: Boolean)

    @Query("UPDATE vault_profile SET membershipTier = :tier, isPremiumUnlocked = (:tier != 'FREE') WHERE id = 1")
    suspend fun updateMembershipTier(tier: String)

    @Query("UPDATE vault_profile SET subscribedEmail = :email WHERE id = 1")
    suspend fun updateSubscribedEmail(email: String)

    @Query("UPDATE vault_profile SET currencyCode = :currencyCode WHERE id = 1")
    suspend fun updateCurrency(currencyCode: String)

    // Chat
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()

    // Accountability Partner
    @Query("SELECT * FROM accountability_partner WHERE id = 1")
    fun getPartner(): Flow<AccountabilityPartnerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePartner(partner: AccountabilityPartnerEntity)

    @Query("UPDATE accountability_partner SET isLinked = 0, partnerEmail = '', partnerName = '', sharedStreak = 0 WHERE id = 1")
    suspend fun unlinkPartner()

    @Query("SELECT * FROM partner_nudges ORDER BY timestamp DESC LIMIT 30")
    fun getRecentNudges(): Flow<List<PartnerNudgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNudge(nudge: PartnerNudgeEntity): Long

    @Query("DELETE FROM partner_nudges")
    suspend fun clearNudges()
}
