package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.AcademyContent
import com.example.data.local.LearningProgressEntity
import com.example.domain.model.LessonItem
import com.example.domain.model.UiLanguage
import com.example.ui.components.GradientButton
import com.example.ui.components.LtrBlock
import com.example.ui.components.StatusPill
import com.example.ui.components.UrduText
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoyalPurple
import com.example.util.stringsFor

@Composable
fun AcademyScreen(
    language: UiLanguage,
    progressList: List<LearningProgressEntity>,
    onSpeakText: (String, Boolean) -> Unit,
    onCompleteLessonQuiz: (String, Int) -> Unit
) {
    val copy = stringsFor(language)
    var activeLesson by remember { mutableStateOf<LessonItem?>(null) }

    if (activeLesson != null) {
        val lesson = activeLesson!!
        var selectedQuizOption by remember { mutableIntStateOf(-1) }
        var quizResultMessage by remember { mutableStateOf<String?>(null) }

        BackHandler { activeLesson = null }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { activeLesson = null }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back to Academy")
                    }
                    Column {
                        Text(lesson.titleEn, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        UrduText(lesson.titleUr, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }

                UrduText(lesson.descriptionUr, style = MaterialTheme.typography.bodyMedium)

                lesson.keyPhrases.forEach { phrase ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LtrBlock {
                                Text(phrase.english, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            }
                            UrduText(phrase.urdu, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            LtrBlock {
                                Text("Roman Urdu: ${phrase.romanUrdu}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Pronounce: ${phrase.pronunciationGuide}", style = MaterialTheme.typography.labelSmall, color = RoyalPurple)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { onSpeakText(phrase.english, false) }) {
                                    Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(copy.listenNormal)
                                }
                                OutlinedButton(onClick = { onSpeakText(phrase.english, true) }) {
                                    Icon(Icons.Rounded.SlowMotionVideo, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(copy.listenSlow)
                                }
                            }
                        }
                    }
                }

                // Real-Life Example
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusPill(label = "Real-Life Call Example", color = ElectricBlue)
                        LtrBlock {
                            Text(lesson.exampleDialogueEn, style = MaterialTheme.typography.bodyMedium)
                        }
                        UrduText(lesson.exampleDialogueUr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Practice Quiz Exercise
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Practice Exercise", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        LtrBlock {
                            Text(lesson.quizQuestionEn, style = MaterialTheme.typography.bodyMedium)
                        }
                        lesson.quizOptions.forEachIndexed { idx, opt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedQuizOption = idx }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedQuizOption == idx,
                                    onClick = { selectedQuizOption = idx }
                                )
                                LtrBlock {
                                    Text(opt, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        GradientButton(
                            text = if (language == UiLanguage.URDU) "مشق مکمل کریں" else "Check Answer & Complete",
                            enabled = selectedQuizOption >= 0,
                            onClick = {
                                val correct = selectedQuizOption == lesson.correctOptionIndex
                                val score = if (correct) 100 else 60
                                quizResultMessage = if (correct) {
                                    "Excellent! That is the polite international call-center choice."
                                } else {
                                    "Good try! Remember to stay polite and clear. Marked as reviewed."
                                }
                                onCompleteLessonQuiz(lesson.id, score)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (quizResultMessage != null) {
                            Text(quizResultMessage!!, style = MaterialTheme.typography.bodySmall, color = EmeraldSuccess)
                        }
                    }
                }
            }
        }
        return
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
            // Academy Hero Banner
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_academy_banner_1795076903542),
                        contentDescription = "English Learning Academy Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(copy.academyTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        UrduText(
                            text = "ابتدائی طلباء اور کال سینٹر ایجنٹس کے لیے آسان انگلش اسباق اور روزانہ 5 الفاظ۔",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Daily Vocabulary (5 Words)
            Text(
                text = if (language == UiLanguage.URDU) "آج کے 5 اہم الفاظ (Daily Vocabulary)" else "Daily Vocabulary (5 Words)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            AcademyContent.dailyVocabulary.forEach { vocab ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            LtrBlock {
                                Text(vocab.word, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            UrduText(
                                text = "${vocab.meaningUr} (${vocab.romanUrdu})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            LtrBlock {
                                Text(vocab.exampleEn, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = { onSpeakText(vocab.word, false) }) {
                            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "Pronounce ${vocab.word}", tint = ElectricBlue)
                        }
                    }
                }
            }

            // 7 Core Lessons
            Text(
                text = if (language == UiLanguage.URDU) "کال سینٹر انگلش اسباق" else "Call Center English Lessons",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            AcademyContent.lessons.forEach { lesson ->
                val done = progressList.any { it.lessonId == lesson.id && it.completed }
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (done) EmeraldSuccess.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeLesson = lesson }
                        .testTag("lesson_${lesson.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (done) Icons.Rounded.CheckCircle else Icons.Rounded.MenuBook,
                            contentDescription = null,
                            tint = if (done) EmeraldSuccess else ElectricBlue
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(lesson.titleEn, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            UrduText(lesson.titleUr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusPill(label = if (done) "Done" else "Start", color = if (done) EmeraldSuccess else ElectricBlue)
                    }
                }
            }
        }
    }
}
