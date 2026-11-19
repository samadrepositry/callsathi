package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.domain.model.PaymentMethodType
import com.example.domain.model.PaymentStatus
import com.example.domain.model.PlanTier
import com.example.domain.model.UiLanguage
import com.example.ui.components.GradientButton
import com.example.ui.components.LtrBlock
import com.example.ui.components.StatusPill
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentsAndPlansView(
    language: UiLanguage,
    profile: UserProfileEntity?,
    adminSettings: AdminSettingsEntity,
    transactions: List<PaymentTransactionEntity>,
    onSubmitPayment: (PlanTier, Int, PaymentMethodType, String, String?) -> Unit
) {
    var selectedPlan by remember { mutableStateOf(PlanTier.MONTHLY_PRO) }
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.JAZZCASH) }
    var referenceCode by remember { mutableStateOf("CS-PK-${(10000..99999).random()}") }
    var receiptUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) receiptUri = uri
    }

    val amountPkr = when (selectedPlan) {
        PlanTier.FREE_TRIAL -> 0
        PlanTier.MONTHLY_PRO -> adminSettings.monthlyPricePkr
        PlanTier.YEARLY_PRO -> adminSettings.yearlyPromoPricePkr
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Current Plan Banner
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (language == UiLanguage.URDU) "آپ کا موجودہ پلان" else "Current Subscription",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                val currentTier = runCatching { PlanTier.valueOf(profile?.planTier ?: "FREE_TRIAL") }
                    .getOrDefault(PlanTier.FREE_TRIAL)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currentTier.labelEn, style = MaterialTheme.typography.titleLarge, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    StatusPill(
                        label = if (currentTier == PlanTier.FREE_TRIAL) "Trial" else "Active Pro",
                        color = if (currentTier == PlanTier.FREE_TRIAL) AmberWarning else EmeraldSuccess
                    )
                }
                profile?.subscriptionExpiresAt?.let { exp ->
                    val formatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(exp))
                    Text("Expires / Renewal Reminder: $formatted", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Plan Selector
        Text(
            text = if (language == UiLanguage.URDU) "1. پلان منتخب کریں (PKR)" else "1. Choose Plan (PKR)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlanOptionCard(
                title = "Monthly Pro",
                price = "Rs. ${adminSettings.monthlyPricePkr}/mo",
                subtitle = "Full Live AI + Training",
                selected = selectedPlan == PlanTier.MONTHLY_PRO,
                modifier = Modifier.weight(1f),
                onClick = { selectedPlan = PlanTier.MONTHLY_PRO }
            )
            PlanOptionCard(
                title = "Yearly Pro",
                price = "Rs. ${adminSettings.yearlyPromoPricePkr}/yr",
                subtitle = "Promo (Reg Rs. ${adminSettings.yearlyRegularPricePkr})",
                selected = selectedPlan == PlanTier.YEARLY_PRO,
                modifier = Modifier.weight(1f),
                onClick = { selectedPlan = PlanTier.YEARLY_PRO }
            )
        }

        // Pakistani Payment Method Selector
        Text(
            text = if (language == UiLanguage.URDU) "2. پاکستانی ادائیگی کا طریقہ" else "2. Pakistan Payment Method",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = AmberWarning.copy(alpha = 0.14f),
            border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f))
        ) {
            Text(
                text = "Automated JazzCash/Easypaisa/Raast merchant APIs are pending credential configuration. Manual receipt verification is active—paid plans activate only after admin approval.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(10.dp)
            )
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PaymentMethodType.entries.forEach { method ->
                FilterChip(
                    selected = selectedMethod == method,
                    onClick = { selectedMethod = method },
                    label = { Text(method.labelEn) }
                )
            }
        }

        // Payment Instructions Box
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Official Payment Instructions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                LtrBlock {
                    when (selectedMethod) {
                        PaymentMethodType.JAZZCASH -> {
                            Text("JazzCash Mobile Account: ${adminSettings.jazzCashNumber}", fontWeight = FontWeight.SemiBold)
                            Text("Account Title: ${adminSettings.bankAccountTitle}", style = MaterialTheme.typography.bodySmall)
                        }
                        PaymentMethodType.EASYPAISA -> {
                            Text("Easypaisa Mobile Account: ${adminSettings.easypaisaNumber}", fontWeight = FontWeight.SemiBold)
                            Text("Account Title: ${adminSettings.bankAccountTitle}", style = MaterialTheme.typography.bodySmall)
                        }
                        PaymentMethodType.BANK_TRANSFER -> {
                            Text("Bank: ${adminSettings.bankName}", fontWeight = FontWeight.SemiBold)
                            Text("IBAN / Account: ${adminSettings.ibanOrAccount}", style = MaterialTheme.typography.bodySmall)
                            Text("Title: ${adminSettings.bankAccountTitle}", style = MaterialTheme.typography.bodySmall)
                        }
                        PaymentMethodType.RAAST -> {
                            Text("Raast ID: ${adminSettings.raastId}", fontWeight = FontWeight.SemiBold)
                            Text("Title: ${adminSettings.bankAccountTitle}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text("Amount to Pay: Rs. $amountPkr", color = ElectricBlue, fontWeight = FontWeight.Bold)
                }
            }
        }

        OutlinedTextField(
            value = referenceCode,
            onValueChange = { referenceCode = it },
            label = { Text("Transaction / TID Reference") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("payment_reference_input")
        )

        OutlinedButton(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth().testTag("upload_receipt_button"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Rounded.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (receiptUri == null) "Upload Payment Receipt Screenshot (Photo Picker)"
                else "Receipt Selected ✓ (Tap to change)"
            )
        }

        if (receiptUri != null) {
            AsyncImage(
                model = receiptUri,
                contentDescription = "Selected payment receipt",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
        }

        GradientButton(
            text = "Submit Rs. $amountPkr Payment for Verification",
            onClick = {
                onSubmitPayment(selectedPlan, amountPkr, selectedMethod, referenceCode, receiptUri?.toString())
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "submit_payment_button"
        )

        // Transaction History
        Text(
            text = if (language == UiLanguage.URDU) "ادائیگیوں کی تاریخ (Transaction History)" else "Transaction History",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        if (transactions.isEmpty()) {
            Text("No payment transactions yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            transactions.forEach { tx ->
                val statusEnum = runCatching { PaymentStatus.valueOf(tx.status) }.getOrDefault(PaymentStatus.PENDING_REVIEW)
                val statusColor = when (statusEnum) {
                    PaymentStatus.APPROVED -> EmeraldSuccess
                    PaymentStatus.REJECTED -> RoseError
                    PaymentStatus.PENDING_REVIEW -> AmberWarning
                }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.ReceiptLong, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rs. ${tx.amountPkr} • ${tx.method}", style = MaterialTheme.typography.titleSmall)
                            }
                            StatusPill(label = statusEnum.labelEn, color = statusColor)
                        }
                        Text("Ref: ${tx.referenceCode} • Plan: ${tx.planTier}", style = MaterialTheme.typography.bodySmall)
                        if (!tx.rejectionReason.isNullOrBlank()) {
                            Text("Reason: ${tx.rejectionReason}", style = MaterialTheme.typography.bodySmall, color = RoseError)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanOptionCard(
    title: String,
    price: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) ElectricBlue.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) ElectricBlue else MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                if (selected) Icon(Icons.Rounded.CheckCircle, contentDescription = "Selected", tint = ElectricBlue, modifier = Modifier.size(16.dp))
            }
            LtrBlock {
                Text(price, style = MaterialTheme.typography.titleMedium, color = ElectricBlue, fontWeight = FontWeight.Bold)
            }
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
