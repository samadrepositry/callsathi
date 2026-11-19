package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.local.UserPreferencesEntity
import com.example.data.local.UserProfileEntity
import com.example.domain.model.AccentPreference
import com.example.domain.model.EnglishLevel
import com.example.domain.model.PaymentMethodType
import com.example.domain.model.PlanTier
import com.example.domain.model.ReplyLength
import com.example.domain.model.ReplyTone
import com.example.domain.model.UiLanguage
import com.example.domain.model.UrduScriptType
import com.example.ui.components.GradientButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.util.stringsFor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccountAndMoreScreen(
    initialSubTab: Int = 0,
    language: UiLanguage,
    darkMode: Boolean,
    profile: UserProfileEntity?,
    preferences: UserPreferencesEntity,
    adminSettings: AdminSettingsEntity,
    userTransactions: List<PaymentTransactionEntity>,
    userTickets: List<SupportTicketEntity>,
    allProfiles: List<UserProfileEntity>,
    allTransactions: List<PaymentTransactionEntity>,
    allTickets: List<SupportTicketEntity>,
    onToggleLanguage: () -> Unit,
    onUpdatePreferences: (UserPreferencesEntity) -> Unit,
    onSubmitPayment: (PlanTier, Int, PaymentMethodType, String, String?) -> Unit,
    onSubmitTicket: (String, String, String) -> Unit,
    onApprovePayment: (PaymentTransactionEntity) -> Unit,
    onRejectPayment: (PaymentTransactionEntity, String) -> Unit,
    onToggleUserStatus: (UserProfileEntity) -> Unit,
    onSaveAdminSettings: (AdminSettingsEntity) -> Unit,
    onResolveTicket: (SupportTicketEntity) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val copy = stringsFor(language)
    val context = LocalContext.current
    var selectedSection by remember(initialSubTab) { mutableIntStateOf(initialSubTab) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

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
            // Sub-section Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    leadingIcon = { Icon(Icons.Rounded.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text(copy.aiSettings) },
                    modifier = Modifier.testTag("tab_ai_settings")
                )
                FilterChip(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    leadingIcon = { Icon(Icons.Rounded.Payments, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text(copy.plansAndPayments) },
                    modifier = Modifier.testTag("tab_plans_payments")
                )
                FilterChip(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    leadingIcon = { Icon(Icons.Rounded.SupportAgent, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text(copy.customerSupport) },
                    modifier = Modifier.testTag("tab_support")
                )
                if (profile?.isAdmin == true) {
                    FilterChip(
                        selected = selectedSection == 3,
                        onClick = { selectedSection = 3 },
                        leadingIcon = { Icon(Icons.Rounded.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text(copy.adminPanel) },
                        modifier = Modifier.testTag("tab_admin_panel")
                    )
                }
            }

            when (selectedSection) {
                0 -> {
                    // AI Reply Settings & App Preferences
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(copy.aiSettings, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                            Text("Preferred English Accent", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AccentPreference.entries.forEach { accent ->
                                    FilterChip(
                                        selected = preferences.accent == accent.name,
                                        onClick = { onUpdatePreferences(preferences.copy(accent = accent.name)) },
                                        label = { Text(accent.labelEn) }
                                    )
                                }
                            }

                            Text("Reply Length", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ReplyLength.entries.forEach { len ->
                                    FilterChip(
                                        selected = preferences.replyLength == len.name,
                                        onClick = { onUpdatePreferences(preferences.copy(replyLength = len.name)) },
                                        label = { Text(len.labelEn) }
                                    )
                                }
                            }

                            Text("English Difficulty", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                EnglishLevel.entries.forEach { lvl ->
                                    FilterChip(
                                        selected = preferences.difficulty == lvl.name,
                                        onClick = { onUpdatePreferences(preferences.copy(difficulty = lvl.name)) },
                                        label = { Text(lvl.labelEn) }
                                    )
                                }
                            }

                            Text("Response Tone", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ReplyTone.entries.forEach { tone ->
                                    FilterChip(
                                        selected = preferences.tone == tone.name,
                                        onClick = { onUpdatePreferences(preferences.copy(tone = tone.name)) },
                                        label = { Text(tone.labelEn) }
                                    )
                                }
                            }

                            Text("Urdu Translation Format", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                UrduScriptType.entries.forEach { script ->
                                    FilterChip(
                                        selected = preferences.urduScript == script.name,
                                        onClick = { onUpdatePreferences(preferences.copy(urduScript = script.name)) },
                                        label = { Text(script.labelEn) }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dark Navy Theme", style = MaterialTheme.typography.bodyMedium)
                                Switch(
                                    checked = darkMode,
                                    onCheckedChange = { onUpdatePreferences(preferences.copy(darkMode = it)) }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Save Transcripts Automatically", style = MaterialTheme.typography.bodyMedium)
                                    Text("Off by default for privacy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = preferences.saveTranscriptsByDefault,
                                    onCheckedChange = { onUpdatePreferences(preferences.copy(saveTranscriptsByDefault = it)) }
                                )
                            }

                            OutlinedButton(onClick = onToggleLanguage, modifier = Modifier.fillMaxWidth()) {
                                Text(if (language == UiLanguage.URDU) "Switch App Language to English" else "ایپ کی زبان اردو میں تبدیل کریں")
                            }
                        }
                    }

                    // Account Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier.weight(1f).testTag("logout_button")
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Logout")
                        }
                        OutlinedButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.weight(1f).testTag("delete_account_button"),
                            border = BorderStroke(1.dp, RoseError)
                        ) {
                            Icon(Icons.Rounded.DeleteForever, contentDescription = null, tint = RoseError, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delete Account", color = RoseError)
                        }
                    }
                }

                1 -> PaymentsAndPlansView(
                    language = language,
                    profile = profile,
                    adminSettings = adminSettings,
                    transactions = userTransactions,
                    onSubmitPayment = onSubmitPayment
                )

                2 -> {
                    // Customer Support & WhatsApp
                    var subject by remember { mutableStateOf("") }
                    var message by remember { mutableStateOf("") }
                    var category by remember { mutableStateOf("General Help") }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(copy.customerSupport, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = {
                                    val digits = adminSettings.whatsappSupportNumber.filter { it.isDigit() }
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$digits"))
                                    runCatching { context.startActivity(intent) }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                modifier = Modifier.fillMaxWidth().testTag("whatsapp_support_button")
                            ) {
                                Icon(Icons.Rounded.Chat, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("WhatsApp Support (${adminSettings.whatsappSupportNumber})", color = Color.White)
                            }

                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("General Help", "Payment Issue", "Report a Problem").forEach { cat ->
                                    FilterChip(
                                        selected = category == cat,
                                        onClick = { category = cat },
                                        label = { Text(cat) }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = subject,
                                onValueChange = { subject = it },
                                label = { Text("Subject") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = message,
                                onValueChange = { message = it },
                                label = { Text("Describe your question or problem") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                            GradientButton(
                                text = "Submit Support Ticket",
                                onClick = {
                                    onSubmitTicket(subject, message, category)
                                    subject = ""
                                    message = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (userTickets.isNotEmpty()) {
                        Text("Your Tickets", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        userTickets.forEach { t ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(t.subject, style = MaterialTheme.typography.titleSmall)
                                        Text(t.message, style = MaterialTheme.typography.bodySmall)
                                    }
                                    StatusPill(label = t.status, color = EmeraldSuccess)
                                }
                            }
                        }
                    }
                }

                3 -> AdminDashboardView(
                    currentProfile = profile,
                    allProfiles = allProfiles,
                    allTransactions = allTransactions,
                    allTickets = allTickets,
                    adminSettings = adminSettings,
                    onApprovePayment = onApprovePayment,
                    onRejectPayment = onRejectPayment,
                    onToggleUserStatus = onToggleUserStatus,
                    onSaveSettings = onSaveAdminSettings,
                    onResolveTicket = onResolveTicket
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account?") },
            text = { Text("This will permanently remove your local profile, saved transcripts, and progress from this device.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                ) { Text("Delete Permanently") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
