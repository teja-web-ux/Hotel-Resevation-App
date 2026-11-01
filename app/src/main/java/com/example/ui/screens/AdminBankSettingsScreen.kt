package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.HotelRepository
import com.example.model.AdminBankDetails
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBankSettingsScreen(
    onBack: () -> Unit
) {
    val bankDetails by HotelRepository.bankDetails.collectAsState()
    val context = LocalContext.current

    var bankName by remember(bankDetails) { mutableStateOf(bankDetails.bankName) }
    var accountHolderName by remember(bankDetails) { mutableStateOf(bankDetails.accountHolderName) }
    var accountNumber by remember(bankDetails) { mutableStateOf(bankDetails.accountNumber) }
    var ifscCode by remember(bankDetails) { mutableStateOf(bankDetails.ifscCode) }
    var branchName by remember(bankDetails) { mutableStateOf(bankDetails.branchName) }
    var upiId by remember(bankDetails) { mutableStateOf(bankDetails.upiId) }
    var errorMsg by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bank & Payout Setup",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = PureWhite,
                shadowElevation = 16.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
                    EmeraldPillButton(
                        text = "Save & Activate Account",
                        icon = Icons.Default.Save,
                        onClick = {
                            if (bankName.isBlank() || accountHolderName.isBlank() || accountNumber.isBlank() || ifscCode.isBlank() || upiId.isBlank()) {
                                errorMsg = "Please fill in all mandatory bank details."
                                return@EmeraldPillButton
                            }
                            errorMsg = ""
                            val updated = bankDetails.copy(
                                bankName = bankName.trim(),
                                accountHolderName = accountHolderName.trim(),
                                accountNumber = accountNumber.trim(),
                                ifscCode = ifscCode.trim().uppercase(),
                                branchName = branchName.trim(),
                                upiId = upiId.trim(),
                                isVerified = true
                            )
                            HotelRepository.updateBankDetails(updated)
                            successMsg = "Bank Account details updated! All customer payments will now credit here."
                            Toast.makeText(context, "Account Saved & Activated Successfully!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "save_bank_details_button"
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .testTag("admin_bank_settings_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                    color = EmeraldContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = PureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Primary Payout Destination",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = EmeraldDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Instant Real-Time IMPS/UPI Settlement",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Surface(
                                color = EmeraldPrimary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = PureWhite,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Current Settled Balance", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("$${String.format("%,.2f", bankDetails.currentBalance)}", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Last Credit Received", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("+$${String.format("%,.2f", bankDetails.lastSettledAmount)}", style = MaterialTheme.typography.titleMedium, color = EmeraldDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Input Fields matching FormInputItem
            item {
                Text("Enter Bank Account Details", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("When a customer pays in app, money is transferred directly to this account.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            item { FormInputItem("Bank Name *", bankName, onValueChange = { bankName = it }) }
            item { FormInputItem("Beneficiary / Account Holder Name *", accountHolderName, onValueChange = { accountHolderName = it }) }
            item { FormInputItem("Account Number *", accountNumber, onValueChange = { accountNumber = it }) }
            item { FormInputItem("IFSC Code *", ifscCode, onValueChange = { ifscCode = it.uppercase() }) }
            item { FormInputItem("Branch Location", branchName, onValueChange = { branchName = it }) }
            item { FormInputItem("Hotel UPI ID (For Direct UPI & QR Pay) *", upiId, onValueChange = { upiId = it }) }

            if (errorMsg.isNotEmpty()) {
                item { Text(errorMsg, color = RedCancel, style = MaterialTheme.typography.bodySmall) }
            }
            if (successMsg.isNotEmpty()) {
                item { Text(successMsg, color = EmeraldDark, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) }
            }

            // Simulation button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val updated = bankDetails.copy(
                            currentBalance = bankDetails.currentBalance + 500.0,
                            totalSettledRevenue = bankDetails.totalSettledRevenue + 500.0,
                            lastSettledAmount = 500.0,
                            lastSettledTime = System.currentTimeMillis()
                        )
                        HotelRepository.updateBankDetails(updated)
                        Toast.makeText(context, "$500 Credited to ${updated.bankName} Account!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer, contentColor = EmeraldDark),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simulate +$500 Customer Payment", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
