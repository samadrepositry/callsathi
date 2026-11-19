package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.local.UserProfileEntity
import com.example.domain.model.AccountStatus
import com.example.domain.model.PaymentStatus
import com.example.domain.model.PlanTier
import com.example.ui.components.GradientButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.ui.theme.RoyalPurple

@Composable
fun AdminDashboardView(
    currentProfile: UserProfileEntity?,
    allProfiles: List<UserProfileEntity>,
    allTransactions: List<PaymentTransactionEntity>,
    allTickets: List<SupportTicketEntity>,
    adminSettings: AdminSettingsEntity,
    onApprovePayment: (PaymentTransactionEntity) -> Unit,
    onRejectPayment: (PaymentTransactionEntity, String) -> Unit,
    onToggleUserStatus: (UserProfileEntity) -> Unit,
    onSaveSettings: (AdminSettingsEntity) -> Unit,
    onResolveTicket: (SupportTicketEntity) -> Unit
) {
    if (currentProfile?.isAdmin != true) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = RoseError.copy(alpha = 0.14f))
        ) {
            Text(
                text = "Admin Access Required: Sign in with an authorized admin account (e.g., Admin Demo on the Sign In tab) to access admin controls.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    var userSearch by remember { mutableStateOf("") }
    var rejectingTx by remember { mutableStateOf<PaymentTransactionEntity?>(null) }
    var rejectReason by remember { mutableStateOf("Transaction reference not found") }

    var monthlyPriceText by remember(adminSettings) { mutableStateOf(adminSettings.monthlyPricePkr.toString()) }
    var yearlyPromoText by remember(adminSettings) { mutableStateOf(adminSettings.yearlyPromoPricePkr.toString()) }
    var trialLimitText by remember(adminSettings) { mutableStateOf(adminSettings.freeTrialReplyLimit.toString()) }
    var whatsappText by remember(adminSettings) { mutableStateOf(adminSettings.whatsappSupportNumber) }
    var bankIbanText by remember(adminSettings) { mutableStateOf(adminSettings.ibanOrAccount) }

    val totalUsers = allProfiles.size
    val activePaying = allProfiles.count { it.planTier != PlanTier.FREE_TRIAL.name && !it.isAdmin }
    val pendingPayments = allTransactions.count { it.status == PaymentStatus.PENDING_REVIEW.name }
    val mrrPkr = allTransactions.filter { it.status == PaymentStatus.APPROVED.name && it.planTier == PlanTier.MONTHLY_PRO.name }.sumOf { it.amountPkr }
    val yearlySalesPkr = allTransactions.filter { it.status == PaymentStatus.APPROVED.name && it.planTier == PlanTier.YEARLY_PRO.name }.sumOf { it.amountPkr }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Admin Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AdminStatTile("Total Users", totalUsers.toString(), ElectricBlue, Modifier.weight(1f))
            AdminStatTile("Paying Users", activePaying.toString(), EmeraldSuccess, Modifier.weight(1f))
            AdminStatTile("Pending Pay", pendingPayments.toString(), AmberWarning, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AdminStatTile("MRR (PKR)", "Rs. $mrrPkr", RoyalPurple, Modifier.weight(1f))
            AdminStatTile("Yearly Sales", "Rs. $yearlySalesPkr", EmeraldSuccess, Modifier.weight(1f))
        }

        // Payment Requests Queue
        Text("Payment Requests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        allTransactions.forEach { tx ->
            val isPending = tx.status == PaymentStatus.PENDING_REVIEW.name
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${tx.userName} • Rs. ${tx.amountPkr}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        StatusPill(
                            label = tx.status,
                            color = when (tx.status) {
                                PaymentStatus.APPROVED.name -> EmeraldSuccess
                                PaymentStatus.REJECTED.name -> RoseError
                                else -> AmberWarning
                            }
                        )
                    }
                    Text("${tx.method} • Ref: ${tx.referenceCode} • ${tx.planTier}", style = MaterialTheme.typography.bodySmall)
                    if (isPending) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onApprovePayment(tx) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                modifier = Modifier.weight(1f).testTag("approve_tx_${tx.id}")
                            ) {
                                Text("Approve")
                            }
                            OutlinedButton(
                                onClick = { rejectingTx = tx },
                                modifier = Modifier.weight(1f).testTag("reject_tx_${tx.id}")
                            ) {
                                Text("Reject", color = RoseError)
                            }
                        }
                    }
                }
            }
        }

        // Dynamic Pricing & Payment Config
        Text("Pricing, Limits & Payment Config", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = monthlyPriceText,
                        onValueChange = { monthlyPriceText = it },
                        label = { Text("Monthly PKR") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = yearlyPromoText,
                        onValueChange = { yearlyPromoText = it },
                        label = { Text("Yearly Promo PKR") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = trialLimitText,
                        onValueChange = { trialLimitText = it },
                        label = { Text("Trial Replies Limit") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = whatsappText,
                        onValueChange = { whatsappText = it },
                        label = { Text("WhatsApp Support") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = bankIbanText,
                    onValueChange = { bankIbanText = it },
                    label = { Text("Business Bank IBAN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                GradientButton(
                    text = "Save Admin Configuration",
                    onClick = {
                        onSaveSettings(
                            adminSettings.copy(
                                monthlyPricePkr = monthlyPriceText.toIntOrNull() ?: adminSettings.monthlyPricePkr,
                                yearlyPromoPricePkr = yearlyPromoText.toIntOrNull() ?: adminSettings.yearlyPromoPricePkr,
                                freeTrialReplyLimit = trialLimitText.toIntOrNull() ?: adminSettings.freeTrialReplyLimit,
                                whatsappSupportNumber = whatsappText.trim(),
                                ibanOrAccount = bankIbanText.trim()
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Users Search & Suspend/Activate
        Text("Registered Users", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = userSearch,
            onValueChange = { userSearch = it },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            label = { Text("Search users by name or email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        allProfiles.filter {
            userSearch.isBlank() ||
                it.fullName.contains(userSearch, ignoreCase = true) ||
                it.email.contains(userSearch, ignoreCase = true)
        }.forEach { user ->
            val suspended = user.accountStatus == AccountStatus.SUSPENDED.name
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${user.email} • ${user.planTier}", style = MaterialTheme.typography.bodySmall)
                    }
                    if (!user.isAdmin) {
                        OutlinedButton(onClick = { onToggleUserStatus(user) }) {
                            Text(if (suspended) "Activate" else "Suspend", color = if (suspended) EmeraldSuccess else RoseError)
                        }
                    }
                }
            }
        }

        // Support Tickets
        Text("Support Tickets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        allTickets.forEach { ticket ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(ticket.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        StatusPill(label = ticket.status, color = if (ticket.status == "RESOLVED") EmeraldSuccess else AmberWarning)
                    }
                    Text("${ticket.userEmail}: ${ticket.message}", style = MaterialTheme.typography.bodySmall)
                    if (ticket.status != "RESOLVED") {
                        TextButton(onClick = { onResolveTicket(ticket) }) { Text("Mark Resolved") }
                    }
                }
            }
        }
    }

    if (rejectingTx != null) {
        AlertDialog(
            onDismissRequest = { rejectingTx = null },
            title = { Text("Reject Payment") },
            text = {
                OutlinedTextField(
                    value = rejectReason,
                    onValueChange = { rejectReason = it },
                    label = { Text("Rejection reason") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onRejectPayment(rejectingTx!!, rejectReason)
                    rejectingTx = null
                }) { Text("Confirm Reject") }
            },
            dismissButton = {
                TextButton(onClick = { rejectingTx = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminStatTile(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.14f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
