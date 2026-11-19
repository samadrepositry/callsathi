package com.example.data.ai

import com.example.data.local.UserPreferencesEntity
import com.example.domain.model.CallMode
import com.example.domain.model.PracticeCustomerPrompt
import com.example.domain.model.PracticeEvaluation
import com.example.domain.model.PracticeScenario
import com.example.domain.model.PracticeTurn
import com.example.domain.model.ReplyRefinementAction
import com.example.domain.model.SuggestedReply
import com.example.domain.model.TranslationAndRepliesResult

data class LiveTranscriptSegment(
    val id: String,
    val customerEnglish: String,
    val customerUrdu: String,
    val customerRomanUrdu: String,
    val intentLabel: String,
    val chosenReply: String
)

interface AiRepository {
    val isPlaceholderMode: Boolean

    suspend fun translateAndGenerateReplies(
        customerEnglish: String,
        mode: CallMode,
        recentHistory: List<LiveTranscriptSegment>,
        preferences: UserPreferencesEntity
    ): TranslationAndRepliesResult

    suspend fun refineReply(
        baseReply: SuggestedReply,
        action: ReplyRefinementAction,
        preferences: UserPreferencesEntity
    ): SuggestedReply

    suspend fun nextPracticeCustomerTurn(
        scenario: PracticeScenario,
        turns: List<PracticeTurn>,
        preferences: UserPreferencesEntity
    ): PracticeCustomerPrompt

    suspend fun evaluatePracticeSession(
        scenario: PracticeScenario,
        turns: List<PracticeTurn>,
        preferences: UserPreferencesEntity
    ): PracticeEvaluation
}
