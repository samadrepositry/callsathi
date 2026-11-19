package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.local.AcademyContent
import com.example.data.local.PracticeSessionEntity
import com.example.data.local.UsageRecordEntity
import com.example.domain.model.PracticeScenario
import com.example.domain.model.UiLanguage
import com.example.ui.components.GradientButton
import com.example.ui.components.LtrBlock
import com.example.ui.components.SampleAiNoticeBanner
import com.example.ui.components.StatusPill
import com.example.ui.components.UrduText
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoyalPurple
import com.example.ui.viewmodel.PracticeUiState
import com.example.util.VoiceAssistManager
import com.example.util.stringsFor

@Composable
fun PracticeScreen(
    language: UiLanguage,
    uiState: PracticeUiState,
    usage: UsageRecordEntity,
    pastSessions: List<PracticeSessionEntity>,
    voiceAssistManager: VoiceAssistManager,
    isPlaceholderAi: Boolean,
    onStartScenario: (PracticeScenario) -> Unit,
    onUpdateDraft: (String) -> Unit,
    onSubmitReply: (String) -> Unit,
    onFinishEarly: () -> Unit,
    onExitScenario: () -> Unit,
    onSpeakCustomer: (String) -> Unit
) {
    val copy = stringsFor(language)
    val context = LocalContext.current

    val micLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            voiceAssistManager.startListening(
                onFinalResult = { spoken -> onUpdateDraft(spoken) },
                onErrorMessage = {}
            )
        }
    }

    if (uiState.activeScenario != null) {
        BackHandler { onExitScenario() }
        val scenario = uiState.activeScenario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onExitScenario) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back to scenarios")
                        }
                        Column {
                            Text(scenario.titleEn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${scenario.region} • ${scenario.difficulty.labelEn}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    if (uiState.evaluation == null) {
                        OutlinedButton(onClick = onFinishEarly) {
                            Text(if (language == UiLanguage.URDU) "اسکور دیکھیں" else "End & Score")
                        }
                    }
                }

                if (isPlaceholderAi) {
                    SampleAiNoticeBanner(noticeText = copy.sampleAiNotice)
                }

                // Turn History
                uiState.turns.forEach { turn ->
                    val isCust = turn.speakerIsCustomer
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCust) MaterialTheme.colorScheme.surface else ElectricBlue.copy(alpha = 0.14f)
                        ),
                        border = BorderStroke(1.dp, if (isCust) MaterialTheme.colorScheme.outline else ElectricBlue.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusPill(
                                    label = if (isCust) "AI Customer (${scenario.region})" else "Your Reply",
                                    color = if (isCust) RoyalPurple else ElectricBlue
                                )
                                if (isCust) {
                                    IconButton(onClick = { onSpeakCustomer(turn.englishText) }) {
                                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "Hear AI customer")
                                    }
                                }
                            }
                            LtrBlock {
                                Text(turn.englishText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            }
                            if (turn.urduHint.isNotBlank()) {
                                UrduText(
                                    text = turn.urduHint,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (uiState.isAiThinking) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                val evaluation = uiState.evaluation
                if (evaluation != null) {
                    // End-of-Session Scorecard
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, EmeraldSuccess)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Practice Scorecard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                StatusPill(label = "Estimated AI Score", color = AmberWarning)
                            }
                            Text(
                                text = "Note: Scores are estimated AI practice metrics for self-improvement, not a formal language certification.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            ScoreBar("Speaking Clarity", evaluation.speakingScore)
                            ScoreBar("Grammar", evaluation.grammarScore)
                            ScoreBar("Response Quality", evaluation.responseQualityScore)
                            ScoreBar("Confidence", evaluation.confidenceScore)

                            Text(evaluation.summaryEn, style = MaterialTheme.typography.bodyMedium)
                            UrduText(evaluation.summaryUr, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)

                            Text("Mistakes & Corrections:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            evaluation.mistakesAndCorrections.forEach { item ->
                                Text("• $item", style = MaterialTheme.typography.bodySmall)
                            }

                            Text("Better Alternative Sentences:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            evaluation.betterAlternatives.forEach { alt ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = alt,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            GradientButton(
                                text = if (language == UiLanguage.URDU) "نیا سیناریو منتخب کریں" else "Back to Scenarios",
                                onClick = onExitScenario,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    // Hint + Reply Input
                    uiState.currentCustomerPrompt?.suggestedHintEn?.let { hint ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Suggested Helper Phrase (Tap to use):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = hint,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clickable { onUpdateDraft(hint) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.userDraftReply,
                        onValueChange = onUpdateDraft,
                        label = { Text(if (language == UiLanguage.URDU) "انگلش میں جواب لکھیں یا مائیک سے بولیں" else "Speak or type your English reply") },
                        modifier = Modifier.fillMaxWidth().testTag("practice_reply_input"),
                        trailingIcon = {
                            IconButton(onClick = {
                                val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                                if (granted) {
                                    voiceAssistManager.startListening(
                                        onFinalResult = { spoken -> onUpdateDraft(spoken) },
                                        onErrorMessage = {}
                                    )
                                } else {
                                    micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }) {
                                Icon(Icons.Rounded.Mic, contentDescription = "Speak reply")
                            }
                        }
                    )

                    GradientButton(
                        text = if (language == UiLanguage.URDU) "جواب بھیجیں" else "Send Reply",
                        enabled = uiState.userDraftReply.isNotBlank() && !uiState.isAiThinking,
                        onClick = { onSubmitReply(uiState.userDraftReply) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "practice_send_button",
                        leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White) }
                    )
                }
            }
        }
        return
    }

    // Scenario Selection List
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (isPlaceholderAi) {
                SampleAiNoticeBanner(noticeText = copy.sampleAiNotice)
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalPurple.copy(alpha = 0.14f)),
                border = BorderStroke(1.dp, RoyalPurple.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(copy.practiceTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        UrduText(
                            text = if (language == UiLanguage.URDU) "عالمی کسٹمرز کے ساتھ اصلی کال سینٹر سیناریوز کی مشق کریں"
                            else "Practice real international call scenarios with AI customers",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = AmberWarning)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${usage.dailyStreak}d", fontWeight = FontWeight.Bold)
                    }
                }
            }

            AcademyContent.practiceScenarios.forEach { scenario ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartScenario(scenario) }
                        .testTag("scenario_${scenario.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(scenario.titleEn, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            UrduText(scenario.titleUr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusPill(label = scenario.region, color = ElectricBlue)
                    }
                }
            }

            if (pastSessions.isNotEmpty()) {
                Text(
                    text = if (language == UiLanguage.URDU) "سابقہ پریکٹس اسکورز" else "Past Practice Scores",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                pastSessions.take(5).forEach { s ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(s.scenarioTitle, style = MaterialTheme.typography.titleSmall)
                                Text("Speaking ${s.speakingScore}% • Grammar ${s.grammarScore}%", style = MaterialTheme.typography.labelSmall)
                            }
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreBar(label: String, score: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text("$score%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { (score / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp),
            color = if (score >= 80) EmeraldSuccess else ElectricBlue
        )
    }
}
