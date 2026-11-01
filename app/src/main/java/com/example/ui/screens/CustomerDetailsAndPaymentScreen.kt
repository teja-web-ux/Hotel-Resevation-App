package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HotelRepository
import com.example.model.Booking
import com.example.model.HotelRoomUnit
import com.example.model.OdishaHotel
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsAndPaymentScreen(
    hotel: OdishaHotel,
    room: HotelRoomUnit,
    guestName: String,
    registrationNumber: String,
    guestEmail: String,
    guestPhone: String,
    checkInDate: String,
    checkOutDate: String,
    nights: Int,
    guestsCount: Int,
    onBack: () -> Unit,
    onPaymentSuccess: (Booking) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val bankDetails by HotelRepository.bankDetails.collectAsState()

    // Customer Details Form State
    var addressLine by remember { mutableStateOf("Saheed Nagar, Bhubaneswar") }
    var selectedIdType by remember { mutableStateOf("Aadhaar Card") }
    var idProofNumber by remember { mutableStateOf("7894-3210-9842") }
    var emergencyContactName by remember { mutableStateOf("Ramesh Mohapatra") }
    var emergencyContactPhone by remember { mutableStateOf("+91 94370 99887") }
    var specialRequests by remember { mutableStateOf("") }
    var isIdDropdownExpanded by remember { mutableStateOf(false) }

    // Payment Selection State
    var selectedPaymentMode by remember { mutableStateOf("Google Pay Scanner") } // "Google Pay Scanner", "UPI ID"
    var customerUpiId by remember { mutableStateOf("guest@okaxis") }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentErrorMessage by remember { mutableStateOf<String?>(null) }

    val basePrice = room.pricePerNight * nights
    val taxAmount = basePrice * 0.12
    val totalAmount = basePrice + taxAmount

    val idTypes = listOf("Aadhaar Card", "Passport", "Voter ID Card", "Driving License")

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customer Details & Payment",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isProcessingPayment) {
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
                        text = if (isProcessingPayment) "Processing Payment..." else "Pay ₹${String.format("%,.0f", totalAmount)} via $selectedPaymentMode",
                        enabled = !isProcessingPayment,
                        onClick = {
                            if (addressLine.isBlank()) {
                                paymentErrorMessage = "Please enter your address in Odisha."
                                return@EmeraldPillButton
                            }
                            if (idProofNumber.isBlank()) {
                                paymentErrorMessage = "Please enter your Govt ID proof number."
                                return@EmeraldPillButton
                            }

                            isProcessingPayment = true
                            paymentErrorMessage = null

                            coroutineScope.launch {
                                // Simulate UPI/Google Pay Gateway handoff
                                delay(1200)

                                // Create booking record with registration number & customer details
                                val booking = HotelRepository.createBooking(
                                    guestName = guestName,
                                    registrationNumber = registrationNumber,
                                    guestEmail = guestEmail,
                                    guestPhone = guestPhone,
                                    customerAddress = addressLine,
                                    idProofType = selectedIdType,
                                    idProofNumber = idProofNumber,
                                    hotel = hotel,
                                    roomUnit = room,
                                    checkInDate = checkInDate,
                                    checkOutDate = checkOutDate,
                                    nights = nights,
                                    guestsCount = guestsCount
                                )

                                // Process Payment with Instant Settlement to Admin Bank Account
                                val confirmed = HotelRepository.processPayment(
                                    bookingId = booking.id,
                                    paymentMethod = selectedPaymentMode,
                                    upiRef = if (selectedPaymentMode == "Google Pay Scanner") "GPAY-OD-${(10000000..99999999).random()}" else customerUpiId
                                )

                                isProcessingPayment = false
                                if (confirmed != null) {
                                    onPaymentSuccess(confirmed)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "pay_and_confirm_booking_btn"
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Header Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceLight,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = hotel.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${room.roomNumber} (${room.roomType})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "₹${String.format("%,.0f", totalAmount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Customer: $guestName", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("Reg No: $registrationNumber", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EmeraldDark)
                        }
                    }
                }
            }

            // Error Message
            if (paymentErrorMessage != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = RedCancelBg,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = paymentErrorMessage ?: "",
                            color = RedCancel,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // SECTION 1: CUSTOMER DETAILS FORM (User requirement)
            item {
                Text(
                    text = "Customer Details Form",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Fill in your residential address and government identification details",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            item {
                OutlinedTextField(
                    value = addressLine,
                    onValueChange = { addressLine = it },
                    label = { Text("Customer Residential Address in Odisha *") },
                    placeholder = { Text("Plot No, Street, City, Odisha - PIN") },
                    leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null, tint = EmeraldPrimary) },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_address_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // ID Proof Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = isIdDropdownExpanded,
                        onExpandedChange = { isIdDropdownExpanded = !isIdDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedIdType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Govt ID Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isIdDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = BorderMedium
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = isIdDropdownExpanded,
                            onDismissRequest = { isIdDropdownExpanded = false }
                        ) {
                            idTypes.forEach { idType ->
                                DropdownMenuItem(
                                    text = { Text(idType) },
                                    onClick = {
                                        selectedIdType = idType
                                        isIdDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // ID Proof Number
                    OutlinedTextField(
                        value = idProofNumber,
                        onValueChange = { idProofNumber = it },
                        label = { Text("Govt ID Number *") },
                        placeholder = { Text("XXXX-XXXX-XXXX") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("customer_id_number_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderMedium
                        )
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = emergencyContactName,
                        onValueChange = { emergencyContactName = it },
                        label = { Text("Emergency Contact Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderMedium
                        )
                    )

                    OutlinedTextField(
                        value = emergencyContactPhone,
                        onValueChange = { emergencyContactPhone = it },
                        label = { Text("Emergency Contact Phone") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderMedium
                        )
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = specialRequests,
                    onValueChange = { specialRequests = it },
                    label = { Text("Special Requests (Optional)") },
                    placeholder = { Text("E.g. Early check-in, ground floor room, airport pickup") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium
                    )
                )
            }

            // SECTION 2: PAYMENT METHOD (Google Pay Scanner or UPI)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Pay seamlessly via Google Pay scanner or direct UPI ID",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            // Payment Mode Selector Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Google Pay Scanner Option
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                2.dp,
                                if (selectedPaymentMode == "Google Pay Scanner") EmeraldPrimary else BorderMedium,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedPaymentMode = "Google Pay Scanner" }
                            .testTag("select_gpay_scanner_option"),
                        color = if (selectedPaymentMode == "Google Pay Scanner") EmeraldLight else PureWhite
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Google Pay Scanner", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("Scan & Pay QR", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    // UPI ID Option
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                2.dp,
                                if (selectedPaymentMode == "UPI ID") EmeraldPrimary else BorderMedium,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedPaymentMode = "UPI ID" }
                            .testTag("select_upi_id_option"),
                        color = if (selectedPaymentMode == "UPI ID") EmeraldLight else PureWhite
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("UPI / VPA ID", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("Instant App Intent", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // GOOGLE PAY SCANNER QR VIEW
            if (selectedPaymentMode == "Google Pay Scanner") {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BorderMedium, RoundedCornerShape(16.dp)),
                        color = SurfaceLight
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Google Pay Scanner QR",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Simulated Visual QR Box
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PureWhite)
                                    .border(2.dp, EmeraldPrimary, RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode2,
                                        contentDescription = "Google Pay QR",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(120.dp)
                                    )
                                    Text(
                                        text = "Scan with Google Pay",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = EmeraldPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Payable: ₹${String.format("%,.0f", totalAmount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldPrimary
                            )

                            Text(
                                text = "Merchant UPI: ${bankDetails.upiId}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                // UPI ID ENTRY VIEW
                item {
                    OutlinedTextField(
                        value = customerUpiId,
                        onValueChange = { customerUpiId = it },
                        label = { Text("Your UPI ID / VPA *") },
                        placeholder = { Text("e.g. yourname@okaxis or yourname@oksbi") },
                        leadingIcon = { Icon(Icons.Outlined.Payment, contentDescription = null, tint = EmeraldPrimary) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_upi_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderMedium
                        )
                    )
                }
            }

            // Real-time Settlement Guarantee Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = EmeraldLight,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Real-Time Account Settlement",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Paid funds credit instantly to ${bankDetails.bankName} (A/C: *${bankDetails.accountNumber.takeLast(4)}).",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
