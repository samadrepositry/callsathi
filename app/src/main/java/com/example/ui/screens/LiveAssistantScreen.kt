package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShortText
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.ai.LiveTranscriptSegment
import com.example.data.local.AcademyContent
import com.example.data.local.UserPreferencesEntity
import com.example.domain.model.AssistantDeviceMode
import com.example.domain.model.CallMode
import com.example.domain.model.ListeningStatus
import com.example.domain.model.ReplyRefinementAction
import com.example.domain.model.ReplyStyle
import com.example.domain.model.SuggestedReply
import com.example.domain.model.UiLanguage
import com.example.domain.model.UrduScriptType
import com.example.ui.components.AnimatedMicButton
import com.example.ui.components.AudioPrivacyConsentDialog
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.LtrBlock
import com.example.ui.components.SampleAiNoticeBanner
import com.example.ui.components.StatusPill
import com.example.ui.components.UrduText
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.ui.theme.RoyalPurple
import com.example.ui.viewmodel.LiveAssistantUiState
import com.example.util.stringsFor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveAssistantScreen(
    language: UiLanguage,
    uiState: LiveAssistantUiState,
    preferences: UserPreferencesEntity,
    rmsLevel: Float,
    partialSpeech: String,
    isPlaceholderAi: Boolean,
    onAcceptPrivacy: () -> Unit,
    onSetDeviceMode: (AssistantDeviceMode) -> Unit,
    onSetCallMode: (CallMode) -> Unit,
    onStartMic: () -> Unit,
    onPauseMic: () -> Unit,
    onResumeMic: () -> Unit,
    onStopMic: () -> Unit,
    onManualInputChange: (String) -> Unit,
    onTranslateSubmit: (String) -> Unit,
    onRefineReply: (ReplyStyle, ReplyRefinementAction) -> Unit,
    onSpeakReply: (String, Boolean) -> Unit,
    onSaveSegment: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearHistory: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val copy = stringsFor(language)
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var permissionDeniedNote by remember { mutableStateOf(false) }
    val useRomanUrdu = preferences.urduScript == UrduScriptType.ROMAN_URDU.name

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            permissionDeniedNote = false
            onStartMic()
        } else {
            permissionDeniedNote = true
            onShowToast(
                if (language == UiLanguage.URDU) "مائیکروفون کی اجازت مسترد کر دی گئی۔ نیچے ٹائپ یا سیمپل استعمال کریں۔"
                else "Microphone permission denied. You can type or use sample phrases below."
            )
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val sampleFromFile = "Hello, I am calling regarding my monthly invoice and need clarification on the service fee."
            onManualInputChange(sampleFromFile)
            onTranslateSubmit(sampleFromFile)
            onShowToast(
                if (language == UiLanguage.URDU) "آڈیو فائل منتخب ہو گئی — سیمپل ٹرانسکرپشن چلا دی گئی ہے۔"
                else "Audio clip selected — processed sample transcript for review."
            )
        }
    }

    fun triggerMicStartWithChecks() {
        if (!uiState.privacyAccepted) {
            showPrivacyDialog = true
            return
        }
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            onStartMic()
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

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

            // Header & Agent-Assist Rule Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ElectricBlue.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = copy.privacyNoticeShort,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Two Audio Modes Switch (Mobile Second-Device vs Desktop Workflow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.deviceMode == AssistantDeviceMode.MOBILE_SECOND_DEVICE,
                    onClick = { onSetDeviceMode(AssistantDeviceMode.MOBILE_SECOND_DEVICE) },
                    leadingIcon = { Icon(Icons.Rounded.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text(if (language == UiLanguage.URDU) "موبائل اسپیکر موڈ" else "Mobile Second-Device") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = uiState.deviceMode == AssistantDeviceMode.DESKTOP_CALL_WORKFLOW,
                    onClick = { onSetDeviceMode(AssistantDeviceMode.DESKTOP_CALL_WORKFLOW) },
                    leadingIcon = { Icon(Icons.Rounded.DesktopWindows, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text(if (language == UiLanguage.URDU) "ڈیسک ٹاپ / ہیڈسیٹ" else "Desktop / Headset") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = if (uiState.deviceMode == AssistantDeviceMode.MOBILE_SECOND_DEVICE) {
                    if (language == UiLanguage.URDU) {
                        "موبائل OS کی پابندیوں کی وجہ سے فون کال کی اندرونی آواز براہِ راست نہیں لی جا سکتی۔ کسٹمر کال کو دوسرے فون/کمپیوٹر اسپیکر پر رکھیں اور مائیک شروع کریں۔"
                    } else {
                        "Mobile OS Limitation: Android restricts intercepting internal phone-call audio. Place your call on speaker or a second device near your mic."
                    }
                } else {
                    if (language == UiLanguage.URDU) {
                        "ڈیسک ٹاپ سیٹ اپ: USB ہیڈسیٹ یا براؤزر ٹیب/سسٹم آڈیو کیپچر کے ساتھ لائیو کال سنیں — آپ خود کسٹمر سے بولتے ہیں۔"
                    } else {
                        "Desktop Call Setup: Pair with a USB headset or supported desktop tab/system audio workflow. You always speak to the customer personally."
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Call Context Mode Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CallMode.entries.forEach { mode ->
                    FilterChip(
                        selected = uiState.callMode == mode,
                        onClick = { onSetCallMode(mode) },
                        label = { Text(if (language == UiLanguage.URDU) mode.labelUr else mode.labelEn) }
                    )
                }
            }

            // Audio Input & Real-Time Waveform Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.dp,
                    if (uiState.listeningStatus == ListeningStatus.LISTENING) ElectricBlue else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (statusLabel, statusColor) = when (uiState.listeningStatus) {
                            ListeningStatus.LISTENING -> (if (language == UiLanguage.URDU) "سن رہا ہے..." else "Listening Live") to EmeraldSuccess
                            ListeningStatus.PAUSED -> (if (language == UiLanguage.URDU) "وقفہ" else "Paused") to AmberWarning
                            ListeningStatus.PROCESSING -> (if (language == UiLanguage.URDU) "AI ترجمہ جاری..." else "Translating...") to CyanAccent
                            ListeningStatus.IDLE -> (if (language == UiLanguage.URDU) "تیار" else "Ready") to ElectricBlue
                        }
                        StatusPill(label = statusLabel, color = statusColor)
                        TextButton(onClick = { showPrivacyDialog = true }) {
                            Text(
                                text = if (uiState.privacyAccepted) "✓ Consent Active" else "Review Privacy",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    AudioWaveformVisualizer(
                        status = uiState.listeningStatus,
                        rmsLevel = rmsLevel
                    )

                    if (partialSpeech.isNotBlank() && uiState.listeningStatus == ListeningStatus.LISTENING) {
                        LtrBlock {
                            Text(
                                text = partialSpeech,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    AnimatedMicButton(
                        listening = uiState.listeningStatus == ListeningStatus.LISTENING,
                        onClick = {
                            if (uiState.listeningStatus == ListeningStatus.LISTENING) onStopMic()
                            else triggerMicStartWithChecks()
                        }
                    )

                    // Audio Transport Controls: Pause, Resume, Stop, Upload
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.listeningStatus == ListeningStatus.LISTENING) {
                            OutlinedButton(onClick = onPauseMic) {
                                Icon(Icons.Rounded.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(copy.pauseListening)
                            }
                            OutlinedButton(onClick = onStopMic) {
                                Icon(Icons.Rounded.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(copy.stopListening)
                            }
                        } else if (uiState.listeningStatus == ListeningStatus.PAUSED) {
                            Button(onClick = { triggerMicStartWithChecks() }) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(copy.resumeListening)
                            }
                        }
                        OutlinedButton(
                            onClick = { audioPickerLauncher.launch("audio/*") },
                            modifier = Modifier.testTag("upload_audio_button")
                        ) {
                            Icon(Icons.Rounded.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(copy.uploadAudio)
                        }
                    }

                    if (permissionDeniedNote || uiState.errorMessage != null) {
                        Surface(
                            color = RoseError.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.errorMessage
                                    ?: "Microphone permission is needed for live capture. You can type or tap a sample phrase below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = RoseError,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Manual Input & Quick Sample Customer Phrases
                    OutlinedTextField(
                        value = uiState.manualInputText,
                        onValueChange = onManualInputChange,
                        label = {
                            Text(
                                if (language == UiLanguage.URDU) "کسٹمر کی انگلش بات لکھیں یا بولیں"
                                else "Type or paste customer's English sentence"
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("live_manual_input"),
                        trailingIcon = {
                            IconButton(
                                onClick = { onTranslateSubmit(uiState.manualInputText) },
                                modifier = Modifier.testTag("live_translate_submit_button")
                            ) {
                                Icon(Icons.Rounded.Translate, contentDescription = "Translate & Generate Replies")
                            }
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AcademyContent.sampleCustomerPrompts.forEach { (label, sentence) ->
                            AssistChip(
                                onClick = {
                                    onManualInputChange(sentence)
                                    onTranslateSubmit(sentence)
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            if (uiState.isGenerating) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Dual Language Cards + 3 Suggested Replies
            uiState.currentResult?.let { result ->
                // Customer English Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = copy.customerSaid,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            StatusPill(
                                label = if (language == UiLanguage.URDU) result.intent.labelUr else result.intent.labelEn,
                                color = RoyalPurple
                            )
                        }
                        LtrBlock {
                            Text(
                                text = result.customerEnglish,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Urdu Translation Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ElectricBlue.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = copy.urduMeaning,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = onSaveSegment) {
                                Icon(Icons.Rounded.BookmarkAdd, contentDescription = "Save transcript", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        if (useRomanUrdu) {
                            LtrBlock {
                                Text(
                                    text = result.customerRomanUrdu,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            UrduText(
                                text = result.customerUrdu,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (result.complianceNote != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AmberWarning.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, AmberWarning)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.WarningAmber, contentDescription = "Compliance caution", tint = AmberWarning)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(result.complianceNote, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // 3 AI Suggested Reply Cards
                Text(
                    text = copy.aiSuggestedReplies,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                result.replies.forEach { reply ->
                    SuggestedReplyCard(
                        reply = reply,
                        language = language,
                        useRomanUrdu = useRomanUrdu,
                        onCopy = {
                            clipboard.setText(AnnotatedString(reply.englishText))
                            onShowToast(if (language == UiLanguage.URDU) "جواب کاپی ہو گیا!" else "Reply copied!")
                        },
                        onPlayNormal = { onSpeakReply(reply.englishText, false) },
                        onPlaySlow = { onSpeakReply(reply.englishText, true) },
                        onRefine = { action -> onRefineReply(reply.style, action) }
                    )
                }
            }

            // Session Conversation History (Last 20 Segments)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = copy.sessionHistory,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.sessionSegments.isNotEmpty()) {
                    TextButton(onClick = onClearHistory) {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(copy.clearHistory)
                    }
                }
            }

            if (uiState.sessionSegments.isNotEmpty()) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    label = { Text(if (language == UiLanguage.URDU) "گفتگو تلاش کریں" else "Search conversation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                val filtered = uiState.sessionSegments.filter {
                    uiState.searchQuery.isBlank() ||
                        it.customerEnglish.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.customerUrdu.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.chosenReply.contains(uiState.searchQuery, ignoreCase = true)
                }
                filtered.forEach { seg ->
                    HistorySegmentItem(
                        segment = seg,
                        useRomanUrdu = useRomanUrdu,
                        onCopyReply = {
                            clipboard.setText(AnnotatedString(seg.chosenReply))
                            onShowToast("Copied reply!")
                        }
                    )
                }
            }
        }
    }

    if (showPrivacyDialog) {
        AudioPrivacyConsentDialog(
            language = language,
            onAccept = {
                onAcceptPrivacy()
                showPrivacyDialog = false
                triggerMicStartWithChecks()
            },
            onDismiss = { showPrivacyDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestedReplyCard(
    reply: SuggestedReply,
    language: UiLanguage,
    useRomanUrdu: Boolean,
    onCopy: () -> Unit,
    onPlayNormal: () -> Unit,
    onPlaySlow: () -> Unit,
    onRefine: (ReplyRefinementAction) -> Unit
) {
    val copy = stringsFor(language)
    var showUrdu by remember { mutableStateOf(true) }

    val badgeColor = when (reply.style) {
        ReplyStyle.SIMPLE -> EmeraldSuccess
        ReplyStyle.PROFESSIONAL -> ElectricBlue
        ReplyStyle.FRIENDLY -> RoyalPurple
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusPill(
                    label = if (language == UiLanguage.URDU) "${reply.style.labelEn} • ${reply.style.labelUr}" else reply.style.labelEn,
                    color = badgeColor
                )
                TextButton(onClick = { showUrdu = !showUrdu }) {
                    Text(if (showUrdu) "Hide Meaning" else "Urdu Meaning", style = MaterialTheme.typography.labelSmall)
                }
            }

            LtrBlock {
                Text(
                    text = reply.englishText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(visible = showUrdu) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (useRomanUrdu) {
                        LtrBlock {
                            Text(
                                text = reply.romanUrduMeaning,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        UrduText(
                            text = reply.urduMeaning,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LtrBlock {
                        Text(
                            text = "Pronunciation: ${reply.pronunciationGuide}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(copy.copyReply)
                }
                OutlinedButton(onClick = onPlayNormal, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "Play normal speed", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(copy.listenNormal)
                }
                OutlinedButton(onClick = onPlaySlow, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Rounded.SlowMotionVideo, contentDescription = "Play slow speed", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(copy.listenSlow)
                }
                AssistChip(
                    onClick = { onRefine(ReplyRefinementAction.REGENERATE) },
                    leadingIcon = { Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    label = { Text(copy.generateAnother) }
                )
                AssistChip(
                    onClick = { onRefine(ReplyRefinementAction.SHORTER) },
                    leadingIcon = { Icon(Icons.Rounded.ShortText, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    label = { Text(copy.makeShorter) }
                )
                AssistChip(
                    onClick = { onRefine(ReplyRefinementAction.MORE_PROFESSIONAL) },
                    leadingIcon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    label = { Text(copy.makeProfessional) }
                )
            }
        }
    }
}

@Composable
private fun HistorySegmentItem(
    segment: LiveTranscriptSegment,
    useRomanUrdu: Boolean,
    onCopyReply: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusPill(label = segment.intentLabel, color = ElectricBlue)
                IconButton(onClick = onCopyReply) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy reply", modifier = Modifier.size(18.dp))
                }
            }
            LtrBlock {
                Text("Customer: ${segment.customerEnglish}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
            if (useRomanUrdu) {
                LtrBlock {
                    Text(segment.customerRomanUrdu, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                UrduText(segment.customerUrdu, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            LtrBlock {
                Text("Reply: ${segment.chosenReply}", style = MaterialTheme.typography.bodySmall, color = EmeraldSuccess)
            }
        }
    }
}
