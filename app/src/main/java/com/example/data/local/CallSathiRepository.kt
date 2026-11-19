package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AccountStatus
import com.example.domain.model.PaymentMethodType
import com.example.domain.model.PaymentStatus
import com.example.domain.model.PlanTier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.sessionDataStore by preferencesDataStore(name = "callsathi_session")

class CallSathiRepository(
    private val context: Context,
    private val db: AppDatabase
) {
    private object Keys {
        val ACTIVE_USER_ID = stringPreferencesKey("active_user_id")
        val IS_DEMO_MODE = booleanPreferencesKey("is_demo_mode")
        val UI_LANGUAGE = stringPreferencesKey("ui_language")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val activeUserIdFlow: Flow<String?> = context.sessionDataStore.data.map { it[Keys.ACTIVE_USER_ID] }
    val isDemoModeFlow: Flow<Boolean> = context.sessionDataStore.data.map { it[Keys.IS_DEMO_MODE] ?: false }
    val uiLanguageFlow: Flow<String> = context.sessionDataStore.data.map { it[Keys.UI_LANGUAGE] ?: "URDU" }
    val darkModeFlow: Flow<Boolean> = context.sessionDataStore.data.map { it[Keys.DARK_MODE] ?: true }

    fun observeProfile(userId: String): Flow<UserProfileEntity?> = db.profileDao().observeProfile(userId)
    fun observeAllProfiles(): Flow<List<UserProfileEntity>> = db.profileDao().observeAllProfiles()
    fun observePreferences(userId: String): Flow<UserPreferencesEntity?> = db.preferencesDao().observePreferences(userId)
    fun observeRecentConversations(userId: String): Flow<List<ConversationSegmentEntity>> = db.conversationDao().observeRecentSegments(userId)
    fun observePracticeSessions(userId: String): Flow<List<PracticeSessionEntity>> = db.practiceDao().observeSessions(userId)
    fun observeLearningProgress(userId: String): Flow<List<LearningProgressEntity>> = db.learningDao().observeProgress(userId)
    fun observeUserTransactions(userId: String): Flow<List<PaymentTransactionEntity>> = db.paymentDao().observeUserTransactions(userId)
    fun observeAllTransactions(): Flow<List<PaymentTransactionEntity>> = db.paymentDao().observeAllTransactions()
    fun observeUserTickets(userId: String): Flow<List<SupportTicketEntity>> = db.supportDao().observeUserTickets(userId)
    fun observeAllTickets(): Flow<List<SupportTicketEntity>> = db.supportDao().observeAllTickets()
    fun observeUsage(userId: String): Flow<UsageRecordEntity?> = db.usageDao().observeUsage(userId)
    fun observeAdminSettings(): Flow<AdminSettingsEntity?> = db.adminSettingsDao().observeSettings()

    suspend fun setUiLanguage(language: String) {
        context.sessionDataStore.edit { it[Keys.UI_LANGUAGE] = language }
    }

    suspend fun setDarkMode(darkMode: Boolean) {
        context.sessionDataStore.edit { it[Keys.DARK_MODE] = darkMode }
    }

    suspend fun enterDemoMode() {
        val demoId = "demo_agent_pk"
        if (db.profileDao().getProfileById(demoId) == null) {
            val now = System.currentTimeMillis()
            db.profileDao().upsertProfile(
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
                    subscriptionExpiresAt = now + 7 * 86_400_000L,
                    createdAt = now
                )
            )
            db.preferencesDao().upsertPreferences(UserPreferencesEntity(userId = demoId))
            db.usageDao().upsertUsage(UsageRecordEntity(userId = demoId, dailyStreak = 4))
        }
        context.sessionDataStore.edit {
            it[Keys.ACTIVE_USER_ID] = demoId
            it[Keys.IS_DEMO_MODE] = true
        }
    }

    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        englishLevel: String,
        preferredLanguage: String
    ): Result<UserProfileEntity> {
        val trimmedEmail = email.trim().lowercase()
        if (fullName.isBlank() || trimmedEmail.isBlank() || password.length < 6) {
            return Result.failure(IllegalArgumentException("Please enter name, valid email, and password (min 6 chars)."))
        }
        val existing = db.profileDao().getProfileByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(IllegalStateException("An account with this email already exists."))
        }
        val now = System.currentTimeMillis()
        val userId = "usr_${UUID.randomUUID().toString().take(10)}"
        val profile = UserProfileEntity(
            id = userId,
            fullName = fullName.trim(),
            email = trimmedEmail,
            passwordHash = password,
            englishLevel = englishLevel,
            preferredLanguage = preferredLanguage,
            planTier = PlanTier.FREE_TRIAL.name,
            accountStatus = AccountStatus.ACTIVE.name,
            isAdmin = false,
            emailVerified = true,
            subscriptionExpiresAt = now + 7 * 86_400_000L,
            createdAt = now
        )
        db.profileDao().upsertProfile(profile)
        db.preferencesDao().upsertPreferences(
            UserPreferencesEntity(userId = userId, difficulty = englishLevel)
        )
        db.usageDao().upsertUsage(UsageRecordEntity(userId = userId, dailyStreak = 1))
        context.sessionDataStore.edit {
            it[Keys.ACTIVE_USER_ID] = userId
            it[Keys.IS_DEMO_MODE] = false
            it[Keys.UI_LANGUAGE] = preferredLanguage
        }
        return Result.success(profile)
    }

    suspend fun login(email: String, password: String): Result<UserProfileEntity> {
        val profile = db.profileDao().getProfileByEmail(email.trim().lowercase())
            ?: return Result.failure(IllegalArgumentException("Account not found for this email."))
        if (profile.passwordHash != password) {
            return Result.failure(IllegalArgumentException("Incorrect password."))
        }
        if (profile.accountStatus == AccountStatus.SUSPENDED.name) {
            return Result.failure(IllegalStateException("This account has been suspended. Please contact support."))
        }
        context.sessionDataStore.edit {
            it[Keys.ACTIVE_USER_ID] = profile.id
            it[Keys.IS_DEMO_MODE] = false
            it[Keys.UI_LANGUAGE] = profile.preferredLanguage
        }
        return Result.success(profile)
    }

    suspend fun loginAsAdminDemo(): Result<UserProfileEntity> {
        val admin = db.profileDao().getProfileById("admin_callsathi")
            ?: return Result.failure(IllegalStateException("Admin profile unavailable."))
        context.sessionDataStore.edit {
            it[Keys.ACTIVE_USER_ID] = admin.id
            it[Keys.IS_DEMO_MODE] = false
        }
        return Result.success(admin)
    }

    suspend fun logout() {
        context.sessionDataStore.edit {
            it.remove(Keys.ACTIVE_USER_ID)
            it[Keys.IS_DEMO_MODE] = false
        }
    }

    suspend fun savePreferences(prefs: UserPreferencesEntity) {
        db.preferencesDao().upsertPreferences(prefs)
    }

    suspend fun saveConversationSegment(segment: ConversationSegmentEntity) {
        db.conversationDao().insertSegment(segment)
    }

    suspend fun clearConversations(userId: String) {
        db.conversationDao().clearForUser(userId)
    }

    suspend fun incrementUsage(
        userId: String,
        addSeconds: Int = 0,
        addReplies: Int = 0,
        addTts: Int = 0,
        addPracticeMins: Int = 0
    ) {
        val current = db.usageDao().getUsage(userId) ?: UsageRecordEntity(userId = userId)
        val today = System.currentTimeMillis() / 86_400_000L
        val lastDay = current.lastPracticeDay / 86_400_000L
        val newStreak = if (addPracticeMins > 0) {
            when {
                lastDay == today -> current.dailyStreak
                lastDay == today - 1 -> current.dailyStreak + 1
                else -> 1
            }
        } else {
            current.dailyStreak
        }
        db.usageDao().upsertUsage(
            current.copy(
                transcriptionSecondsUsed = current.transcriptionSecondsUsed + addSeconds,
                aiRepliesUsed = current.aiRepliesUsed + addReplies,
                ttsCountUsed = current.ttsCountUsed + addTts,
                practiceMinutesUsed = current.practiceMinutesUsed + addPracticeMins,
                dailyStreak = newStreak,
                lastPracticeDay = if (addPracticeMins > 0) System.currentTimeMillis() else current.lastPracticeDay
            )
        )
    }

    suspend fun savePracticeSession(session: PracticeSessionEntity) {
        db.practiceDao().insertSession(session)
        incrementUsage(session.userId, addPracticeMins = session.durationMinutes)
    }

    suspend fun markLessonCompleted(userId: String, lessonId: String, quizScore: Int) {
        db.learningDao().upsertProgress(
            LearningProgressEntity(
                userId = userId,
                lessonId = lessonId,
                completed = true,
                quizScore = quizScore,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun submitManualPayment(
        user: UserProfileEntity,
        planTier: PlanTier,
        amountPkr: Int,
        method: PaymentMethodType,
        referenceCode: String,
        receiptUri: String?
    ): PaymentTransactionEntity {
        val tx = PaymentTransactionEntity(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            userId = user.id,
            userName = user.fullName,
            userEmail = user.email,
            planTier = planTier.name,
            amountPkr = amountPkr,
            method = method.name,
            referenceCode = referenceCode.trim(),
            receiptUri = receiptUri,
            status = PaymentStatus.PENDING_REVIEW.name,
            rejectionReason = null,
            createdAt = System.currentTimeMillis(),
            reviewedAt = null
        )
        db.paymentDao().upsertTransaction(tx)
        return tx
    }

    suspend fun approvePayment(tx: PaymentTransactionEntity) {
        val now = System.currentTimeMillis()
        val durationDays = if (tx.planTier == PlanTier.YEARLY_PRO.name) 365L else 30L
        db.paymentDao().upsertTransaction(
            tx.copy(
                status = PaymentStatus.APPROVED.name,
                rejectionReason = null,
                reviewedAt = now
            )
        )
        db.profileDao().updateSubscription(
            userId = tx.userId,
            planTier = tx.planTier,
            expiresAt = now + durationDays * 86_400_000L
        )
    }

    suspend fun rejectPayment(tx: PaymentTransactionEntity, reason: String) {
        db.paymentDao().upsertTransaction(
            tx.copy(
                status = PaymentStatus.REJECTED.name,
                rejectionReason = reason.ifBlank { "Receipt verification failed" },
                reviewedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleAccountStatus(profile: UserProfileEntity) {
        val next = if (profile.accountStatus == AccountStatus.ACTIVE.name) {
            AccountStatus.SUSPENDED.name
        } else {
            AccountStatus.ACTIVE.name
        }
        db.profileDao().updateAccountStatus(profile.id, next)
    }

    suspend fun updateAdminSettings(settings: AdminSettingsEntity) {
        db.adminSettingsDao().upsertSettings(settings)
    }

    suspend fun submitSupportTicket(
        userId: String,
        userEmail: String,
        subject: String,
        message: String,
        category: String
    ) {
        db.supportDao().upsertTicket(
            SupportTicketEntity(
                id = "tk_${UUID.randomUUID().toString().take(8)}",
                userId = userId,
                userEmail = userEmail,
                subject = subject.trim(),
                message = message.trim(),
                category = category,
                status = "OPEN",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun resolveSupportTicket(ticket: SupportTicketEntity) {
        db.supportDao().upsertTicket(ticket.copy(status = "RESOLVED"))
    }

    suspend fun deleteAccount(userId: String) {
        db.conversationDao().clearForUser(userId)
        db.practiceDao().deleteForUser(userId)
        db.learningDao().deleteForUser(userId)
        db.paymentDao().deleteForUser(userId)
        db.supportDao().deleteForUser(userId)
        db.usageDao().deleteForUser(userId)
        db.preferencesDao().deleteForUser(userId)
        db.profileDao().deleteProfile(userId)
        logout()
    }

    suspend fun currentLanguageSnapshot(): String = uiLanguageFlow.first()
}
