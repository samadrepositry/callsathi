package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val englishLevel: String,
    val preferredLanguage: String,
    val planTier: String,
    val accountStatus: String,
    val isAdmin: Boolean,
    val emailVerified: Boolean,
    val subscriptionExpiresAt: Long?,
    val createdAt: Long
)

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val userId: String,
    val accent: String = "AMERICAN",
    val replyLength: String = "MEDIUM",
    val difficulty: String = "BEGINNER",
    val tone: String = "PROFESSIONAL",
    val urduScript: String = "URDU_SCRIPT",
    val darkMode: Boolean = true,
    val saveTranscriptsByDefault: Boolean = false
)

@Entity(tableName = "conversation_segments")
data class ConversationSegmentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val callMode: String,
    val customerEnglish: String,
    val customerUrdu: String,
    val customerRomanUrdu: String,
    val intent: String,
    val simpleReply: String,
    val simpleReplyUrdu: String,
    val professionalReply: String,
    val professionalReplyUrdu: String,
    val friendlyReply: String,
    val friendlyReplyUrdu: String,
    val pronunciationTip: String,
    val createdAt: Long
)

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val scenarioTitle: String,
    val speakingScore: Int,
    val grammarScore: Int,
    val responseQualityScore: Int,
    val confidenceScore: Int,
    val feedbackSummary: String,
    val durationMinutes: Int,
    val createdAt: Long
)

@Entity(tableName = "user_learning_progress", primaryKeys = ["userId", "lessonId"])
data class LearningProgressEntity(
    val userId: String,
    val lessonId: String,
    val completed: Boolean,
    val quizScore: Int,
    val updatedAt: Long
)

@Entity(tableName = "payment_transactions")
data class PaymentTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val planTier: String,
    val amountPkr: Int,
    val method: String,
    val referenceCode: String,
    val receiptUri: String?,
    val status: String,
    val rejectionReason: String?,
    val createdAt: Long,
    val reviewedAt: Long?
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userEmail: String,
    val subject: String,
    val message: String,
    val category: String,
    val status: String,
    val createdAt: Long
)

@Entity(tableName = "usage_records")
data class UsageRecordEntity(
    @PrimaryKey val userId: String,
    val transcriptionSecondsUsed: Int = 0,
    val aiRepliesUsed: Int = 0,
    val ttsCountUsed: Int = 0,
    val practiceMinutesUsed: Int = 0,
    val dailyStreak: Int = 1,
    val lastPracticeDay: Long = 0L
)

@Entity(tableName = "admin_settings")
data class AdminSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val monthlyPricePkr: Int = 2000,
    val yearlyPromoPricePkr: Int = 6000,
    val yearlyRegularPricePkr: Int = 8000,
    val freeTrialReplyLimit: Int = 25,
    val freeTrialSecondsLimit: Int = 600,
    val monthlyProReplyLimit: Int = 1500,
    val bankAccountTitle: String = "CallSathi Technologies (Pvt) Ltd",
    val bankName: String = "Meezan Bank Limited",
    val ibanOrAccount: String = "PK36MEZN0002810104928371",
    val jazzCashNumber: String = "0300-8451920",
    val easypaisaNumber: String = "0345-8451920",
    val raastId: String = "03008451920",
    val whatsappSupportNumber: String = "+923008451920"
)
