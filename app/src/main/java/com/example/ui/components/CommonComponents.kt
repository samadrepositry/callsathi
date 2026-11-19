package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ListeningStatus
import com.example.domain.model.UiLanguage
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NotoSansArabicFontFamily
import com.example.ui.theme.RoyalPurple
import kotlin.math.sin

@Composable
fun LtrBlock(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        content()
    }
}

@Composable
fun UrduText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Normal,
    maxLines: Int = Int.MAX_VALUE
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Text(
            text = text,
            modifier = modifier,
            style = style.copy(
                fontFamily = NotoSansArabicFontFamily,
                fontWeight = fontWeight,
                lineHeight = (style.fontSize.value * 1.55f).sp
            ),
            color = color,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SampleAiNoticeBanner(
    noticeText: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AmberWarning.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = "Sample AI Notice",
                tint = AmberWarning,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = noticeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "gradient_button",
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val gradient = Brush.horizontalGradient(
        colors = if (enabled) listOf(ElectricBlue, RoyalPurple)
        else listOf(Color.Gray.copy(alpha = 0.4f), Color.Gray.copy(alpha = 0.4f))
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(52.dp)
            .minimumInteractiveComponentSize()
            .testTag(testTag),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(brush = gradient, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun StatusPill(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun AudioWaveformVisualizer(
    status: ListeningStatus,
    rmsLevel: Float,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "waveform")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val active = status == ListeningStatus.LISTENING || status == ListeningStatus.PROCESSING
    val barCount = 24
    val barBrush = Brush.verticalGradient(listOf(ElectricBlue, RoyalPurple))

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        val barWidth = size.width / (barCount * 1.8f)
        val gap = (size.width - barWidth * barCount) / (barCount - 1).coerceAtLeast(1)
        val centerY = size.height / 2f

        for (i in 0 until barCount) {
            val waveFactor = if (active) {
                val base = 0.25f + 0.75f * rmsLevel.coerceIn(0.15f, 1f)
                val sinVal = (sin(phase + i * 0.45f) + 1f) / 2f
                (base * (0.35f + 0.65f * sinVal)).coerceIn(0.12f, 1f)
            } else if (status == ListeningStatus.PAUSED) {
                0.18f
            } else {
                0.10f
            }
            val barHeight = (size.height * waveFactor).coerceAtLeast(6f)
            val x = i * (barWidth + gap)
            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, centerY - barHeight / 2f),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
                alpha = if (active) 0.95f else 0.45f
            )
        }
    }
}

@Composable
fun AnimatedMicButton(
    listening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.24f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier.size(86.dp),
        contentAlignment = Alignment.Center
    ) {
        if (listening) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(ElectricBlue.copy(alpha = 0.24f))
            )
        }
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        if (listening) listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                        else listOf(ElectricBlue, RoyalPurple)
                    )
                )
                .clickable(onClick = onClick)
                .testTag("live_mic_toggle_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (listening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                contentDescription = if (listening) "Stop listening" else "Start microphone",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun AudioPrivacyConsentDialog(
    language: UiLanguage,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.PrivacyTip,
                contentDescription = "Audio Privacy",
                tint = ElectricBlue
            )
        },
        title = {
            Text(
                text = if (language == UiLanguage.URDU) "آڈیو پرائیویسی اور اجازت کا نوٹس"
                else "Audio Privacy & Consent Notice"
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == UiLanguage.URDU) {
                        "• CallSathi AI خود کسٹمر سے بات نہیں کرتا — آپ خود بولتے ہیں۔\n" +
                            "• آپ کی آواز یا کال آڈیو مستقل طور پر محفوظ نہیں کی جاتی۔\n" +
                            "• موبائل OS پرائیویسی پابندیوں کی وجہ سے یہ ایپ فون کال کی اندرونی آواز چوری نہیں کرتی۔ موبائل پر اسپیکر یا دوسرے ڈیوائس کے قریب مائیک استعمال کریں۔\n" +
                            "• براہ کرم یقینی بنائیں کہ آپ کو اپنے آجر کی پالیسی اور قوانین کے مطابق آڈیو ترجمہ کرنے کی اجازت ہے۔"
                    } else {
                        "• CallSathi AI never speaks directly to customers—you always speak personally.\n" +
                            "• Raw audio is never permanently stored by default.\n" +
                            "• Due to mobile OS privacy restrictions, Android apps cannot intercept protected phone-call audio. Use speaker/second-device mode on mobile or USB headset on desktop.\n" +
                            "• Ensure you have permission under employer policies and applicable laws before processing call audio."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                modifier = Modifier.testTag("accept_privacy_button")
            ) {
                Text(if (language == UiLanguage.URDU) "میں متفق ہوں" else "I Understand & Consent")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == UiLanguage.URDU) "منسوخ کریں" else "Cancel")
            }
        }
    )
}
