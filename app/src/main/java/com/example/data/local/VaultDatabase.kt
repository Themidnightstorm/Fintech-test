package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.VaultDao
import com.example.data.local.entity.AccountabilityPartnerEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.LessonEntity
import com.example.data.local.entity.PartnerNudgeEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.VaultProfileEntity

@Database(
    entities = [
        TransactionEntity::class,
        SavingsGoalEntity::class,
        LessonEntity::class,
        VaultProfileEntity::class,
        ChatMessageEntity::class,
        AccountabilityPartnerEntity::class,
        PartnerNudgeEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getDatabase(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "the_vault_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
