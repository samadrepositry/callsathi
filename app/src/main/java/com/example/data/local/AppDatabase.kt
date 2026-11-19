package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        UserPreferencesEntity::class,
        ConversationSegmentEntity::class,
        PracticeSessionEntity::class,
        LearningProgressEntity::class,
        PaymentTransactionEntity::class,
        SupportTicketEntity::class,
        UsageRecordEntity::class,
        AdminSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun preferencesDao(): PreferencesDao
    abstract fun conversationDao(): ConversationDao
    abstract fun practiceDao(): PracticeDao
    abstract fun learningDao(): LearningDao
    abstract fun paymentDao(): PaymentDao
    abstract fun supportDao(): SupportDao
    abstract fun usageDao(): UsageDao
    abstract fun adminSettingsDao(): AdminSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "callsathi_local_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedInitialData(getInstance(context))
                        }
                    }
                }).build().also { INSTANCE = it }
            }
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            val now = System.currentTimeMillis()
            val dayMs = 86_400_000L

            database.adminSettingsDao().upsertSettings(AdminSettingsEntity())

            // Demo Agent Account
            val demoId = "demo_agent_pk"
            database.profileDao().upsertProfile(
                UserProfileEntity(
                    id = demoId,
                    fullName = "Hamza Tariq (Demo)",
                    email = "hamza.demo@callsathi.pk",
                    passwordHash = "demo1234",
                    englishLevel = "BEGINNER",
                    preferredLanguage = "URDU",
                    planTier = "FREE_TRIAL",
                    accountStatus = "ACTIVE",
                    isAdmin = false,
                    emailVerified = true,
                    subscriptionExpiresAt = now + 7 * dayMs,
                    createdAt = now - 5 * dayMs
                )
            )
            database.preferencesDao().upsertPreferences(UserPreferencesEntity(userId = demoId))
            database.usageDao().upsertUsage(
                UsageRecordEntity(
                    userId = demoId,
                    transcriptionSecondsUsed = 140,
                    aiRepliesUsed = 6,
                    ttsCountUsed = 9,
                    practiceMinutesUsed = 12,
                    dailyStreak = 4,
                    lastPracticeDay = now - dayMs
                )
            )

            // Admin Account
            val adminId = "admin_callsathi"
            database.profileDao().upsertProfile(
                UserProfileEntity(
                    id = adminId,
                    fullName = "Bilal Ahmed (Admin)",
                    email = "admin@callsathi.pk",
                    passwordHash = "admin1234",
                    englishLevel = "ADVANCED",
                    preferredLanguage = "URDU",
                    planTier = "YEARLY_PRO",
                    accountStatus = "ACTIVE",
                    isAdmin = true,
                    emailVerified = true,
                    subscriptionExpiresAt = now + 365 * dayMs,
                    createdAt = now - 30 * dayMs
                )
            )
            database.preferencesDao().upsertPreferences(
                UserPreferencesEntity(userId = adminId, tone = "FORMAL", difficulty = "ADVANCED")
            )
            database.usageDao().upsertUsage(UsageRecordEntity(userId = adminId, dailyStreak = 14))

            // Additional Sample Users for Admin Analytics
            database.profileDao().upsertProfile(
                UserProfileEntity(
                    id = "user_ayesha",
                    fullName = "Ayesha Siddiqui",
                    email = "ayesha.s@lahorebpo.pk",
                    passwordHash = "pass1234",
                    englishLevel = "INTERMEDIATE",
                    preferredLanguage = "URDU",
                    planTier = "MONTHLY_PRO",
                    accountStatus = "ACTIVE",
                    isAdmin = false,
                    emailVerified = true,
                    subscriptionExpiresAt = now + 22 * dayMs,
                    createdAt = now - 8 * dayMs
                )
            )
            database.profileDao().upsertProfile(
                UserProfileEntity(
                    id = "user_usman",
                    fullName = "Usman Raza",
                    email = "usman.raza@karachicalls.pk",
                    passwordHash = "pass1234",
                    englishLevel = "BEGINNER",
                    preferredLanguage = "URDU",
                    planTier = "YEARLY_PRO",
                    accountStatus = "ACTIVE",
                    isAdmin = false,
                    emailVerified = true,
                    subscriptionExpiresAt = now + 340 * dayMs,
                    createdAt = now - 25 * dayMs
                )
            )

            // Sample Transactions (including a pending receipt for Admin review)
            database.paymentDao().upsertTransaction(
                PaymentTransactionEntity(
                    id = "tx_sample_1",
                    userId = "user_ayesha",
                    userName = "Ayesha Siddiqui",
                    userEmail = "ayesha.s@lahorebpo.pk",
                    planTier = "MONTHLY_PRO",
                    amountPkr = 2000,
                    method = "JAZZCASH",
                    referenceCode = "CS-JC-88412",
                    receiptUri = null,
                    status = "APPROVED",
                    rejectionReason = null,
                    createdAt = now - 8 * dayMs,
                    reviewedAt = now - 8 * dayMs
                )
            )
            database.paymentDao().upsertTransaction(
                PaymentTransactionEntity(
                    id = "tx_sample_2",
                    userId = "user_usman",
                    userName = "Usman Raza",
                    userEmail = "usman.raza@karachicalls.pk",
                    planTier = "YEARLY_PRO",
                    amountPkr = 6000,
                    method = "BANK_TRANSFER",
                    referenceCode = "CS-MZN-51904",
                    receiptUri = null,
                    status = "APPROVED",
                    rejectionReason = null,
                    createdAt = now - 25 * dayMs,
                    reviewedAt = now - 24 * dayMs
                )
            )
            database.paymentDao().upsertTransaction(
                PaymentTransactionEntity(
                    id = "tx_sample_pending",
                    userId = demoId,
                    userName = "Hamza Tariq (Demo)",
                    userEmail = "hamza.demo@callsathi.pk",
                    planTier = "MONTHLY_PRO",
                    amountPkr = 2000,
                    method = "EASYPAISA",
                    referenceCode = "CS-EP-77319",
                    receiptUri = null,
                    status = "PENDING_REVIEW",
                    rejectionReason = null,
                    createdAt = now - 3_600_000L,
                    reviewedAt = null
                )
            )

            // Sample Support Ticket
            database.supportDao().upsertTicket(
                SupportTicketEntity(
                    id = "tk_sample_1",
                    userId = demoId,
                    userEmail = "hamza.demo@callsathi.pk",
                    subject = "USB Headset setup for Desktop Call Mode",
                    message = "Assalam o Alaikum, how do I connect my USB headset so I can hear the customer clearly?",
                    category = "Technical Support",
                    status = "OPEN",
                    createdAt = now - 7_200_000L
                )
            )
        }
    }
}
