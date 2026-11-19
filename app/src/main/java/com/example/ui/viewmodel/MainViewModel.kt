package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiRepository
import com.example.data.ai.LiveTranscriptSegment
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.CallSathiRepository
import com.example.data.local.ConversationSegmentEntity
import com.example.data.local.LearningProgressEntity
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.PracticeSessionEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.local.UsageRecordEntity
import com.example.data.local.UserPreferencesEntity
import com.example.data.local.UserProfileEntity
import com.example.domain.model.AccentPreference
import com.example.domain.model.AssistantDeviceMode
import com.example.domain.model.CallMode
import com.example.domain.model.EnglishLevel
import com.example.domain.model.ListeningStatus
import com.example.domain.model.PaymentMethodType
import com.example.domain.model.PlanTier
import com.example.domain.model.PracticeCustomerPrompt
import com.example.domain.model.PracticeEvaluation
import com.example.domain.model.PracticeScenario
import com.example.domain.model.PracticeTurn
import com.example.domain.model.ReplyRefinementAction
import com.example.domain.model.ReplyStyle
import com.example.domain.model.TranslationAndRepliesResult
import com.example.domain.model.UiLanguage
import com.example.util.VoiceAssistManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AuthFormState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val statusBanner: String? = null
)

data class LiveAssistantUiState(
    val listeningStatus: ListeningStatus = ListeningStatus.IDLE,
    val deviceMode: AssistantDeviceMode = AssistantDeviceMode.MOBILE_SECOND_DEVICE,
    val callMode: CallMode = CallMode.CUSTOMER_SUPPORT,
    val privacyAccepted: Boolean = false,
    val manualInputText: String = "",
    val isGenerating: Boolean = false,
    val currentResult: TranslationAndRepliesResult? = null,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val sessionSegments: List<LiveTranscriptSegment> = emptyList()
)

