package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.Upgrade
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AcademyContent
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.LearningProgressEntity
import com.example.data.local.PracticeSessionEntity
import com.example.data.local.UsageRecordEntity
import com.example.data.local.UserProfileEntity
import com.example.domain.model.PlanTier
import com.example.domain.model.UiLanguage
import com.example.ui.components.GradientButton
import com.example.ui.components.SampleAiNoticeBanner
import com.example.ui.components.StatusPill
import com.example.ui.components.UrduText
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoyalPurple
import com.example.util.stringsFor

@Composable
fun DashboardScreen(
    profile: UserProfileEntity?,
    isDemoMode: Boolean,
    language: UiLanguage,
    usage: UsageRecordEntity,
    adminSettings: AdminSettingsEntity,
    learningProgress: List<LearningProgressEntity>,
    practiceHistory: List<PracticeSessionEntity>,
    isPlaceholderAi: Boolean,
    onOpenLiveAssistant: () -> Unit,
    onOpenPractice: () -> Unit,
    onOpenAcademy: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenSupport: () -> Unit
) {
    val copy = stringsFor(language)
    val planTier = runCatching { PlanTier.valueOf(profile?.planTier ?: "FREE_TRIAL") }
        .getOrDefault(PlanTier.FREE_TRIAL)
    val replyLimit = when (planTier) {
        PlanTier.FREE_TRIAL -> adminSettings.freeTrialReplyLimit
        PlanTier.MONTHLY_PRO, PlanTier.YEARLY_PRO -> adminSettings.monthlyProReplyLimit
    }
    val remainingReplies = (replyLimit - usage.aiRepliesUsed).coerceAtLeast(0)
    val progressFraction = (usage.aiRepliesUsed.toFloat() / replyLimit.coerceAtLeast(1)).coerceIn(0f, 1f)
    val completedLessons = learningProgress.count { it.completed }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isPlaceholderAi) {
                SampleAiNoticeBanner(noticeText = copy.sampleAiNotice)
            }

            // Welcome & Subscription Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${copy.welcomeBack}, ${profile?.fullName ?: "Agent"}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            UrduText(
                                text = if (language == UiLanguage.URDU) "آج آپ کی انگلش کال اسسٹنس تیار ہے۔"
                                else "Your live call assistant is ready.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusPill(
                            label = if (isDemoMode) "Demo • ${planTier.labelEn}" else planTier.labelEn,
                            color = if (planTier == PlanTier.FREE_TRIAL) AmberWarning else EmeraldSuccess
                        )
                    }

                    // Remaining AI Usage
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == UiLanguage.URDU) "باقی AI جوابات" else "Remaining AI Replies",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "$remainingReplies / $replyLimit",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { 1f - progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = ElectricBlue,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Audio: ${usage.transcriptionSecondsUsed}s",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "TTS: ${usage.ttsCountUsed}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Practice: ${usage.practiceMinutesUsed}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (planTier == PlanTier.FREE_TRIAL) {
                        GradientButton(
                            text = "${copy.upgradePro} — Rs. ${adminSettings.monthlyPricePkr}/mo",
                            onClick = onOpenPlans,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "dashboard_upgrade_button",
                            leadingIcon = {
                                Icon(Icons.Rounded.Upgrade, contentDescription = null, tint = Color.White)
                            }
                        )
                    }
                }
            }

            // Primary Shortcuts
            Text(
                text = if (language == UiLanguage.URDU) "فوری شارٹ کٹس" else "Quick Shortcuts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ShortcutCard(
                icon = Icons.Rounded.Mic,
                accentColor = ElectricBlue,
                title = if (language == UiLanguage.URDU) "لائیو کال اسسٹنٹ کھولیں" else "Open Live Call Assistant",
                subtitle = if (language == UiLanguage.URDU) "کسٹمر کی بات اردو میں ترجمہ کریں اور 3 انگلش جوابات پائیں" else "Translate customer speech to Urdu & get 3 English replies",
                testTag = "shortcut_live_assistant",
                onClick = onOpenLiveAssistant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompactShortcutTile(
                    icon = Icons.Rounded.RecordVoiceOver,
                    color = RoyalPurple,
                    title = if (language == UiLanguage.URDU) "انگلش پریکٹس" else "Practice English",
                    metric = "${practiceHistory.size} sessions",
                    testTag = "shortcut_practice",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenPractice
                )
                CompactShortcutTile(
                    icon = Icons.Rounded.School,
                    color = CyanAccent,
                    title = if (language == UiLanguage.URDU) "لرننگ اکیڈمی" else "Academy",
                    metric = "$completedLessons / ${AcademyContent.lessons.size} lessons",
                    testTag = "shortcut_academy",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAcademy
                )
            }

            // Daily Progress & Streak
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AmberWarning.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.LocalFireDepartment, contentDescription = "Streak", tint = AmberWarning)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${usage.dailyStreak} Day Practice Streak",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == UiLanguage.URDU) "روزانہ مشق سے آپ کی روانی بہتر ہوتی ہے" else "Keep practicing daily to boost fluency",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(Icons.Rounded.Bolt, contentDescription = null, tint = AmberWarning)
                }
            }

            // Recent Activity
            Text(
                text = if (language == UiLanguage.URDU) "حالیہ سرگرمی" else "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (practiceHistory.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == UiLanguage.URDU) "پہلی پریکٹس کال یا لائیو ترجمہ شروع کریں تاکہ یہاں سرگرمی نظر آئے۔"
                            else "Start your first Live Call Assist or Practice Call to track activity here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                practiceHistory.take(3).forEach { session ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(session.scenarioTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "Speaking ${session.speakingScore}% • Confidence ${session.confidenceScore}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusPill(label = "${session.responseQualityScore}%", color = EmeraldSuccess)
                        }
                    }
                }
            }

            // Help / Support Quick Row
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenSupport)
                    .testTag("dashboard_support_button")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.SupportAgent, contentDescription = "Support", tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = copy.customerSupport,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = adminSettings.whatsappSupportNumber,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ShortcutCard(
    icon: ImageVector,
    accentColor: Color,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(accentColor, RoyalPurple)))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.88f))
            }
        }
    }
}

@Composable
private fun CompactShortcutTile(
    icon: ImageVector,
    color: Color,
    title: String,
    metric: String,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color)
            }
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(metric, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
