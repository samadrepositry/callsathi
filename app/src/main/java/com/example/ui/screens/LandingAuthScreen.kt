package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.HeadsetMic
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.AcademyContent
import com.example.data.local.AdminSettingsEntity
import com.example.domain.model.EnglishLevel
import com.example.domain.model.UiLanguage
import com.example.ui.components.GradientButton
import com.example.ui.components.LtrBlock
import com.example.ui.components.StatusPill
import com.example.ui.components.UrduText
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RoyalPurple
import com.example.ui.viewmodel.AuthFormState
import com.example.util.stringsFor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LandingAuthScreen(
    language: UiLanguage,
    darkMode: Boolean,
    authState: AuthFormState,
    adminSettings: AdminSettingsEntity,
    onToggleLanguage: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onWatchDemo: () -> Unit,
    onSignUp: (String, String, String, EnglishLevel, UiLanguage) -> Unit,
    onLogin: (String, String) -> Unit,
    onAdminDemoLogin: () -> Unit,
    onResetPassword: (String) -> Unit
) {
    val copy = stringsFor(language)
    var showAuthSection by remember { mutableStateOf(false) }
    var authTab by remember { mutableIntStateOf(0) } // 0 = Sign Up, 1 = Login
    var legalModalTitle by remember { mutableStateOf<String?>(null) }
    var legalModalBody by remember { mutableStateOf("") }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf(EnglishLevel.BEGINNER) }
    var preferredLang by remember { mutableStateOf(UiLanguage.URDU) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Top Brand Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(ElectricBlue, RoyalPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.HeadsetMic,
                                contentDescription = "CallSathi AI Logo",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CallSathi AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = copy.tagline,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = onToggleLanguage,
                            modifier = Modifier.testTag("landing_language_toggle")
                        ) {
                            Icon(Icons.Rounded.Language, contentDescription = "Switch Language", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == UiLanguage.URDU) "EN" else "اردو")
                        }
                        IconButton(onClick = onToggleDarkMode) {
                            Icon(
                                imageVector = if (darkMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                contentDescription = "Toggle theme"
                            )
                        }
                    }
                }

                // Hero Card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDeep),
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f))
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(165.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_assistant_1795076903608),
                                contentDescription = "CallSathi AI Waveform Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, NavyDeep.copy(alpha = 0.92f))
                                        )
                                    )
                            )
                            StatusPill(
                                label = "For Pakistan Call Center Agents",
                                color = CyanAccent,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(14.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LtrBlock {
                                Text(
                                    text = "Now Understand English Calls Like a Professional!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            UrduText(
                                text = "انگلش نہیں آتی؟ کوئی مسئلہ نہیں! CallSathi AI آپ کی مدد کرے گا۔",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFBFDBFE),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Listen to English conversations, understand them in Urdu, and get professional English replies instantly.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCBD5E1)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GradientButton(
                                    text = copy.startTrial,
                                    onClick = {
                                        showAuthSection = true
                                        authTab = 0
                                    },
                                    modifier = Modifier.weight(1f),
                                    testTag = "start_free_trial_cta"
                                )
                                OutlinedButton(
                                    onClick = onWatchDemo,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .testTag("watch_demo_cta"),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFF60A5FA))
                                ) {
                                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(copy.watchDemo, color = Color.White)
                                }
                            }
                            TextButton(
                                onClick = {
                                    showAuthSection = !showAuthSection
                                    authTab = 1
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = if (language == UiLanguage.URDU) "پہلے سے اکاؤنٹ ہے؟ لاگ ان کریں" else "Already have an account? Sign In",
                                    color = Color(0xFF93C5FD)
                                )
                            }
                        }
                    }
                }

                // Authentication Card (Expandable or toggled by Start Trial / Sign In)
                AnimatedVisibility(visible = showAuthSection) {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TabRow(selectedTabIndex = authTab) {
                                Tab(
                                    selected = authTab == 0,
                                    onClick = { authTab = 0 },
                                    text = { Text(copy.signUp) }
                                )
                                Tab(
                                    selected = authTab == 1,
                                    onClick = { authTab = 1 },
                                    text = { Text(copy.signIn) }
                                )
                            }

                            if (authState.errorMessage != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = authState.errorMessage,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            if (authState.statusBanner != null) {
                                Surface(
                                    color = EmeraldSuccess.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = authState.statusBanner,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            if (authTab == 0) {
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    label = { Text(if (language == UiLanguage.URDU) "پورا نام (Full Name)" else "Full Name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("signup_name_input")
                                )
                            }
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text(if (language == UiLanguage.URDU) "ای میل (Email)" else "Email Address") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(if (language == UiLanguage.URDU) "پاس ورڈ (کم از کم 6 حروف)" else "Password (min 6 chars)") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
                            )

                            if (authTab == 0) {
                                Text(
                                    text = if (language == UiLanguage.URDU) "انگلش کی سطح (English Proficiency)" else "English Proficiency",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    EnglishLevel.entries.forEach { level ->
                                        FilterChip(
                                            selected = selectedLevel == level,
                                            onClick = { selectedLevel = level },
                                            label = { Text(level.labelEn) }
                                        )
                                    }
                                }
                                Text(
                                    text = if (language == UiLanguage.URDU) "پسندیدہ زبان" else "Preferred UI Language",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    UiLanguage.entries.forEach { lang ->
                                        FilterChip(
                                            selected = preferredLang == lang,
                                            onClick = { preferredLang = lang },
                                            label = { Text(lang.labelUr) }
                                        )
                                    }
                                }
                                GradientButton(
                                    text = copy.signUp,
                                    enabled = !authState.isLoading,
                                    onClick = { onSignUp(fullName, email, password, selectedLevel, preferredLang) },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "signup_submit_button"
                                )
                            } else {
                                GradientButton(
                                    text = copy.signIn,
                                    enabled = !authState.isLoading,
                                    onClick = { onLogin(email, password) },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "login_submit_button"
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextButton(onClick = { onResetPassword(email) }) {
                                        Text(if (language == UiLanguage.URDU) "پاس ورڈ بھول گئے؟" else "Reset Password")
                                    }
                                    TextButton(
                                        onClick = onAdminDemoLogin,
                                        modifier = Modifier.testTag("admin_demo_login_button")
                                    ) {
                                        Text(if (language == UiLanguage.URDU) "ایڈمن ڈیمو" else "Admin Demo")
                                    }
                                }
                            }
                        }
                    }
                }

                // Live Visual Conversation Demo Preview
                Text(
                    text = if (language == UiLanguage.URDU) "لائیو ترجمہ اور جواب کا نمونہ" else "Live AI Translation Demo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatusPill(label = "Sample Preview", color = ElectricBlue)
                        LtrBlock {
                            Text(
                                text = "Customer: \"Can you please tell me why you are interested in this service?\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        UrduText(
                            text = "اردو معنی: \"کیا آپ بتا سکتے ہیں کہ آپ اس سروس میں دلچسپی کیوں رکھتے ہیں؟\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "AI Suggested Reply (Professional):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                                LtrBlock {
                                    Text(
                                        text = "\"Certainly! I am interested in learning more about your service because I believe it could be very helpful for my needs.\"",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // How It Works & Features
                Text(
                    text = if (language == UiLanguage.URDU) "یہ کیسے کام کرتا ہے؟" else "How CallSathi AI Works",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                val features = listOf(
                    Triple(Icons.Rounded.GraphicEq, "1. Live AI Translation", "کسٹمر کی انگلش بات فوری اردو میں سمجھیں — موبائل یا ڈیسک ٹاپ سیٹ اپ کے ساتھ۔"),
                    Triple(Icons.Rounded.AutoAwesome, "2. Instant Reply Generator", "3 پروفیشنل، سادہ اور دوستانہ انگلش جوابات اردو معنی کے ساتھ حاصل کریں۔"),
                    Triple(Icons.Rounded.Translate, "3. AI Call Training", "امریکہ، برطانیہ، کینیڈا اور آسٹریلیا کے کسٹمرز کے ساتھ پریکٹس کریں۔"),
                    Triple(Icons.Rounded.School, "4. English Learning Academy", "بنیادی کال سینٹر انگلش اسباق اور روزانہ 5 نئے الفاظ سیکھیں۔")
                )
                features.forEach { (icon, title, desc) ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ElectricBlue.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = title, tint = ElectricBlue)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                UrduText(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Pricing Section
                Text(
                    text = if (language == UiLanguage.URDU) "پاکستانی روپوں (PKR) میں پیکجز" else "Pricing Plans (PKR)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                PricingSummaryCard(
                    title = "Free Trial",
                    price = "Rs. 0",
                    bullets = listOf("Limited AI replies (${adminSettings.freeTrialReplyLimit})", "Live translation preview", "Sample lessons & demo"),
                    highlighted = false,
                    onSelect = { showAuthSection = true; authTab = 0 }
                )
                PricingSummaryCard(
                    title = "Monthly Pro",
                    price = "Rs. ${adminSettings.monthlyPricePkr} / month",
                    bullets = listOf("Live AI translation & Urdu meanings", "3 Reply Styles + Context + Voice Playback", "Full AI Call Training & Learning Academy"),
                    highlighted = true,
                    onSelect = { showAuthSection = true; authTab = 0 }
                )
                PricingSummaryCard(
                    title = "Yearly Pro (Launch Offer)",
                    price = "Rs. ${adminSettings.yearlyPromoPricePkr} / year (Regular Rs. ${adminSettings.yearlyRegularPricePkr})",
                    bullets = listOf("All Monthly Pro features for 12 months", "Save up to Rs. ${adminSettings.yearlyRegularPricePkr - adminSettings.yearlyPromoPricePkr}", "JazzCash, Easypaisa, Bank & Raast supported"),
                    highlighted = false,
                    onSelect = { showAuthSection = true; authTab = 0 }
                )

                // Sample Testimonials (explicitly labeled)
                Text(
                    text = if (language == UiLanguage.URDU) "نمونہ تاثرات (Sample Testimonials)" else "Sample Testimonials",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusPill(label = "Sample Feedback (Demo)", color = RoyalPurple)
                        Text(
                            text = "\"CallSathi AI helped me understand US customers clearly during training in Lahore and speak polite replies confidently.\" — Sample Agent Profile",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // FAQs
                Text(
                    text = if (language == UiLanguage.URDU) "اکثر پوچھے گئے سوالات (FAQ)" else "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                AcademyContent.faqs.forEach { faq ->
                    var expanded by remember { mutableStateOf(false) }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.clickable { expanded = !expanded }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == UiLanguage.URDU) faq.questionUr else faq.questionEn,
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = "Expand FAQ"
                                )
                            }
                            AnimatedVisibility(visible = expanded) {
                                Text(
                                    text = if (language == UiLanguage.URDU) faq.answerUr else faq.answerEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        legalModalTitle = "Privacy Policy"
                        legalModalBody = "CallSathi AI never permanently records or stores customer call audio by default. Transcripts are saved only if you explicitly enable saving or tap Save. You must comply with your employer's policies and applicable call-monitoring laws."
                    }) { Text("Privacy Policy") }
                    TextButton(onClick = {
                        legalModalTitle = "Terms & Conditions"
                        legalModalBody = "CallSathi AI is an agent-assist tool. The human agent always speaks directly to the customer. Paid subscriptions in Pakistan are activated only after verified payment callback or admin receipt approval."
                    }) { Text("Terms") }
                    TextButton(onClick = {
                        legalModalTitle = "Contact CallSathi AI"
                        legalModalBody = "WhatsApp Support: ${adminSettings.whatsappSupportNumber}\nEmail: support@callsathi.pk\nLahore & Karachi Remote BPO Support"
                    }) { Text("Contact") }
                }
            }
        }
    }

    if (legalModalTitle != null) {
        AlertDialog(
            onDismissRequest = { legalModalTitle = null },
            title = { Text(legalModalTitle!!) },
            text = { Text(legalModalBody) },
            confirmButton = {
                TextButton(onClick = { legalModalTitle = null }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun PricingSummaryCard(
    title: String,
    price: String,
    bullets: List<String>,
    highlighted: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) ElectricBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (highlighted) 2.dp else 1.dp,
            color = if (highlighted) ElectricBlue else MaterialTheme.colorScheme.outline
        )
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
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (highlighted) StatusPill(label = "Most Popular", color = ElectricBlue)
            }
            LtrBlock {
                Text(
                    text = price,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            bullets.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item, style = MaterialTheme.typography.bodySmall)
                }
            }
            OutlinedButton(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Select $title")
            }
        }
    }
}