data class PracticeUiState(
    val activeScenario: PracticeScenario? = null,
    val turns: List<PracticeTurn> = emptyList(),
    val currentCustomerPrompt: PracticeCustomerPrompt? = null,
    val userDraftReply: String = "",
    val isAiThinking: Boolean = false,
    val evaluation: PracticeEvaluation? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: CallSathiRepository,
    private val aiRepository: AiRepository,
    val voiceAssistManager: VoiceAssistManager
) : ViewModel() {

    val isPlaceholderAi: Boolean = aiRepository.isPlaceholderMode

    val activeUserId: StateFlow<String?> = repository.activeUserIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isDemoMode: StateFlow<Boolean> = repository.isDemoModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val uiLanguage: StateFlow<UiLanguage> = repository.uiLanguageFlow
        .combine(flowOf(Unit)) { code, _ ->
            runCatching { UiLanguage.valueOf(code) }.getOrDefault(UiLanguage.URDU)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiLanguage.URDU)

    val darkMode: StateFlow<Boolean> = repository.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val currentProfile: StateFlow<UserProfileEntity?> = activeUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(null) else repository.observeProfile(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userPreferences: StateFlow<UserPreferencesEntity> = activeUserId
        .flatMapLatest { uid ->
            if (uid == null) flowOf(UserPreferencesEntity("guest"))
            else repository.observePreferences(uid).combine(flowOf(uid)) { prefs, id ->
                prefs ?: UserPreferencesEntity(userId = id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesEntity("guest"))

    val usageRecord: StateFlow<UsageRecordEntity> = activeUserId
        .flatMapLatest { uid ->
            if (uid == null) flowOf(UsageRecordEntity("guest"))
            else repository.observeUsage(uid).combine(flowOf(uid)) { u, id ->
                u ?: UsageRecordEntity(userId = id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UsageRecordEntity("guest"))

    val savedConversations: StateFlow<List<ConversationSegmentEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repository.observeRecentConversations(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val practiceHistory: StateFlow<List<PracticeSessionEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repository.observePracticeSessions(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val learningProgress: StateFlow<List<LearningProgressEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repository.observeLearningProgress(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<PaymentTransactionEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repository.observeUserTransactions(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTickets: StateFlow<List<SupportTicketEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repository.observeUserTickets(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminSettings: StateFlow<AdminSettingsEntity> = repository.observeAdminSettings()
        .combine(flowOf(Unit)) { settings, _ -> settings ?: AdminSettingsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminSettingsEntity())

    val allProfilesForAdmin: StateFlow<List<UserProfileEntity>> = repository.observeAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactionsForAdmin: StateFlow<List<PaymentTransactionEntity>> = repository.observeAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTicketsForAdmin: StateFlow<List<SupportTicketEntity>> = repository.observeAllTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _authState = MutableStateFlow(AuthFormState())
    val authState: StateFlow<AuthFormState> = _authState.asStateFlow()

    private val _liveState = MutableStateFlow(LiveAssistantUiState())
    val liveState: StateFlow<LiveAssistantUiState> = _liveState.asStateFlow()

    private val _practiceState = MutableStateFlow(PracticeUiState())
    val practiceState: StateFlow<PracticeUiState> = _practiceState.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun consumeToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun toggleUiLanguage() {
        viewModelScope.launch {
            val next = if (uiLanguage.value == UiLanguage.URDU) UiLanguage.ENGLISH else UiLanguage.URDU
            repository.setUiLanguage(next.name)
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val next = !darkMode.value
            repository.setDarkMode(next)
            val uid = activeUserId.value
            if (uid != null) {
                repository.savePreferences(userPreferences.value.copy(userId = uid, darkMode = next))
            }
        }
    }

    // --- Auth & Onboarding ---

    fun enterDemoMode() {
        viewModelScope.launch {
            _authState.value = AuthFormState(isLoading = true)
            repository.enterDemoMode()
            _authState.value = AuthFormState()
        }
    }

    fun signUp(
        fullName: String,
        email: String,
        password: String,
        level: EnglishLevel,
        preferredLanguage: UiLanguage
    ) {
        viewModelScope.launch {
            _authState.value = AuthFormState(isLoading = true)
            val result = repository.signUp(
                fullName = fullName,
                email = email,
                password = password,
                englishLevel = level.name,
                preferredLanguage = preferredLanguage.name
            )
            _authState.value = result.fold(
                onSuccess = { AuthFormState() },
                onFailure = { AuthFormState(errorMessage = it.message ?: "Sign up failed") }
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthFormState(isLoading = true)
            val result = repository.login(email, password)
            _authState.value = result.fold(
                onSuccess = { AuthFormState() },
                onFailure = { AuthFormState(errorMessage = it.message ?: "Login failed") }
            )
        }
    }

    fun loginAsAdminDemo() {
        viewModelScope.launch {
            _authState.value = AuthFormState(isLoading = true)
            val result = repository.loginAsAdminDemo()
            _authState.value = result.fold(
                onSuccess = { AuthFormState() },
                onFailure = { AuthFormState(errorMessage = it.message ?: "Admin login failed") }
            )
        }
    }

    fun requestPasswordReset(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthFormState(errorMessage = "Please enter your email address first.")
        } else {
            _authState.value = AuthFormState(
                statusBanner = "Password reset instructions prepared for $email (Local demo mode)."
            )
        }
    }

    fun clearAuthMessages() {
        _authState.value = AuthFormState()
    }

    fun logout() {
        voiceAssistManager.stopListening()
        voiceAssistManager.stopSpeaking()
        viewModelScope.launch {
            repository.logout()
            _liveState.value = LiveAssistantUiState()
            _practiceState.value = PracticeUiState()
        }
    }

    fun deleteCurrentAccount() {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.deleteAccount(uid)
            _toastMessage.value = "Account deleted."
        }
    }

    // --- Live Call Assistant ---

    fun acceptAudioPrivacy() {
        _liveState.update { it.copy(privacyAccepted = true, errorMessage = null) }
    }

    fun setDeviceMode(mode: AssistantDeviceMode) {
        _liveState.update { it.copy(deviceMode = mode) }
    }

    fun setCallMode(mode: CallMode) {
        _liveState.update { it.copy(callMode = mode) }
    }

    fun updateManualInput(text: String) {
        _liveState.update { it.copy(manualInputText = text) }
    }

    fun updateHistorySearch(query: String) {
        _liveState.update { it.copy(searchQuery = query) }
    }

    fun startMicrophoneListening() {
        if (!_liveState.value.privacyAccepted) {
            _liveState.update { it.copy(errorMessage = "Please review and accept the Audio Privacy Notice before recording.") }
            return
        }
        val localeTag = runCatching {
            AccentPreference.valueOf(userPreferences.value.accent).localeTag
        }.getOrDefault("en-US")

        _liveState.update { it.copy(listeningStatus = ListeningStatus.LISTENING, errorMessage = null) }
        voiceAssistManager.startListening(
            localeTag = localeTag,
            onFinalResult = { spokenEnglish ->
                _liveState.update { it.copy(listeningStatus = ListeningStatus.IDLE, manualInputText = spokenEnglish) }
                processCustomerUtterance(spokenEnglish, estimatedSeconds = 8)
            },
            onErrorMessage = { err ->
                _liveState.update { it.copy(listeningStatus = ListeningStatus.IDLE, errorMessage = err) }
            }
        )
    }

    fun pauseListening() {
        voiceAssistManager.stopListening()
        _liveState.update { it.copy(listeningStatus = ListeningStatus.PAUSED) }
    }

    fun resumeListening() {
        startMicrophoneListening()
    }

    fun stopListening() {
        voiceAssistManager.stopListening()
        _liveState.update { it.copy(listeningStatus = ListeningStatus.IDLE) }
    }

    fun processCustomerUtterance(customerEnglish: String, estimatedSeconds: Int = 5) {
        val cleaned = customerEnglish.trim()
        if (cleaned.isBlank()) return
        val profile = currentProfile.value
        val settings = adminSettings.value
        val usage = usageRecord.value

        if (profile?.planTier == PlanTier.FREE_TRIAL.name &&
            usage.aiRepliesUsed >= settings.freeTrialReplyLimit
        ) {
            _liveState.update {
                it.copy(
                    errorMessage = "Free Trial AI reply limit reached (${settings.freeTrialReplyLimit}). Please upgrade to Monthly Pro (Rs. ${settings.monthlyPricePkr}) in Account > Plans."
                )
            }
            return
        }

        viewModelScope.launch {
            _liveState.update {
                it.copy(
                    isGenerating = true,
                    listeningStatus = ListeningStatus.PROCESSING,
                    errorMessage = null
                )
            }
            val result = aiRepository.translateAndGenerateReplies(
                customerEnglish = cleaned,
                mode = _liveState.value.callMode,
                recentHistory = _liveState.value.sessionSegments,
                preferences = userPreferences.value
            )
            val segmentId = "seg_${UUID.randomUUID().toString().take(8)}"
            val newSegment = LiveTranscriptSegment(
                id = segmentId,
                customerEnglish = result.customerEnglish,
                customerUrdu = result.customerUrdu,
                customerRomanUrdu = result.customerRomanUrdu,
                intentLabel = result.intent.labelEn,
                chosenReply = result.replies.firstOrNull { it.style == ReplyStyle.PROFESSIONAL }?.englishText
                    ?: result.replies.firstOrNull()?.englishText.orEmpty()
            )
            val updatedSessionList = (listOf(newSegment) + _liveState.value.sessionSegments).take(20)

            _liveState.update {
                it.copy(
                    isGenerating = false,
                    listeningStatus = ListeningStatus.IDLE,
                    currentResult = result,
                    sessionSegments = updatedSessionList
                )
            }

            val uid = activeUserId.value
            if (uid != null) {
                repository.incrementUsage(uid, addSeconds = estimatedSeconds, addReplies = 1)
                if (userPreferences.value.saveTranscriptsByDefault) {
                    saveSegmentToDatabase(uid, result)
                }
            }
        }
    }

    fun saveCurrentSegmentExplicitly() {
        val uid = activeUserId.value ?: return
        val result = _liveState.value.currentResult ?: return
        viewModelScope.launch {
            saveSegmentToDatabase(uid, result)
            _toastMessage.value = "Transcript segment saved."
        }
    }

    private suspend fun saveSegmentToDatabase(uid: String, result: TranslationAndRepliesResult) {
        val simple = result.replies.firstOrNull { it.style == ReplyStyle.SIMPLE }
        val pro = result.replies.firstOrNull { it.style == ReplyStyle.PROFESSIONAL }
        val friendly = result.replies.firstOrNull { it.style == ReplyStyle.FRIENDLY }
        repository.saveConversationSegment(
            ConversationSegmentEntity(
                id = "conv_${UUID.randomUUID().toString().take(10)}",
                userId = uid,
                callMode = _liveState.value.callMode.name,
                customerEnglish = result.customerEnglish,
                customerUrdu = result.customerUrdu,
                customerRomanUrdu = result.customerRomanUrdu,
                intent = result.intent.name,
                simpleReply = simple?.englishText.orEmpty(),
                simpleReplyUrdu = simple?.urduMeaning.orEmpty(),
                professionalReply = pro?.englishText.orEmpty(),
                professionalReplyUrdu = pro?.urduMeaning.orEmpty(),
                friendlyReply = friendly?.englishText.orEmpty(),
                friendlyReplyUrdu = friendly?.urduMeaning.orEmpty(),
                pronunciationTip = pro?.pronunciationGuide.orEmpty(),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    fun refineReply(style: ReplyStyle, action: ReplyRefinementAction) {
        val current = _liveState.value.currentResult ?: return
        val target = current.replies.firstOrNull { it.style == style } ?: return
        viewModelScope.launch {
            val updatedReply = aiRepository.refineReply(target, action, userPreferences.value)
            val newReplies = current.replies.map { if (it.style == style) updatedReply else it }
            _liveState.update { it.copy(currentResult = current.copy(replies = newReplies)) }
            activeUserId.value?.let { repository.incrementUsage(it, addReplies = 1) }
        }
    }

    fun clearSessionHistory() {
        _liveState.update { it.copy(sessionSegments = emptyList(), currentResult = null) }
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.clearConversations(uid)
            _toastMessage.value = "Conversation history cleared."
        }
    }

    fun speakReplyExplicitly(englishText: String, slow: Boolean) {
        val localeTag = runCatching {
            AccentPreference.valueOf(userPreferences.value.accent).localeTag
        }.getOrDefault("en-US")
        voiceAssistManager.speakEnglish(englishText, localeTag = localeTag, slow = slow)
        activeUserId.value?.let { uid ->
            viewModelScope.launch { repository.incrementUsage(uid, addTts = 1) }
        }
    }

    // --- Practice English Simulator ---

    fun startPracticeScenario(scenario: PracticeScenario) {
        viewModelScope.launch {
            _practiceState.value = PracticeUiState(activeScenario = scenario, isAiThinking = true)
            val firstPrompt = aiRepository.nextPracticeCustomerTurn(scenario, emptyList(), userPreferences.value)
            _practiceState.value = PracticeUiState(
                activeScenario = scenario,
                turns = listOf(PracticeTurn(speakerIsCustomer = true, englishText = firstPrompt.englishLine, urduHint = firstPrompt.urduTranslation)),
                currentCustomerPrompt = firstPrompt,
                isAiThinking = false
            )
        }
    }

    fun updatePracticeDraft(text: String) {
        _practiceState.update { it.copy(userDraftReply = text) }
    }

    fun submitPracticeReply(replyText: String) {
        val state = _practiceState.value
        val scenario = state.activeScenario ?: return
        val cleaned = replyText.trim()
        if (cleaned.isBlank()) return

        val updatedTurns = state.turns + PracticeTurn(speakerIsCustomer = false, englishText = cleaned)
        viewModelScope.launch {
            _practiceState.update {
                it.copy(turns = updatedTurns, userDraftReply = "", isAiThinking = true)
            }
            val nextCustomer = aiRepository.nextPracticeCustomerTurn(scenario, updatedTurns, userPreferences.value)
            val allTurns = updatedTurns + PracticeTurn(
                speakerIsCustomer = true,
                englishText = nextCustomer.englishLine,
                urduHint = nextCustomer.urduTranslation
            )
            if (nextCustomer.isSessionComplete) {
                val evaluation = aiRepository.evaluatePracticeSession(scenario, allTurns, userPreferences.value)
                _practiceState.update {
                    it.copy(
                        turns = allTurns,
                        currentCustomerPrompt = nextCustomer,
                        isAiThinking = false,
                        evaluation = evaluation
                    )
                }
                persistPracticeSession(scenario, evaluation)
            } else {
                _practiceState.update {
                    it.copy(turns = allTurns, currentCustomerPrompt = nextCustomer, isAiThinking = false)
                }
            }
        }
    }

    fun finishPracticeEarly() {
        val state = _practiceState.value
        val scenario = state.activeScenario ?: return
        viewModelScope.launch {
            _practiceState.update { it.copy(isAiThinking = true) }
            val evaluation = aiRepository.evaluatePracticeSession(scenario, state.turns, userPreferences.value)
            _practiceState.update { it.copy(isAiThinking = false, evaluation = evaluation) }
            persistPracticeSession(scenario, evaluation)
        }
    }

    fun exitPracticeScenario() {
        _practiceState.value = PracticeUiState()
    }

    private suspend fun persistPracticeSession(scenario: PracticeScenario, evaluation: PracticeEvaluation) {
        val uid = activeUserId.value ?: return
        repository.savePracticeSession(
            PracticeSessionEntity(
                id = "prac_${UUID.randomUUID().toString().take(8)}",
                userId = uid,
                scenarioTitle = scenario.titleEn,
                speakingScore = evaluation.speakingScore,
                grammarScore = evaluation.grammarScore,
                responseQualityScore = evaluation.responseQualityScore,
                confidenceScore = evaluation.confidenceScore,
                feedbackSummary = evaluation.summaryEn,
                durationMinutes = 4,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    // --- Learning Academy ---

    fun completeLessonQuiz(lessonId: String, scorePercent: Int) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.markLessonCompleted(uid, lessonId, scorePercent)
            _toastMessage.value = "Lesson completed! Quiz score: $scorePercent%"
        }
    }

    // --- Settings, Payments, Admin, Support ---

    fun updatePreferences(updated: UserPreferencesEntity) {
        val uid = activeUserId.value ?: return
        viewModelScope.launch {
            repository.savePreferences(updated.copy(userId = uid))
            repository.setDarkMode(updated.darkMode)
            _toastMessage.value = "Preferences saved."
        }
    }

    fun submitPaymentRequest(
        planTier: PlanTier,
        amountPkr: Int,
        method: PaymentMethodType,
        referenceCode: String,
        receiptUri: String?
    ) {
        val profile = currentProfile.value ?: return
        if (referenceCode.isBlank()) {
            _toastMessage.value = "Please enter your Transaction / Reference ID."
            return
        }
        viewModelScope.launch {
            repository.submitManualPayment(profile, planTier, amountPkr, method, referenceCode, receiptUri)
            _toastMessage.value = "Payment submitted for admin verification. Paid access activates only after review."
        }
    }

    fun approvePayment(tx: PaymentTransactionEntity) {
        viewModelScope.launch {
            repository.approvePayment(tx)
            _toastMessage.value = "Approved ${tx.userName}'s ${tx.planTier} subscription."
        }
    }

    fun rejectPayment(tx: PaymentTransactionEntity, reason: String) {
        viewModelScope.launch {
            repository.rejectPayment(tx, reason)
            _toastMessage.value = "Payment rejected."
        }
    }

    fun toggleUserAccountStatus(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.toggleAccountStatus(profile)
        }
    }

    fun saveAdminSettings(settings: AdminSettingsEntity) {
        viewModelScope.launch {
            repository.updateAdminSettings(settings)
            _toastMessage.value = "Admin pricing & settings updated."
        }
    }

    fun submitSupportTicket(subject: String, message: String, category: String) {
        val profile = currentProfile.value ?: return
        if (subject.isBlank() || message.isBlank()) {
            _toastMessage.value = "Please enter subject and details."
            return
        }
        viewModelScope.launch {
            repository.submitSupportTicket(profile.id, profile.email, subject, message, category)
            _toastMessage.value = "Support ticket submitted."
        }
    }

    fun resolveTicket(ticket: SupportTicketEntity) {
        viewModelScope.launch {
            repository.resolveSupportTicket(ticket)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceAssistManager.release()
    }

    class Factory(
        private val repository: CallSathiRepository,
        private val aiRepository: AiRepository,
        private val voiceAssistManager: VoiceAssistManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository, aiRepository, voiceAssistManager) as T
        }
    }
}
