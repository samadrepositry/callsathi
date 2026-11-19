package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY createdAt DESC")
    fun observeAllProfiles(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    fun observeProfile(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM profiles WHERE email = :email LIMIT 1")
    suspend fun getProfileByEmail(email: String): UserProfileEntity?

    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    suspend fun getProfileById(userId: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)

    @Query("UPDATE profiles SET planTier = :planTier, subscriptionExpiresAt = :expiresAt WHERE id = :userId")
    suspend fun updateSubscription(userId: String, planTier: String, expiresAt: Long?)

    @Query("UPDATE profiles SET accountStatus = :status WHERE id = :userId")
    suspend fun updateAccountStatus(userId: String, status: String)

    @Query("DELETE FROM profiles WHERE id = :userId")
    suspend fun deleteProfile(userId: String)
}

@Dao
interface PreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1")
    fun observePreferences(userId: String): Flow<UserPreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPreferences(prefs: UserPreferencesEntity)

    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation_segments WHERE userId = :userId ORDER BY createdAt DESC LIMIT 20")
    fun observeRecentSegments(userId: String): Flow<List<ConversationSegmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegment(segment: ConversationSegmentEntity)

    @Query("DELETE FROM conversation_segments WHERE userId = :userId")
    suspend fun clearForUser(userId: String)
}

@Dao
interface PracticeDao {
    @Query("SELECT * FROM practice_sessions WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeSessions(userId: String): Flow<List<PracticeSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSessionEntity)

    @Query("DELETE FROM practice_sessions WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface LearningDao {
    @Query("SELECT * FROM user_learning_progress WHERE userId = :userId")
    fun observeProgress(userId: String): Flow<List<LearningProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: LearningProgressEntity)

    @Query("DELETE FROM user_learning_progress WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserTransactions(userId: String): Flow<List<PaymentTransactionEntity>>

    @Query("SELECT * FROM payment_transactions ORDER BY createdAt DESC")
    fun observeAllTransactions(): Flow<List<PaymentTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransaction(tx: PaymentTransactionEntity)

    @Query("DELETE FROM payment_transactions WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface SupportDao {
    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserTickets(userId: String): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun observeAllTickets(): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTicket(ticket: SupportTicketEntity)

    @Query("DELETE FROM support_tickets WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface UsageDao {
    @Query("SELECT * FROM usage_records WHERE userId = :userId LIMIT 1")
    fun observeUsage(userId: String): Flow<UsageRecordEntity?>

    @Query("SELECT * FROM usage_records WHERE userId = :userId LIMIT 1")
    suspend fun getUsage(userId: String): UsageRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUsage(usage: UsageRecordEntity)

    @Query("DELETE FROM usage_records WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface AdminSettingsDao {
    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<AdminSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: AdminSettingsEntity)
}
